package RMI;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RMIPowerOfTwoChoicesLoadBalancer {

    private final List<RemoteTaskService> servers;
    private final Random random;

    public RMIPowerOfTwoChoicesLoadBalancer(
            List<RemoteTaskService> servers
    ) {
        if (servers == null || servers.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two servers are required."
            );
        }

        this.servers = servers;
        this.random = new Random();
    }

    public synchronized RemoteTaskService getNextServer()
            throws RemoteException {

        List<RemoteTaskService> healthyServers =
                new ArrayList<>();

        for (RemoteTaskService server : servers) {
            if (server.isHealthy()) {
                healthyServers.add(server);
            } else {
                System.out.println(
                        server.getServerName()
                                + " is DOWN -> skipped"
                );
            }
        }

        if (healthyServers.size() < 2) {
            throw new RuntimeException(
                    "At least two healthy servers are required."
            );
        }

        RemoteTaskService firstServer =
                healthyServers.get(
                        random.nextInt(healthyServers.size())
                );

        RemoteTaskService secondServer =
                healthyServers.get(
                        random.nextInt(healthyServers.size())
                );

        while (firstServer == secondServer) {
            secondServer =
                    healthyServers.get(
                            random.nextInt(healthyServers.size())
                    );
        }

        System.out.println(
                "Comparing: "
                        + firstServer.getServerName()
                        + " ("
                        + firstServer.getActiveRequests()
                        + " active) vs "
                        + secondServer.getServerName()
                        + " ("
                        + secondServer.getActiveRequests()
                        + " active)"
        );

        RemoteTaskService selectedServer =
                firstServer.getActiveRequests()
                        <= secondServer.getActiveRequests()
                        ? firstServer
                        : secondServer;

        System.out.println(
                "Power of Two selected -> "
                        + selectedServer.getServerName()
        );

        selectedServer.startRequest();

        return selectedServer;
    }
}