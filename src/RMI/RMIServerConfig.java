package RMI;

public class RMIServerConfig {

    private final String registryName;
    private final String serverName;
    private final boolean healthy;

    public RMIServerConfig(
            String registryName,
            String serverName,
            boolean healthy
    ) {
        this.registryName = registryName;
        this.serverName = serverName;
        this.healthy = healthy;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isHealthy() {
        return healthy;
    }
}