package demo;

import heartbeat.HeartbeatMonitor;
import loadbalancer.RMIRoundRobinLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RuntimeFailureDemo {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            RemoteTaskService serverA =
                    (RemoteTaskService) registry.lookup("ServerA");

            RemoteTaskService serverC =
                    (RemoteTaskService) registry.lookup("ServerC");

            RemoteTaskService serverE =
                    (RemoteTaskService) registry.lookup("ServerE");

            serverA.setHealthy(true);
            serverC.setHealthy(true);
            serverE.setHealthy(true);

            List<RemoteTaskService> servers =
                    List.of(serverA, serverC, serverE);

            HeartbeatMonitor monitor =
                    new HeartbeatMonitor();

            System.out.println("========== RUNTIME FAILURE DEMO ==========");
            System.out.println("Scenario: ServerC is healthy, then fails during runtime");
            System.out.println();

            System.out.println("Step 1: Initial heartbeat check");
            monitor.getHealthyServers(servers);

            System.out.println();
            System.out.println("Step 2: Send requests before failure");

            RMIRoundRobinLoadBalancer loadBalancer =
                    new RMIRoundRobinLoadBalancer(servers);

            for (int i = 1; i <= 3; i++) {
                RemoteTaskService selectedServer = null;

                try {
                    selectedServer =
                            loadBalancer.getNextServer();

                    String response =
                            selectedServer.processRequest(
                                    "Before failure request " + i
                            );

                    System.out.println(response);

                } finally {
                    if (selectedServer != null) {
                        selectedServer.finishRequest();
                    }
                }
            }

            System.out.println();
            System.out.println("Step 3: ServerC fails during runtime");
            serverC.setHealthy(false);

            System.out.println();
            System.out.println("Step 4: Heartbeat detects failure");
            monitor.getHealthyServers(servers);

            System.out.println();
            System.out.println("Step 5: Load balancer skips failed ServerC");

            for (int i = 1; i <= 6; i++) {
                RemoteTaskService selectedServer = null;

                try {
                    selectedServer =
                            loadBalancer.getNextServer();

                    String response =
                            selectedServer.processRequest(
                                    "After failure request " + i
                            );

                    System.out.println(response);

                } finally {
                    if (selectedServer != null) {
                        selectedServer.finishRequest();
                    }
                }
            }

            System.out.println();
            System.out.println("Runtime failure scenario finished successfully.");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}