package demo;

import loadbalancer.RMIAdaptiveLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIAdaptiveClient {

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
                        registryName
                                + " loaded from RMI Registry"
                );
            }

            RMIAdaptiveLoadBalancer loadBalancer =
                    new RMIAdaptiveLoadBalancer(servers);

            for (int requestNumber = 1;
                 requestNumber <= 15;
                 requestNumber++) {

                final int currentRequest = requestNumber;

                Thread thread = new Thread(() -> {
                    RemoteTaskService selectedServer = null;

                    try {
                        selectedServer =
                                loadBalancer.getNextServer();

                        String response =
                                selectedServer.processRequest(
                                        "Request " + currentRequest
                                );

                        System.out.println(response);
                        System.out.println();

                    } catch (Exception exception) {
                        exception.printStackTrace();

                    } finally {
                        if (selectedServer != null) {
                            try {
                                selectedServer.finishRequest();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                });

                thread.start();

                Thread.sleep(300);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}