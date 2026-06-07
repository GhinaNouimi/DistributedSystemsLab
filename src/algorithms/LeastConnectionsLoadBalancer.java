package algorithms;

import model.ServerNode;

import java.util.List;

public class LeastConnectionsLoadBalancer {

    private final List<ServerNode> servers;

    public LeastConnectionsLoadBalancer(List<ServerNode> servers) {
        this.servers = servers;
    }

    public ServerNode getNextServer() {

        ServerNode selectedServer = servers.get(0);

        for (ServerNode server : servers) {

            if (server.getActiveConnections()
                    < selectedServer.getActiveConnections()) {

                selectedServer = server;
            }
        }

        selectedServer.incrementConnections();

        return selectedServer;
    }
}