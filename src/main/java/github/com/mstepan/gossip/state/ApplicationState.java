package github.com.mstepan.gossip.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Node metadata.
 *
 * @param status - mode status.
 * @param metadata - generic node metadata in key-value format.
 */
public final class ApplicationState {

    public static final ApplicationState EMPTY = new ApplicationState();

    private AppStatus status;

    private final Map<String, String> metadata;

    public ApplicationState() {
        this(AppStatus.NORMAL, new HashMap<>());
    }

    public ApplicationState(AppStatus status, Map<String, String> metadata) {
        this.status = status;
        this.metadata = new HashMap<>(metadata);
    }

    public Map<String, String> metadataCopy() {
        return new HashMap<>(metadata);
    }

    public void status(AppStatus newStatus) {
        this.status = newStatus;
    }

    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "status: %s, metadata: %s".formatted(status, metadata);
    }

    /** For local usage only. Should not be shared during gossip communication. */
    enum AppStatus {
        BOOTSTRAP,
        NORMAL,
        LEFT
    }
}
