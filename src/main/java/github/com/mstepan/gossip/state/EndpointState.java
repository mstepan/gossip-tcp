package github.com.mstepan.gossip.state;

public record EndpointState(
        NodeState node, HearbeatState heartbeat, ApplicationState application) {}
