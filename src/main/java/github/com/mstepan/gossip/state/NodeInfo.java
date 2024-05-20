package github.com.mstepan.gossip.state;

import java.time.Instant;

/** Tracks gossip node state. Should be thread-safe. */
public class NodeInfo {

    /** Epoch timestamp in seconds. */
    private long generation;

    /** Monotonically increased counter. Increment during each gossip cycle. */
    private long heartbit;

    /** Node status. For local usage only. Should not be shared during gossip communication. */
    private NodeStatus status;

    public NodeInfo() {
        generation = Instant.now().getEpochSecond();
        heartbit = 0;
    }

    public long generation() {
        return generation;
    }

    public long heartbit() {
        return heartbit;
    }

    public NodeStatus status() {
        return status;
    }
}
