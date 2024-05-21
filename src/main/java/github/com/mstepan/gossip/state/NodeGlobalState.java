package github.com.mstepan.gossip.state;

import java.time.Instant;

/**
 * Tracks gossip node state. Should be thread-safe. We will leverage enum-singleton to prevent the
 * instantiation of multiple instances of this class.
 */
public enum NodeGlobalState {
    INST;

    /** Epoch timestamp in seconds. */
    private long generation;

    /** Monotonically increased counter. Increment during each gossip cycle. */
    private long heartbit;

    /** Node status. For local usage only. Should not be shared during gossip communication. */
    private NodeStatus status;

    NodeGlobalState() {
        generation = Instant.now().getEpochSecond();
        heartbit = 0;
        status = NodeStatus.BOOTSTRAP;
    }

    public synchronized NodeStateSnapshot recordCycle() {
        generation = Instant.now().getEpochSecond();
        ++heartbit;
        return snapshot();
    }

    public synchronized NodeStateSnapshot snapshot() {
        return new NodeStateSnapshot(generation, heartbit, status);
    }
}
