package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteTaskService extends Remote {

    String processRequest(String requestName)
            throws RemoteException;

    String getServerName()
            throws RemoteException;

    boolean isHealthy()
            throws RemoteException;

    int getActiveRequests()
            throws RemoteException;

    void startRequest()
            throws RemoteException;

    void finishRequest()
            throws RemoteException;

    int getSuccessfulRequests()
            throws RemoteException;

    int getFailedRequests()
            throws RemoteException;

    long getAverageResponseTime()
            throws RemoteException;
    long heartbeat() throws RemoteException;
}