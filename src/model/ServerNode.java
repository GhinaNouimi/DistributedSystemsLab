package model;

public class ServerNode {

    private String name;
    private int weight;
    private int activeConnections;
    private boolean healthy;

    public ServerNode(String name) {
        this(name, 1);
    }

    public ServerNode(String name, int weight) {
        this.name = name;
        this.weight = weight;
        this.activeConnections = 0;
        this.healthy = true;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void incrementConnections() {
        activeConnections++;
    }

    public void decrementConnections() {
        if (activeConnections > 0) {
            activeConnections--;
        }
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public String toString() {
        return name +
                " | Weight=" + weight +
                " | Connections=" + activeConnections +
                " | Healthy=" + healthy;
    }
}