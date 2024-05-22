package github.com.mstepan.gossip.state;

/**
 * 'generation' - Epoch timestamp in seconds.
 *
 * <p>'heartbit' - Monotonically increased counter. Incremented during each gossip cycle.
 */
public record HearbitState(long generation, long hearbit) {}
