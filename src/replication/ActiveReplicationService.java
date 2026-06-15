package replication;

import remote.RemoteTaskService;

import java.util.ArrayList;
import java.util.List;

public class ActiveReplicationService {

    private final List<RemoteTaskService> replicas;

    public ActiveReplicationService(
            List<RemoteTaskService> replicas
    ) {
        this.replicas = replicas;
    }

    public void writeToAllReplicas(String operation) {
        List<Thread> threads = new ArrayList<>();

        for (RemoteTaskService replica : replicas) {
            Thread thread = new Thread(() -> {
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
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                System.out.println(
                        "Active replication interrupted"
                );
            }
        }
    }
}