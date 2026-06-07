package RMI;

import java.rmi.RemoteException;
import java.util.List;

public class RMILeastConnectionsLoadBalancer {

    private final List<RemoteTaskService> servers;

    public RMILeastConnectionsLoadBalancer(
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

        for (RemoteTaskService server : servers) {

            if (!server.isHealthy()) {

                System.out.println(
                        server.getServerName()
                                + " is DOWN"
                );

                continue;
            }

            if (selectedServer == null ||
                    server.getActiveRequests()
                            < selectedServer.getActiveRequests()) {
                selectedServer = server;
            }
        }

        if (selectedServer == null) {
            throw new RuntimeException(
                    "No healthy servers available."
            );
        }

        System.out.println(
                "Least Connections selected -> "
                        + selectedServer.getServerName()
                        + " before reservation ("
                        + selectedServer.getActiveRequests()
                        + " active requests)"
        );

        selectedServer.startRequest();

        System.out.println(
                selectedServer.getServerName()
                        + " after reservation ("
                        + selectedServer.getActiveRequests()
                        + " active requests)"
        );

        return selectedServer;
    }
}