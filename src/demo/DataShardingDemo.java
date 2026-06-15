package demo;

import heartbeat.HeartbeatMonitor;
import remote.RemoteTaskService;
import sharding.ConsistentHashShardResolver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class DataShardingDemo {

    public static void main(String[] args) {
        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            List<String> registryNames = List.of(
                    "ServerA", "ServerB", "ServerC", "ServerD", "ServerE"
            );

            List<RemoteTaskService> servers = new ArrayList<>();

            for (String registryName : registryNames) {
                servers.add(
                        (RemoteTaskService) registry.lookup(registryName)
                );
            }

            HeartbeatMonitor monitor = new HeartbeatMonitor();

            List<RemoteTaskService> healthyServers =
                    monitor.getHealthyServers(servers);

            if (healthyServers.isEmpty()) {
                System.out.println(
                        "No healthy servers available for data sharding"
                );
                return;
            }

            System.out.println();
            System.out.println("========== DATA SHARDING DEMO ==========");

            ConsistentHashShardResolver shardResolver =
                    new ConsistentHashShardResolver(healthyServers);

            String[] keys = {
                    "patient-1001",
                    "patient-1002",
                    "patient-1003",
                    "patient-1004",
                    "appointment-2001",
                    "appointment-2002",
                    "appointment-2003",
                    "invoice-3001",
                    "invoice-3002",
                    "invoice-3003",
                    "lab-result-4001",
                    "lab-result-4002",
                    "lab-result-4003",
                    "doctor-5001",
                    "doctor-5002"
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

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}