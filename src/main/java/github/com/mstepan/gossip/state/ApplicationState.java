package github.com.mstepan.gossip.state;

/**
 * Node metadata.
 *
 * @param status
 * @param diskSpaceUsage
 */
public record ApplicationState(AppStatus status, double diskSpaceUsage) {

    public static final ApplicationState EMPTY = new ApplicationState(AppStatus.NORMAL, 0.0);

    /** For local usage only. Should not be shared during gossip communication. */
    enum AppStatus {
        BOOTSTRAP,
        NORMAL,
        LEAVING,
        LEFT
    }
}
