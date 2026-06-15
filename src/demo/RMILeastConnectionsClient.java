package demo;

import loadbalancer.RMILeastConnectionsLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMILeastConnectionsClient {

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

            RMILeastConnectionsLoadBalancer loadBalancer =
                    new RMILeastConnectionsLoadBalancer(servers);

            for (int requestNumber = 1;
                 requestNumber <= 10;
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