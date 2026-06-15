package demo;

import heartbeat.HeartbeatMonitor;
import loadbalancer.RMIAdaptiveLoadBalancer;
import remote.RemoteTaskService;
import sharding.ConsistentHashShardResolver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class PartialFailureDemo {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            List<String> registryNames = List.of(
                    "ServerA",
                    "ServerB",
                    "ServerC",
                    "ServerD",
                    "ServerE"
            );

            List<RemoteTaskService> servers =
                    new ArrayList<>();

            for (String registryName : registryNames) {
                servers.add(
                        (RemoteTaskService)
                                registry.lookup(registryName)
                );
            }

            System.out.println("========== PARTIAL FAILURE DEMO ==========");
            System.out.println("Scenario: ServerB and ServerD are DOWN");
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

            if (healthyServers.isEmpty()) {
                System.out.println(
                        "No healthy servers available. System cannot continue."
                );
                return;
            }

            System.out.println();
            System.out.println("========== LOAD BALANCING TEST ==========");

            RMIAdaptiveLoadBalancer loadBalancer =
                    new RMIAdaptiveLoadBalancer(servers);

            for (int i = 1; i <= 5; i++) {
                RemoteTaskService selectedServer = null;

                try {
                    selectedServer =
                            loadBalancer.getNextServer();

                    String response =
                            selectedServer.processRequest(
                                    "Partial failure request " + i
                            );

                    System.out.println(response);

                } finally {
                    if (selectedServer != null) {
                        selectedServer.finishRequest();
                    }
                }
            }

            System.out.println();
            System.out.println("========== SHARDING TEST ==========");

            ConsistentHashShardResolver shardResolver =
                    new ConsistentHashShardResolver(healthyServers);

            String[] keys = {
                    "patient-1001",
                    "appointment-2001",
                    "invoice-3001",
                    "lab-result-4001",
                    "doctor-5001"
            };

            for (String key : keys) {
                RemoteTaskService shard =
                        shardResolver.resolveShard(key);

                System.out.println(
                        key
                                + " assigned to shard -> "
                                + shard.getServerName()
                );
            }

            System.out.println();
            System.out.println(
                    "Partial failure scenario finished successfully."
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}