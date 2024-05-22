package github.com.mstepan.gossip.state;

public record NodeInfo(String host, int port, NodeType type) {}
