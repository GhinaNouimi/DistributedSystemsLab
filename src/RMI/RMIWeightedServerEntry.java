package RMI;

public class RMIWeightedServerEntry {

    private final RemoteTaskService server;
    private final int weight;

    public RMIWeightedServerEntry(
            RemoteTaskService server,
            int weight
    ) {
        this.server = server;
        this.weight = weight;
    }

    public RemoteTaskService getServer() {
        return server;
    }

    public int getWeight() {
        return weight;
    }
}