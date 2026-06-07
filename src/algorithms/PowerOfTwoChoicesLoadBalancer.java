package algorithms;

import model.ServerNode;

import java.util.List;
import java.util.Random;

public class PowerOfTwoChoicesLoadBalancer {

    private final List<ServerNode> servers;
    private final Random random;

    public PowerOfTwoChoicesLoadBalancer(List<ServerNode> servers) {
        if (servers == null || servers.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two servers are required."
            );
        }

        this.servers = servers;
        this.random = new Random();
    }

    public ServerNode getNextServer() {

        ServerNode firstServer =
                servers.get(random.nextInt(servers.size()));

        ServerNode secondServer =
                servers.get(random.nextInt(servers.size()));

        while (firstServer == secondServer) {
            secondServer =
                    servers.get(random.nextInt(servers.size()));
        }

        System.out.println(
                "Comparing: "
                        + firstServer.getName()
                        + " (" + firstServer.getActiveConnections() + ")"
                        + " vs "
                        + secondServer.getName()
                        + " (" + secondServer.getActiveConnections() + ")"
        );

        ServerNode selectedServer =
                firstServer.getActiveConnections()
                        <= secondServer.getActiveConnections()
                        ? firstServer
                        : secondServer;

        System.out.println(
                "Selected -> "
                        + selectedServer.getName()
        );

        selectedServer.incrementConnections();

        return selectedServer;
    }}