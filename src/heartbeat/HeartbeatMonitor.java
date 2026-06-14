package heartbeat;

import remote.RemoteTaskService;

import java.util.ArrayList;
import java.util.List;

public class HeartbeatMonitor {

    public List<RemoteTaskService> getHealthyServers(
            List<RemoteTaskService> servers
    ) {
        List<RemoteTaskService> healthyServers =
                new ArrayList<>();

        for (RemoteTaskService server : servers) {
            try {
                long beat = server.heartbeat();

                System.out.println(
                        server.getServerName()
                                + " heartbeat OK at "
                                + beat
                );

                healthyServers.add(server);

            } catch (Exception exception) {
                try {
                    System.out.println(
                            server.getServerName()
                                    + " heartbeat FAILED -> DOWN"
                    );
                } catch (Exception ignored) {
                    System.out.println(
                            "Unknown server heartbeat FAILED"
                    );
                }
            }
        }

        return healthyServers;
    }
}