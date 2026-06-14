package sharding;

import remote.RemoteTaskService;

public interface ShardResolver {

    RemoteTaskService resolveShard(String key);
}