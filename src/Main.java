import algorithms.*;
import model.ServerNode;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        runRoundRobinDemo();
        runLeastConnectionsDemo();
        runHealthAwareDemo();
        runWeightedRoundRobinDemo();
        runPowerOfTwoChoicesDemo();
        runConsistentHashDemo();
    }

    private static void runRoundRobinDemo() {
        printTitle("Round Robin Load Balancer");

        List<ServerNode> servers = createServers();

        RoundRobinLoadBalancer loadBalancer =
                new RoundRobinLoadBalancer(servers);

        for (int requestNumber = 1; requestNumber <= 10; requestNumber++) {
            ServerNode selectedServer = loadBalancer.getNextServer();

            System.out.println(
                    "Request " + requestNumber +
                            " -> " + selectedServer.getName()
            );
        }

        printServersState(servers);
    }

    private static void runLeastConnectionsDemo() {
        printTitle("Least Connections Load Balancer");

        List<ServerNode> servers = createServers();

        servers.get(0).incrementConnections();
        servers.get(0).incrementConnections();
        servers.get(0).incrementConnections();

        servers.get(1).incrementConnections();

        LeastConnectionsLoadBalancer loadBalancer =
                new LeastConnectionsLoadBalancer(servers);

        ServerNode selectedServer = loadBalancer.getNextServer();

        System.out.println(
                "Selected Server -> " + selectedServer.getName()
        );

        printServersState(servers);
    }

    private static void runHealthAwareDemo() {
        printTitle("Health-Aware Load Balancer");

        List<ServerNode> servers = createServers();

        servers.get(0).incrementConnections();
        servers.get(0).incrementConnections();

        servers.get(1).setHealthy(false);

        HealthAwareLoadBalancer loadBalancer =
                new HealthAwareLoadBalancer(servers);

        ServerNode selectedServer =
                loadBalancer.getNextHealthyServer();

        System.out.println(
                "Selected Server -> " + selectedServer.getName()
        );

        printServersState(servers);
    }

    private static void runWeightedRoundRobinDemo() {
        printTitle("Weighted Round Robin Load Balancer");

        List<ServerNode> servers = List.of(
                new ServerNode("Server A", 4),
                new ServerNode("Server B", 2),
                new ServerNode("Server C", 1),
                new ServerNode("Server D", 3)
        );

        WeightedRoundRobinLoadBalancer loadBalancer =
                new WeightedRoundRobinLoadBalancer(servers);

        for (int requestNumber = 1; requestNumber <= 20; requestNumber++) {
            ServerNode selectedServer = loadBalancer.getNextServer();

            System.out.println(
                    "Request " + requestNumber +
                            " -> " + selectedServer.getName()
            );
        }

        printServersState(servers);
    }

    private static void runPowerOfTwoChoicesDemo() {
        printTitle("Power of Two Choices Load Balancer");

        List<ServerNode> servers = createServers();

        servers.get(0).incrementConnections();
        servers.get(0).incrementConnections();
        servers.get(0).incrementConnections();
        servers.get(0).incrementConnections();

        servers.get(1).incrementConnections();

        PowerOfTwoChoicesLoadBalancer loadBalancer =
                new PowerOfTwoChoicesLoadBalancer(servers);

        for (int requestNumber = 1; requestNumber <= 10; requestNumber++) {
            ServerNode selectedServer = loadBalancer.getNextServer();

            System.out.println(
                    "Request " + requestNumber +
                            " -> " + selectedServer.getName()
            );
        }

        printServersState(servers);
    }

    private static void runConsistentHashDemo() {

        printTitle("Consistent Hashing");

        ConsistentHashLoadBalancer loadBalancer =
                new ConsistentHashLoadBalancer();

        loadBalancer.addServer(
                new ServerNode("Server A")
        );

        loadBalancer.addServer(
                new ServerNode("Server B")
        );

        loadBalancer.addServer(
                new ServerNode("Server C")
        );

//        loadBalancer.addServer(
//                new ServerNode("Server D")
//        );
//        String[] users = {
//                "Ali", "Sara", "Ahmad", "Omar", "Lina", "Mohammad", "Rama",
//                "Nour", "Huda", "Khaled", "Maya", "Yazan", "Sami", "Ruba",
//                "Tala", "Fadi", "Mona", "Hassan", "Dima", "Karam",
//                "User1", "User2", "User3", "User4", "User5",
//                "User6", "User7", "User8", "User9", "User10"
//        };

        String[] users = {
                "Ali",
                "Sara",
                "Ahmad",
                "Omar",
                "Lina",
                "Mohammad",
                "Rama"
        };

        System.out.println();

        for (String user : users) {

            ServerNode server =
                    loadBalancer.getServer(user);

            System.out.println(
                    user
                            + " -> "
                            + server.getName()
            );
        }

        System.out.println();
    }

    private static List<ServerNode> createServers() {
        return List.of(
                new ServerNode("Server A"),
                new ServerNode("Server B"),
                new ServerNode("Server C")
        );
    }

    private static void printServersState(List<ServerNode> servers) {
        System.out.println();
        System.out.println("Servers State:");

        for (ServerNode server : servers) {
            System.out.println(server);
        }

        System.out.println();
    }

    private static void printTitle(String title) {
        System.out.println("====================================");
        System.out.println(title);
        System.out.println("====================================");
    }
}