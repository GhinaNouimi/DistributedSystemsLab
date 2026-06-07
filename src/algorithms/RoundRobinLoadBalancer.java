package algorithms;

import model.ServerNode;

import java.util.List;

public class RoundRobinLoadBalancer {

    private final List<ServerNode> servers;
    private int currentIndex;

    public RoundRobinLoadBalancer(List<ServerNode> servers) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException("Servers list cannot be empty.");
        }

        this.servers = servers;
        this.currentIndex = 0;
    }

    public ServerNode getNextServer() {
        ServerNode selectedServer = servers.get(currentIndex);

        currentIndex = (currentIndex + 1) % servers.size();

        selectedServer.incrementConnections();

        return selectedServer;
    }
}