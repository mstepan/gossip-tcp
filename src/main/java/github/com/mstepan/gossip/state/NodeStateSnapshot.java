package github.com.mstepan.gossip.state;

public record NodeStateSnapshot(long generation, long heartbit, NodeStatus status) {}
