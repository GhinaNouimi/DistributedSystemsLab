package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RMIServerLauncher {

    public static void main(String[] args) {
        try {
            Registry registry =
                    LocateRegistry.createRegistry(1099);

            List<RMIServerConfig> serverConfigs = List.of(
                    new RMIServerConfig("ServerA", "RMI Server A", true),
                    new RMIServerConfig("ServerB", "RMI Server B", false),
                    new RMIServerConfig("ServerC", "RMI Server C", true),
                    new RMIServerConfig("ServerD", "RMI Server D", false),
                    new RMIServerConfig("ServerE", "RMI Server E", true)
            );

            for (RMIServerConfig config : serverConfigs) {
                RemoteTaskService server =
                        new RemoteTaskServiceImpl(
                                config.getServerName(),
                                config.isHealthy()
                        );

                registry.rebind(
                        config.getRegistryName(),
                        server
                );

                printServerStatus(
                        config.getRegistryName(),
                        server
                );
            }

            System.out.println("RMI Registry started on port 1099");
            System.out.println("RMI Servers are ready...");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void printServerStatus(
            String registryName,
            RemoteTaskService server
    ) throws Exception {

        String status = server.isHealthy()
                ? "healthy"
                : "DOWN";

        System.out.println(
                registryName
                        + " registered as "
                        + status
        );
    }
}