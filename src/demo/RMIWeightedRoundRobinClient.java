package demo;

import core.model.RMIWeightedServerEntry;
import loadbalancer.RMIWeightedRoundRobinLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RMIWeightedRoundRobinClient {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            Map<String, Integer> serverWeights =
                    new LinkedHashMap<>();

            serverWeights.put("ServerA", 4);
            serverWeights.put("ServerB", 2);
            serverWeights.put("ServerC", 1);
            serverWeights.put("ServerD", 3);
            serverWeights.put("ServerE", 2);

            List<RMIWeightedServerEntry> weightedServers =
                    new ArrayList<>();

            for (Map.Entry<String, Integer> entry :
                    serverWeights.entrySet()) {

                RemoteTaskService server =
                        (RemoteTaskService)
                                registry.lookup(entry.getKey());

                weightedServers.add(
                        new RMIWeightedServerEntry(
                                server,
                                entry.getValue()
                        )
                );

                System.out.println(
                        entry.getKey()
                                + " loaded with weight="
                                + entry.getValue()
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