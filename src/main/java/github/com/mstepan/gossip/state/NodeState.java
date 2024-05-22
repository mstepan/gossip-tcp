package github.com.mstepan.gossip.state;

public record NodeState(String host, int port, NodeType type) {
    public String hostAndPort() {
        return "%s:%d".formatted(host, port);
    }
}
