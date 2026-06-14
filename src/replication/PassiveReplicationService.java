package replication;

import remote.RemoteTaskService;

import java.util.ArrayList;
import java.util.List;

public class PassiveReplicationService {

    private RemoteTaskService leader;
    private final List<RemoteTaskService> followers;

    public PassiveReplicationService(
            RemoteTaskService leader,
            List<RemoteTaskService> followers
    ) {
        this.leader = leader;
        this.followers = new ArrayList<>(followers);
    }

    public void write(String operation) {
        try {
            System.out.println(
                    "Leader handling operation..."
            );

            String leaderResponse =
                    leader.processRequest(operation);

            System.out.println(leaderResponse);

            replicateToFollowers(operation);

        } catch (Exception exception) {
            System.out.println(
                    "Leader failed -> starting failover"
            );

            promoteNewLeader();

            write(operation);
        }
    }

    private void replicateToFollowers(String operation) {
        for (RemoteTaskService follower : followers) {
            try {
                System.out.println(
                        "Replicating to "
                                + follower.getServerName()
                );

                follower.processRequest(
                        "REPLICA COPY: " + operation
                );

            } catch (Exception exception) {
                System.out.println(
                        "Follower replication failed"
                );
            }
        }
    }

    private void promoteNewLeader() {
        if (followers.isEmpty()) {
            throw new RuntimeException(
                    "No followers available for failover"
            );
        }

        leader = followers.remove(0);

        try {
            System.out.println(
                    leader.getServerName()
                            + " promoted as new Leader"
            );
        } catch (Exception exception) {
            System.out.println(
                    "New leader promoted"
            );
        }
    }
}