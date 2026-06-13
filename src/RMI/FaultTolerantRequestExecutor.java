package RMI;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaultTolerantRequestExecutor {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MILLIS = 1000;

    private final List<RemoteTaskService> servers;
    private final Map<String, CircuitBreaker> circuitBreakers;

    public FaultTolerantRequestExecutor(
            List<RemoteTaskService> servers
    ) throws RemoteException {

        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Servers list cannot be empty."
            );
        }

        this.servers = servers;
        this.circuitBreakers = new HashMap<>();

        for (RemoteTaskService server : servers) {
            circuitBreakers.put(
                    server.getServerName(),
                    new CircuitBreaker(
                            2,
                            5000
                    )
            );
        }
    }

    public String execute(String requestName) {

        long backoffMillis =
                INITIAL_BACKOFF_MILLIS;

        for (int attempt = 1;
             attempt <= MAX_RETRIES;
             attempt++) {

            RemoteTaskService selectedServer =
                    null;

            try {
                selectedServer =
                        selectBestAvailableServer();

                if (selectedServer == null) {
                    return fallbackResponse(requestName);
                }

                String serverName =
                        selectedServer.getServerName();

                CircuitBreaker circuitBreaker =
                        circuitBreakers.get(serverName);

                System.out.println();
                System.out.println(
                        "Attempt "
                                + attempt
                                + " for "
                                + requestName
                );

                System.out.println(
                        "Selected server -> "
                                + serverName
                                + " | CircuitBreaker="
                                + circuitBreaker.getState()
                );

                selectedServer.startRequest();

                String response =
                        selectedServer.processRequest(requestName);

                if (isFailureResponse(response)) {
                    throw new RemoteException(response);
                }

                circuitBreaker.recordSuccess();

                return response
                        + " | success after attempt "
                        + attempt;

            } catch (Exception exception) {

                try {
                    if (selectedServer != null) {
                        String serverName =
                                selectedServer.getServerName();

                        CircuitBreaker circuitBreaker =
                                circuitBreakers.get(serverName);

                        circuitBreaker.recordFailure();

                        System.out.println(
                                "Failure on "
                                        + serverName
                                        + " | failureCount="
                                        + circuitBreaker.getFailureCount()
                                        + " | CircuitBreaker="
                                        + circuitBreaker.getState()
                        );
                    }

                    if (attempt < MAX_RETRIES) {
                        System.out.println(
                                "Retrying after "
                                        + backoffMillis
                                        + " ms..."
                        );

                        Thread.sleep(backoffMillis);
                        backoffMillis *= 2;
                    }

                } catch (Exception innerException) {
                    innerException.printStackTrace();
                }

            } finally {
                if (selectedServer != null) {
                    try {
                        selectedServer.finishRequest();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }

        return fallbackResponse(requestName);
    }

    private RemoteTaskService selectBestAvailableServer()
            throws RemoteException {

        RemoteTaskService selectedServer = null;
        double bestScore = Double.MAX_VALUE;

        for (RemoteTaskService server : servers) {

            String serverName =
                    server.getServerName();

            CircuitBreaker circuitBreaker =
                    circuitBreakers.get(serverName);

            if (!server.isHealthy()) {
                System.out.println(
                        serverName
                                + " is DOWN -> skipped"
                );
                continue;
            }

            if (!circuitBreaker.allowRequest()) {
                System.out.println(
                        serverName
                                + " CircuitBreaker is OPEN -> skipped"
                );
                continue;
            }

            int activeRequests =
                    server.getActiveRequests();

            long averageResponseTime =
                    server.getAverageResponseTime();

            int failedRequests =
                    server.getFailedRequests();

            double score =
                    activeRequests * 10
                            + averageResponseTime * 0.01
                            + failedRequests * 5;

            System.out.println(
                    serverName
                            + " | active="
                            + activeRequests
                            + " | avgResponse="
                            + averageResponseTime
                            + "ms"
                            + " | failures="
                            + failedRequests
                            + " | score="
                            + score
                            + " | CB="
                            + circuitBreaker.getState()
            );

            if (score < bestScore) {
                bestScore = score;
                selectedServer = server;
            }
        }

        return selectedServer;
    }

    private boolean isFailureResponse(
            String response
    ) {
        String lowerResponse =
                response.toLowerCase();

        return lowerResponse.contains("failed")
                || lowerResponse.contains("interrupted");
    }

    private String fallbackResponse(
            String requestName
    ) {
        return "Fallback response for ["
                + requestName
                + "]: service is temporarily unavailable. "
                + "Please try again later.";
    }
}