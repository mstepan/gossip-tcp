package github.com.mstepan.gossip.state;

import java.time.Instant;

/**
 * 'generation' - Epoch timestamp in seconds.
 *
 * <p>'heartbit' - Monotonically increased counter. Incremented during each gossip cycle.
 */
public record HearbeatState(long generation, long version) {
    public static final HearbeatState EMPTY = new HearbeatState(0L, 0L);

    /**
     * Generate next heartbeat state. Record new generation timestamp and increment heartbeat
     * counter.
     */
    public HearbeatState next() {
        return new HearbeatState(Instant.now().getEpochSecond(), version + 1);
    }
}
