package github.com.mstepan.gossip.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Node metadata.
 *
 * @param status - mode status.
 * @param metadata - generic node metadata in key-value format.
 */
public record ApplicationState(AppStatus status, Map<String, String> metadata) {

    public static final ApplicationState EMPTY =
            new ApplicationState(AppStatus.NORMAL, new HashMap<>());

    /** For local usage only. Should not be shared during gossip communication. */
    enum AppStatus {
        BOOTSTRAP,
        NORMAL,
        LEAVING,
        LEFT
    }
}
