package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIFaultToleranceClient {

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

            FaultTolerantRequestExecutor executor =
                    new FaultTolerantRequestExecutor(
                            servers
                    );

            for (int requestNumber = 1;
                 requestNumber <= 20;
                 requestNumber++) {

                String result =
                        executor.execute(
                                "Request " + requestNumber
                        );

                System.out.println(result);
                System.out.println(
                        "--------------------------------"
                );
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}