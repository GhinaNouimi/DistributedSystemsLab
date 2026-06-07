package RMI;

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

    public RemoteTaskService getNextServer()
            throws RemoteException {

        RemoteTaskService selectedServer =
                servers.get(currentIndex);

        System.out.println(
                "Round Robin selected -> "
                        + selectedServer.getServerName()
        );

        currentIndex =
                (currentIndex + 1) % servers.size();

        return selectedServer;
    }
}