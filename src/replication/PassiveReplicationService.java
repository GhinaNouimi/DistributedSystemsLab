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
        int maxAttempts = followers.size() + 1;
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                System.out.println("Leader handling operation...");

                String leaderResponse =
                        leader.processRequest(operation);

                if (isFailedResponse(leaderResponse)) {
                    throw new RuntimeException(leaderResponse);
                }

                System.out.println(leaderResponse);

                replicateToFollowers(operation);

                return;

            } catch (Exception exception) {
                attempts++;

                System.out.println(
                        "Leader failed -> starting failover"
                );

                if (attempts >= maxAttempts) {
                    throw new RuntimeException(
                            "Passive replication failed: no available leader"
                    );
                }

                promoteNewLeader();
            }
        }
    }

    private void replicateToFollowers(String operation) {
        for (RemoteTaskService follower : followers) {
            try {
                System.out.println(
                        "Replicating to "
                                + follower.getServerName()
                );

                String response =
                        follower.processRequest(
                                "REPLICA COPY: " + operation
                        );

                if (isFailedResponse(response)) {
                    System.out.println(
                            "Follower replication failed"
                    );
                }

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
            System.out.println("New leader promoted");
        }
    }

    private boolean isFailedResponse(String response) {
        return response != null
                && response.toLowerCase().contains("failed");
    }
}