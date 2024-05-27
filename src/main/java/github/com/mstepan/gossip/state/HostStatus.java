package github.com.mstepan.gossip.state;

/** For local usage only. Should not be shared during gossip communication. */
public enum HostStatus {
    BOOTSTRAP,
    NORMAL,
    DOWN
}
