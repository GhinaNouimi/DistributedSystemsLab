package demo;

import faulttolerance.FaultTolerantRequestExecutor;
import heartbeat.HeartbeatMonitor;
import remote.RemoteTaskService;
import sharding.ConsistentHashShardResolver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class AllServersDownDemo {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            List<String> registryNames = List.of(
                    "ServerA", "ServerB", "ServerC", "ServerD", "ServerE"
            );

            List<RemoteTaskService> servers =
                    new ArrayList<>();

            for (String registryName : registryNames) {
                RemoteTaskService server =
                        (RemoteTaskService) registry.lookup(registryName);

                server.setHealthy(false);
                servers.add(server);
            }

            System.out.println("========== ALL SERVERS DOWN DEMO ==========");
            System.out.println("Scenario: All servers are DOWN");
            System.out.println();

            HeartbeatMonitor monitor =
                    new HeartbeatMonitor();

            List<RemoteTaskService> healthyServers =
                    monitor.getHealthyServers(servers);

            System.out.println();
            System.out.println(
                    "Healthy servers count = "
                            + healthyServers.size()
            );

            System.out.println();
            System.out.println("========== SHARDING TEST ==========");

            if (healthyServers.isEmpty()) {
                System.out.println(
                        "No healthy servers available for sharding."
                );
            } else {
                ConsistentHashShardResolver shardResolver =
                        new ConsistentHashShardResolver(healthyServers);

                RemoteTaskService shard =
                        shardResolver.resolveShard("patient-1001");

                System.out.println(
                        "patient-1001 assigned to shard -> "
                                + shard.getServerName()
                );
            }

            System.out.println();
            System.out.println("========== FAULT TOLERANCE TEST ==========");

            FaultTolerantRequestExecutor executor =
                    new FaultTolerantRequestExecutor(servers);

            String result =
                    executor.execute(
                            "Critical request when all servers are down"
                    );

            System.out.println(result);

            System.out.println();
            System.out.println(
                    "IMPORTANT: All servers were intentionally marked as DOWN."
            );
            System.out.println(
                    "Restart RMIServerLauncher before running the next demos."
            );

            System.out.println();
            System.out.println(
                    "All servers down scenario finished safely."
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}