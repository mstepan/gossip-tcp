package github.com.mstepan.gossip.state;

import java.time.Instant;
import java.util.Objects;

/**
 * 'generation' - Epoch timestamp in seconds.
 *
 * <p>'heartbit' - Monotonically increased counter. Incremented during each gossip cycle.
 */
public record HearbeatState(long generation, long version) {

    /**
     * Generate next heartbeat state. Record new generation timestamp and increment heartbeat
     * counter.
     */
    public HearbeatState next() {
        return new HearbeatState(Instant.now().getEpochSecond(), version + 1);
    }

    public long calculateDelta(long curTimeEpoch) {
        return curTimeEpoch - generation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HearbeatState that = (HearbeatState) o;
        return generation == that.generation && version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(generation, version);
    }
}
