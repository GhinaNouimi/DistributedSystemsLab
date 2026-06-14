package demo;

import loadbalancer.RMIHealthAwareLoadBalancer;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RMIHealthAwareClient {

    public static void main(String[] args) {
        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            RemoteTaskService serverA =
                    (RemoteTaskService)
                            registry.lookup("ServerA");

            RemoteTaskService serverB =
                    (RemoteTaskService)
                            registry.lookup("ServerB");

            RemoteTaskService serverC =
                    (RemoteTaskService)
                            registry.lookup("ServerC");

            List<RemoteTaskService> servers =
                    List.of(serverA, serverB, serverC);

            RMIHealthAwareLoadBalancer loadBalancer =
                    new RMIHealthAwareLoadBalancer(servers);

            for (int requestNumber = 1;
                 requestNumber <= 9;
                 requestNumber++) {

                RemoteTaskService selectedServer =
                        loadBalancer.getNextHealthyServer();

                String response =
                        selectedServer.processRequest(
                                "Request " + requestNumber
                        );

                System.out.println(response);
                System.out.println();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}