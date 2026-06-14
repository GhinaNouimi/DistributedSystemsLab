package demo;

import loadbalancer.RMILeastConnectionsLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RMILeastConnectionsConcurrentClient {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            RemoteTaskService serverA =
                    (RemoteTaskService) registry.lookup("ServerA");

            RemoteTaskService serverB =
                    (RemoteTaskService) registry.lookup("ServerB");

            RemoteTaskService serverC =
                    (RemoteTaskService) registry.lookup("ServerC");

            List<RemoteTaskService> servers =
                    List.of(serverA, serverB, serverC);

            RMILeastConnectionsLoadBalancer loadBalancer =
                    new RMILeastConnectionsLoadBalancer(servers);

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