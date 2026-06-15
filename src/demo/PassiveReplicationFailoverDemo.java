package demo;

import heartbeat.HeartbeatMonitor;
import remote.RemoteTaskService;
import replication.PassiveReplicationService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class PassiveReplicationFailoverDemo {

    public static void main(String[] args) {

        try {
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            RemoteTaskService leader =
                    (RemoteTaskService) registry.lookup("ServerA");

            RemoteTaskService followerOne =
                    (RemoteTaskService) registry.lookup("ServerC");

            RemoteTaskService followerTwo =
                    (RemoteTaskService) registry.lookup("ServerE");

            leader.setHealthy(true);
            followerOne.setHealthy(true);
            followerTwo.setHealthy(true);

            List<RemoteTaskService> servers =
                    List.of(leader, followerOne, followerTwo);

            HeartbeatMonitor monitor =
                    new HeartbeatMonitor();

            System.out.println("========== PASSIVE REPLICATION FAILOVER DEMO ==========");
            System.out.println("Scenario: Leader fails, follower is promoted");
            System.out.println();

            System.out.println("Step 1: Initial heartbeat check");
            monitor.getHealthyServers(servers);

            System.out.println();
            System.out.println("Step 2: Create passive replication group");
            System.out.println("Leader   -> " + leader.getServerName());
            System.out.println("Follower -> " + followerOne.getServerName());
            System.out.println("Follower -> " + followerTwo.getServerName());

            PassiveReplicationService replicationService =
                    new PassiveReplicationService(
                            leader,
                            List.of(followerOne, followerTwo)
                    );

            System.out.println();
            System.out.println("Step 3: Leader fails before write");
            leader.setHealthy(false);

            System.out.println();
            System.out.println("Step 4: Write operation starts");
            replicationService.write(
                    "Update patient record after leader failure"
            );

            System.out.println();
            System.out.println("Step 5: Heartbeat after failover");
            monitor.getHealthyServers(servers);

            System.out.println();
            System.out.println(
                    "Passive replication failover scenario finished successfully."
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}