package demo;

import heartbeat.HeartbeatMonitor;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class HeartbeatDemo {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            List<String> registryNames = List.of(
                    "ServerA", "ServerB", "ServerC", "ServerD", "ServerE"
            );

            List<RemoteTaskService> servers = new ArrayList<>();

            for (String registryName : registryNames) {
                servers.add((RemoteTaskService) registry.lookup(registryName));
            }

            System.out.println("========== HEARTBEAT DEMO ==========");

            HeartbeatMonitor monitor = new HeartbeatMonitor();
            List<RemoteTaskService> healthyServers =
                    monitor.getHealthyServers(servers);

            System.out.println();
            System.out.println("Healthy servers count = " + healthyServers.size());

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}