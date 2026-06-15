package demo;

import loadbalancer.RMIHealthAwareLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIHealthAwareClient {

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

            RMIHealthAwareLoadBalancer loadBalancer =
                    new RMIHealthAwareLoadBalancer(servers);

            for (int requestNumber = 1;
                 requestNumber <= 9;
                 requestNumber++) {

                RemoteTaskService selectedServer = null;

                try {
                    selectedServer =
                            loadBalancer.getNextHealthyServer();

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