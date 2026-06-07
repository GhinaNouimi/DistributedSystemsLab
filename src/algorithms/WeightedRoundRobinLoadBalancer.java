package algorithms;

import model.ServerNode;

import java.util.ArrayList;
import java.util.List;

public class WeightedRoundRobinLoadBalancer {

    private final List<ServerNode> weightedServers;
    private int currentIndex;

    public WeightedRoundRobinLoadBalancer(List<ServerNode> servers) {

        weightedServers = new ArrayList<>();

        for (ServerNode server : servers) {

            for (int i = 0; i < server.getWeight(); i++) {
                weightedServers.add(server);
            }
        }

        currentIndex = 0;
    }

    public ServerNode getNextServer() {

        ServerNode selectedServer =
                weightedServers.get(currentIndex);

        currentIndex =
                (currentIndex + 1) % weightedServers.size();

        selectedServer.incrementConnections();

        return selectedServer;
    }
}