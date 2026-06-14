package demo;

import core.model.RMIServerConfig;
import core.model.RMIWeightedServerEntry;
import loadbalancer.RMIWeightedRoundRobinLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIWeightedRoundRobinClient {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            List<RMIServerConfig> serverConfigs = List.of(
                    new RMIServerConfig("ServerA", "RMI Server A", true, 4, 0),
                    new RMIServerConfig("ServerB", "RMI Server B", true, 2, 0),
                    new RMIServerConfig("ServerC", "RMI Server C", true, 1, 0),
                    new RMIServerConfig("ServerD", "RMI Server D", true, 3, 0),
                    new RMIServerConfig("ServerE", "RMI Server E", true, 2, 0)
            );

            List<RMIWeightedServerEntry> weightedServers =
                    new ArrayList<>();

            for (RMIServerConfig config : serverConfigs) {
                RemoteTaskService server =
                        (RemoteTaskService)
                                registry.lookup(config.getRegistryName());

                weightedServers.add(
                        new RMIWeightedServerEntry(
                                server,
                                config.getWeight()
                        )
                );

                System.out.println(
                        config.getRegistryName()
                                + " loaded with weight="
                                + config.getWeight()
                );
            }

            RMIWeightedRoundRobinLoadBalancer loadBalancer =
                    new RMIWeightedRoundRobinLoadBalancer(
                            weightedServers
                    );

            for (int requestNumber = 1;
                 requestNumber <= 24;
                 requestNumber++) {

                RemoteTaskService selectedServer = null;

                try {
                    selectedServer =
                            loadBalancer.getNextServer();

                    String response =
                            selectedServer.processRequest(
                                    "Request " + requestNumber
                            );

                    System.out.println(response);
                    System.out.println();

                } finally {
                    if (selectedServer != null) {
                        selectedServer.finishRequest();
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}