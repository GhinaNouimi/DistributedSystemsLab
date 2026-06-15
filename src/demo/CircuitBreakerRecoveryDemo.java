package demo;

import faulttolerance.FaultTolerantRequestExecutor;
import remote.RemoteTaskService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class CircuitBreakerRecoveryDemo {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            RemoteTaskService serverC =
                    (RemoteTaskService) registry.lookup("ServerC");

            System.out.println("========== CIRCUIT BREAKER RECOVERY DEMO ==========");
            System.out.println("Scenario: ServerC fails repeatedly until CircuitBreaker opens");
            System.out.println("IMPORTANT: For this demo, set ServerC failureRate=100 in RMIServerLauncher.");
            System.out.println();

            serverC.setHealthy(true);

            List<RemoteTaskService> servers =
                    List.of(serverC);

            FaultTolerantRequestExecutor executor =
                    new FaultTolerantRequestExecutor(servers);

            System.out.println("Step 1: Trigger failures until CircuitBreaker becomes OPEN");
            System.out.println();

            for (int requestNumber = 1;
                 requestNumber <= 3;
                 requestNumber++) {

                String result =
                        executor.execute(
                                "CircuitBreaker forced failure request "
                                        + requestNumber
                        );

                System.out.println(result);
                System.out.println("--------------------------------");
            }

            System.out.println();
            System.out.println("Step 2: Wait for OPEN duration to expire");
            System.out.println("Waiting 6 seconds so CircuitBreaker can move to HALF_OPEN...");
            Thread.sleep(6000);

            System.out.println();
            System.out.println("Step 3: Send request after wait to observe HALF_OPEN behavior");

            String result =
                    executor.execute(
                            "CircuitBreaker half-open test request"
                    );

            System.out.println(result);
            System.out.println("--------------------------------");

            System.out.println();
            System.out.println(
                    "Circuit breaker recovery scenario finished."
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}