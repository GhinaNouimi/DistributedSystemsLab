package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class RemoteTaskServiceImpl
        extends UnicastRemoteObject
        implements RemoteTaskService {

    private final String serverName;
    private final boolean healthy;
    private final int failureRate;

    private int activeRequests;
    private int successfulRequests;
    private int failedRequests;
    private long totalResponseTime;

    private final Random random;

    public RemoteTaskServiceImpl(
            String serverName,
            boolean healthy,
            int failureRate
    ) throws RemoteException {
        super();
        this.serverName = serverName;
        this.healthy = healthy;
        this.failureRate = failureRate;
        this.activeRequests = 0;
        this.successfulRequests = 0;
        this.failedRequests = 0;
        this.totalResponseTime = 0;
        this.random = new Random();
    }

    @Override
    public String processRequest(String requestName)
            throws RemoteException {

        long startTime = System.currentTimeMillis();

        try {
            int processingTime =
                    500 + random.nextInt(2000);

            Thread.sleep(processingTime);

            boolean simulatedFailure =
                    random.nextInt(100) < failureRate;

            if (simulatedFailure) {
                failedRequests++;

                return "Request ["
                        + requestName
                        + "] failed on "
                        + serverName;
            }

            successfulRequests++;

            return "Request ["
                    + requestName
                    + "] processed by "
                    + serverName;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            failedRequests++;

            return "Request ["
                    + requestName
                    + "] interrupted on "
                    + serverName;

        } finally {
            long endTime = System.currentTimeMillis();
            totalResponseTime += (endTime - startTime);
        }
    }

    @Override
    public String getServerName()
            throws RemoteException {
        return serverName;
    }

    @Override
    public boolean isHealthy()
            throws RemoteException {
        return healthy;
    }

    @Override
    public synchronized int getActiveRequests()
            throws RemoteException {
        return activeRequests;
    }

    @Override
    public synchronized void startRequest()
            throws RemoteException {
        activeRequests++;
    }

    @Override
    public synchronized void finishRequest()
            throws RemoteException {
        if (activeRequests > 0) {
            activeRequests--;
        }
    }

    @Override
    public synchronized int getSuccessfulRequests()
            throws RemoteException {
        return successfulRequests;
    }

    @Override
    public synchronized int getFailedRequests()
            throws RemoteException {
        return failedRequests;
    }

    @Override
    public synchronized long getAverageResponseTime()
            throws RemoteException {

        int completedRequests =
                successfulRequests + failedRequests;

        if (completedRequests == 0) {
            return 0;
        }

        return totalResponseTime / completedRequests;
    }
}