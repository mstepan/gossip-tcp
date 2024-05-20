package github.com.mstepan.gossip.command;

public record SyncLineInfo(String host, int port, long generation, long heartbit) {}
