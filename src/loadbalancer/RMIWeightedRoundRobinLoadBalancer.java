package loadbalancer;

import core.model.RMIWeightedServerEntry;
import remote.RemoteTaskService;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RMIWeightedRoundRobinLoadBalancer {

    private final List<RemoteTaskService> weightedServers;
    private int currentIndex;

    public RMIWeightedRoundRobinLoadBalancer(
            List<RMIWeightedServerEntry> serverEntries
    ) throws RemoteException {

        if (serverEntries == null || serverEntries.isEmpty()) {
            throw new IllegalArgumentException(
                    "Servers list cannot be empty."
            );
        }

        this.weightedServers = new ArrayList<>();

        for (RMIWeightedServerEntry entry : serverEntries) {
            RemoteTaskService server = entry.getServer();

            for (int i = 0; i < entry.getWeight(); i++) {
                weightedServers.add(server);
            }
        }

        if (weightedServers.isEmpty()) {
            throw new RuntimeException(
                    "No weighted servers available."
            );
        }

        this.currentIndex = 0;
    }

    public synchronized RemoteTaskService getNextServer()
            throws RemoteException {

        int checkedServers = 0;

        while (checkedServers < weightedServers.size()) {
            RemoteTaskService selectedServer =
                    weightedServers.get(currentIndex);

            currentIndex =
                    (currentIndex + 1) % weightedServers.size();

            checkedServers++;

            if (selectedServer.isHealthy()) {
                System.out.println(
                        "Weighted Round Robin selected -> "
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
                "No healthy weighted servers available."
        );
    }
}