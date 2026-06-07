package algorithms;

import model.ServerNode;

import java.util.List;

public class HealthAwareLoadBalancer {

    private final List<ServerNode> servers;

    public HealthAwareLoadBalancer(List<ServerNode> servers) {
        this.servers = servers;
    }

    public ServerNode getNextHealthyServer() {

        ServerNode selectedServer = null;

        for (ServerNode server : servers) {

            if (!server.isHealthy()) {
                continue;
            }

            if (selectedServer == null ||
                    server.getActiveConnections()
                            < selectedServer.getActiveConnections()) {

                selectedServer = server;
            }
        }

        if (selectedServer == null) {
            throw new RuntimeException(
                    "No healthy servers available."
            );
        }

        selectedServer.incrementConnections();

        return selectedServer;
    }
}