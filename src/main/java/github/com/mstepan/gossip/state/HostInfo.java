package github.com.mstepan.gossip.state;

public record HostInfo(String host, int port, HostType type) {
    public String hostAndPort() {
        return "%s:%d".formatted(host, port);
    }
}
