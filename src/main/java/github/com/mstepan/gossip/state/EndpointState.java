package github.com.mstepan.gossip.state;

public record EndpointState(HostInfo host, HearbeatState heartbeat, ApplicationState application) {}
