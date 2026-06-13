package RMI;

import java.rmi.RemoteException;
import java.util.List;

public class RMIAdaptiveLoadBalancer {

    private final List<RemoteTaskService> servers;

    public RMIAdaptiveLoadBalancer(
            List<RemoteTaskService> servers
    ) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Servers list cannot be empty."
            );
        }

        this.servers = servers;
    }

    public synchronized RemoteTaskService getNextServer()
            throws RemoteException {

        RemoteTaskService selectedServer = null;
        double bestScore = Double.MAX_VALUE;

        for (RemoteTaskService server : servers) {

            if (!server.isHealthy()) {
                System.out.println(
                        server.getServerName()
                                + " is DOWN -> skipped"
                );
                continue;
            }

            int activeRequests =
                    server.getActiveRequests();

            long averageResponseTime =
                    server.getAverageResponseTime();

            int failedRequests =
                    server.getFailedRequests();

            double score =
                    activeRequests * 10
                            + averageResponseTime * 0.01
                            + failedRequests * 5;

            System.out.println(
                    server.getServerName()
                            + " | active="
                            + activeRequests
                            + " | avgResponse="
                            + averageResponseTime
                            + "ms"
                            + " | failures="
                            + failedRequests
                            + " | score="
                            + score
            );

            if (score < bestScore) {
                bestScore = score;
                selectedServer = server;
            }
        }

        if (selectedServer == null) {
            throw new RuntimeException(
                    "No healthy servers available."
            );
        }

        System.out.println(
                "Adaptive selected -> "
                        + selectedServer.getServerName()
                        + " with score="
                        + bestScore
        );

        selectedServer.startRequest();

        return selectedServer;
    }
}