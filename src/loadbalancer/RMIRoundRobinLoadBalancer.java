package loadbalancer;

import remote.RemoteTaskService;

import java.rmi.RemoteException;
import java.util.List;

public class RMIRoundRobinLoadBalancer {

    private final List<RemoteTaskService> servers;
    private int currentIndex;

    public RMIRoundRobinLoadBalancer(
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

    public synchronized RemoteTaskService getNextServer()
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
                        "Round Robin selected -> "
                                + selectedServer.getServerName()
                );

                selectedServer.startRequest();

                return selectedServer;
            }

            System.out.println(
                    selectedServer.getServerName()
                            + " is DOWN -> skipped"
            );
        }

        throw new RuntimeException(
                "No healthy RMI servers available."
        );
    }
}