package github.com.mstepan.gossip.state;

public record ApplicationState(AppStatus status, double diskSpaceUsage) {}
