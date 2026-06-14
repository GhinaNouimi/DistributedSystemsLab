package demo;

import loadbalancer.RMIPowerOfTwoChoicesLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIPowerOfTwoChoicesClient {

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
                        registryName +
                                " loaded from RMI Registry"
                );
            }

            RMIPowerOfTwoChoicesLoadBalancer loadBalancer =
                    new RMIPowerOfTwoChoicesLoadBalancer(servers);

            for (int requestNumber = 1;
                 requestNumber <= 10;
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
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}