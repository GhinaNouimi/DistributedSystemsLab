package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServerLauncher {

    public static void main(String[] args) {
        try {
            Registry registry =
                    LocateRegistry.createRegistry(1099);

            RemoteTaskService serverA =
                    new RemoteTaskServiceImpl("RMI Server A", true);

            RemoteTaskService serverB =
                    new RemoteTaskServiceImpl("RMI Server B", false);

            RemoteTaskService serverC =
                    new RemoteTaskServiceImpl("RMI Server C", true);

            registry.rebind("ServerA", serverA);
            registry.rebind("ServerB", serverB);
            registry.rebind("ServerC", serverC);

            System.out.println("RMI Registry started on port 1099");
            System.out.println("ServerA registered as healthy");
            System.out.println("ServerB registered as DOWN");
            System.out.println("ServerC registered as healthy");
            System.out.println("RMI Servers are ready...");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}