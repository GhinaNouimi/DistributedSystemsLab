package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteTaskServiceImpl
        extends UnicastRemoteObject
        implements RemoteTaskService {

    private final String serverName;
    private final boolean healthy;
    private int activeRequests;

    public RemoteTaskServiceImpl(String serverName, boolean healthy)
            throws RemoteException {
        super();
        this.serverName = serverName;
        this.healthy = healthy;
        this.activeRequests = 0;
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
    public String processRequest(String requestName)
            throws RemoteException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        return "Request ["
                + requestName
                + "] processed by "
                + serverName;
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
}