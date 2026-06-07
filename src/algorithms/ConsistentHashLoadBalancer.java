package algorithms;

import model.ServerNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer {

    private final SortedMap<Integer, ServerNode> hashRing;

    public ConsistentHashLoadBalancer() {
        this.hashRing = new TreeMap<>();
    }

    public void addServer(ServerNode server) {
        int hash = hash(server.getName());

        hashRing.put(hash, server);

        System.out.println(
                server.getName() + " added at hash " + hash
        );
    }

    public ServerNode getServer(String key) {
        if (hashRing.isEmpty()) {
            throw new IllegalStateException("No servers available.");
        }

        int keyHash = hash(key);

        SortedMap<Integer, ServerNode> tailMap =
                hashRing.tailMap(keyHash);

        int selectedHash =
                tailMap.isEmpty()
                        ? hashRing.firstKey()
                        : tailMap.firstKey();

        return hashRing.get(selectedHash);
    }

    private int hash(String value) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("MD5");

            byte[] bytes =
                    digest.digest(value.getBytes(StandardCharsets.UTF_8));

            int hash = 0;

            for (int i = 0; i < 4; i++) {
                hash =
                        (hash << 8)
                                | (bytes[i] & 0xFF);
            }

            return hash & 0x7fffffff;

        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Hash algorithm not found.", exception);
        }
    }
}