package remote;

import core.model.RMIServerConfig;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RMIServerLauncher {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.createRegistry(1099);

            List<RMIServerConfig> serverConfigs = List.of(
                    new RMIServerConfig(
                            "ServerA",
                            "RMI Server A",
                            true,
                            4,
                            10
                    ),
                    new RMIServerConfig(
                            "ServerB",
                            "RMI Server B",
                            true,
                            2,
                            70
                    ),
                    new RMIServerConfig(
                            "ServerC",
                            "RMI Server C",
                            true,
                            1,
                            15
                    ),
                    new RMIServerConfig(
                            "ServerD",
                            "RMI Server D",
                            true,
                            3,
                            40
                    ),
                    new RMIServerConfig(
                            "ServerE",
                            "RMI Server E",
                            true,
                            2,
                            5
                    )
            );

            for (RMIServerConfig config : serverConfigs) {

                RemoteTaskService server =
                        new RemoteTaskServiceImpl(
                                config.getServerName(),
                                config.isHealthy(),
                                config.getFailureRate()
                        );

                registry.rebind(
                        config.getRegistryName(),
                        server
                );

                printServerStatus(config);
            }

            System.out.println();
            System.out.println(
                    "RMI Registry started on port 1099"
            );
            System.out.println(
                    "RMI Servers are ready..."
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void printServerStatus(
            RMIServerConfig config
    ) {

        String status =
                config.isHealthy()
                        ? "healthy"
                        : "DOWN";

        System.out.println(
                config.getRegistryName()
                        + " registered as "
                        + status
                        + " | weight="
                        + config.getWeight()
                        + " | failureRate="
                        + config.getFailureRate()
                        + "%"
        );
    }
}