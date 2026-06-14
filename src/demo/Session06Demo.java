package demo;

import heartbeat.HeartbeatMonitor;
import replication.ActiveReplicationService;
import replication.PassiveReplicationService;
import remote.RemoteTaskService;
import sharding.ConsistentHashShardResolver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Session06Demo {

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
                RemoteTaskService server =
                        (RemoteTaskService)
                                registry.lookup(registryName);

                servers.add(server);

                System.out.println(
                        registryName + " loaded from RMI Registry"
                );
            }

            System.out.println();
            System.out.println("========== HEARTBEAT ==========");
            HeartbeatMonitor heartbeatMonitor =
                    new HeartbeatMonitor();

            List<RemoteTaskService> healthyServers =
                    heartbeatMonitor.getHealthyServers(servers);

            System.out.println();
            System.out.println(
                    "Healthy servers count = "
                            + healthyServers.size()
            );

            System.out.println();
            System.out.println("========== DATA SHARDING ==========");
            ConsistentHashShardResolver shardResolver =
                    new ConsistentHashShardResolver(healthyServers);

            String[] keys = {
                    "user-100",
                    "user-200",
                    "order-300",
                    "invoice-400",
                    "patient-500"
            };

            for (String key : keys) {
                RemoteTaskService shard =
                        shardResolver.resolveShard(key);

                System.out.println(
                        key
                                + " stored on shard -> "
                                + shard.getServerName()
                );
            }

            System.out.println();
            System.out.println("========== PASSIVE REPLICATION ==========");

            RemoteTaskService leader =
                    healthyServers.get(0);

            List<RemoteTaskService> followers =
                    healthyServers.subList(
                            1,
                            healthyServers.size()
                    );

            PassiveReplicationService passiveReplicationService =
                    new PassiveReplicationService(
                            leader,
                            followers
                    );

            passiveReplicationService.write(
                    "Update patient medical record"
            );

            System.out.println();
            System.out.println("========== ACTIVE REPLICATION ==========");

            ActiveReplicationService activeReplicationService =
                    new ActiveReplicationService(
                            healthyServers
                    );

            activeReplicationService.writeToAllReplicas(
                    "Synchronize emergency alert"
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}