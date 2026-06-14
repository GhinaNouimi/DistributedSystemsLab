package core.model;

public class RMIServerConfig {

    private final String registryName;
    private final String serverName;
    private final boolean healthy;
    private final int weight;
    private final int failureRate;

    public RMIServerConfig(
            String registryName,
            String serverName,
            boolean healthy,
            int weight,
            int failureRate
    ) {
        this.registryName = registryName;
        this.serverName = serverName;
        this.healthy = healthy;
        this.weight = weight;
        this.failureRate = failureRate;
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

    public int getWeight() {
        return weight;
    }

    public int getFailureRate() {
        return failureRate;
    }
}