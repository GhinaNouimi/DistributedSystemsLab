package loadbalancer;

import remote.RemoteTaskService;

import java.rmi.RemoteException;
import java.util.List;

public class RMIHealthAwareLoadBalancer {

    private final List<RemoteTaskService> servers;
    private int currentIndex;

    public RMIHealthAwareLoadBalancer(
            List<RemoteTaskService> servers
    ) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Servers list cannot be empty."
            );
        }

        this.servers = servers;
        this.currentIndex = 0;
    }

    public RemoteTaskService getNextHealthyServer()
            throws RemoteException {

        int checkedServers = 0;

        while (checkedServers < servers.size()) {
            RemoteTaskService selectedServer =
                    servers.get(currentIndex);

            currentIndex =
                    (currentIndex + 1) % servers.size();

            checkedServers++;

            if (selectedServer.isHealthy()) {
                System.out.println(
                        "Health check passed -> "
                                + selectedServer.getServerName()
                );

                return selectedServer;
            }

            System.out.println(
                    "Health check failed -> "
                            + selectedServer.getServerName()
            );
        }

        throw new RuntimeException(
                "No healthy RMI servers available."
        );
    }
}