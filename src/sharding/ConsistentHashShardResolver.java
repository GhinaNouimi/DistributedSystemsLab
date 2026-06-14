package sharding;

import remote.RemoteTaskService;

import java.rmi.RemoteException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashShardResolver
        implements ShardResolver {

    private static final int VIRTUAL_NODES = 5;

    private final SortedMap<Integer, RemoteTaskService> ring =
            new TreeMap<>();

    public ConsistentHashShardResolver(
            List<RemoteTaskService> servers
    ) throws RemoteException {

        for (RemoteTaskService server : servers) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNode =
                        server.getServerName() + "#VN" + i;

                ring.put(
                        hash(virtualNode),
                        server
                );
            }
        }
    }

    @Override
    public RemoteTaskService resolveShard(String key) {
        int keyHash = hash(key);

        SortedMap<Integer, RemoteTaskService> tail =
                ring.tailMap(keyHash);

        int selectedHash =
                tail.isEmpty()
                        ? ring.firstKey()
                        : tail.firstKey();

        return ring.get(selectedHash);
    }

    private int hash(String value) {
        return Math.abs(value.hashCode());
    }
}