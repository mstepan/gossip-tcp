package github.com.mstepan.gossip.state;

public record EndpointStateSnapshot(HearbitState heartbitState, ApplicationState appState) {}
