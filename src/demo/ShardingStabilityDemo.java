package demo;

import sharding.ConsistentHashShardResolver;
import remote.RemoteTaskService;
import remote.RemoteTaskServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ShardingStabilityDemo {

    public static void main(String[] args) {

        try {

            System.out.println(
                    "========== SHARDING STABILITY DEMO =========="
            );

            List<RemoteTaskService> originalServers =
                    new ArrayList<>();

            originalServers.add(
                    new RemoteTaskServiceImpl(
                            "ServerA",
                            true,
                            0
                    )
            );

            originalServers.add(
                    new RemoteTaskServiceImpl(
                            "ServerC",
                            true,
                            0
                    )
            );

            originalServers.add(
                    new RemoteTaskServiceImpl(
                            "ServerE",
                            true,
                            0
                    )
            );

            String[] keys = {
                    "patient-1001",
                    "patient-1002",
                    "patient-1003",
                    "appointment-2001",
                    "invoice-3001",
                    "doctor-5001",
                    "lab-result-4001"
            };

            ConsistentHashShardResolver beforeResolver =
                    new ConsistentHashShardResolver(
                            originalServers
                    );

            System.out.println();
            System.out.println("Before adding ServerF");
            System.out.println();

            List<String> beforeAssignments =
                    new ArrayList<>();

            for (String key : keys) {

                String serverName =
                        beforeResolver
                                .resolveShard(key)
                                .getServerName();

                beforeAssignments.add(serverName);

                System.out.println(
                        key
                                + " -> "
                                + serverName
                );
            }

            System.out.println();
            System.out.println(
                    "Adding new server: ServerF"
            );

            originalServers.add(
                    new RemoteTaskServiceImpl(
                            "ServerF",
                            true,
                            0
                    )
            );

            ConsistentHashShardResolver afterResolver =
                    new ConsistentHashShardResolver(
                            originalServers
                    );

            System.out.println();
            System.out.println("After adding ServerF");
            System.out.println();

            int movedKeys = 0;

            for (int i = 0; i < keys.length; i++) {

                String newServer =
                        afterResolver
                                .resolveShard(keys[i])
                                .getServerName();

                System.out.println(
                        keys[i]
                                + " -> "
                                + newServer
                );

                if (!beforeAssignments.get(i)
                        .equals(newServer)) {

                    movedKeys++;
                }
            }

            System.out.println();
            System.out.println(
                    "Moved keys = "
                            + movedKeys
                            + " / "
                            + keys.length
            );

            System.out.println();

            if (movedKeys < keys.length) {

                System.out.println(
                        "Consistent Hashing worked correctly."
                );

                System.out.println(
                        "Only a subset of keys moved."
                );
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}