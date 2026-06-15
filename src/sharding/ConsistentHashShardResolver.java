package sharding;

import remote.RemoteTaskService;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashShardResolver implements ShardResolver {

    private static final int VIRTUAL_NODES = 100;

    private final SortedMap<Integer, RemoteTaskService> ring =
            new TreeMap<>();

    public ConsistentHashShardResolver(
            List<RemoteTaskService> servers
    ) throws RemoteException {

        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException(
                    "No healthy servers available for sharding"
            );
        }

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
        if (ring.isEmpty()) {
            throw new IllegalStateException("Shard ring is empty");
        }

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
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("MD5");

            byte[] bytes =
                    digest.digest(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            int hash = 0;

            for (int i = 0; i < 4; i++) {
                hash =
                        (hash << 8)
                                | (bytes[i] & 0xFF);
            }

            return hash & 0x7fffffff;

        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(
                    "Hash algorithm not found.",
                    exception
            );
        }
    }
}