package demo;

import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

    public static void main(String[] args) {

        try {

            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            RemoteTaskService serverA =
                    (RemoteTaskService)
                            registry.lookup("ServerA");

            String response =
                    serverA.processRequest("Login Request");

            System.out.println(response);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}