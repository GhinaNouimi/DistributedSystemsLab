package demo;

import heartbeat.HeartbeatMonitor;
import remote.RemoteTaskService;
import replication.ActiveReplicationService;
import replication.PassiveReplicationService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ReplicationDemo {

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
                        "No healthy servers available for replication demo"
                );
                return;
            }

            if (healthyServers.size() < 2) {
                System.out.println(
                        "Passive replication needs at least 2 healthy servers"
                );
                return;
            }

            System.out.println();
            System.out.println("========== PASSIVE REPLICATION DEMO ==========");

            RemoteTaskService leader = healthyServers.get(0);

            List<RemoteTaskService> followers =
                    new ArrayList<>(
                            healthyServers.subList(1, healthyServers.size())
                    );

            PassiveReplicationService passiveReplicationService =
                    new PassiveReplicationService(leader, followers);

            passiveReplicationService.write(
                    "Update patient medical record"
            );

            System.out.println();
            System.out.println("========== ACTIVE REPLICATION DEMO ==========");

            ActiveReplicationService activeReplicationService =
                    new ActiveReplicationService(healthyServers);

            activeReplicationService.writeToAllReplicas(
                    "Broadcast emergency patient alert"
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}