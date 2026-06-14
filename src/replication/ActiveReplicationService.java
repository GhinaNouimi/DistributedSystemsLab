package replication;

import remote.RemoteTaskService;

import java.util.List;

public class ActiveReplicationService {

    private final List<RemoteTaskService> replicas;

    public ActiveReplicationService(
            List<RemoteTaskService> replicas
    ) {
        this.replicas = replicas;
    }

    public void writeToAllReplicas(String operation) {
        for (RemoteTaskService replica : replicas) {
            new Thread(() -> {
                try {
                    String response =
                            replica.processRequest(
                                    "ACTIVE REPLICA: "
                                            + operation
                            );

                    System.out.println(response);

                } catch (Exception exception) {
                    System.out.println(
                            "Replica failed during active replication"
                    );
                }
            }).start();
        }
    }
}