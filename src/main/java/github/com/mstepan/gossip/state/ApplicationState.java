package github.com.mstepan.gossip.state;

import github.com.mstepan.gossip.util.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Node metadata.
 *
 * <p>'status' - mode status. 'metadata' - generic node metadata in key-value format.
 */
public final class ApplicationState {

    private HostStatus status;

    private final Map<String, String> metadata;

    public ApplicationState(HostStatus status, Map<String, String> metadata) {
        this.status = status;
        this.metadata = new HashMap<>(metadata);
    }

    public ApplicationState() {
        this(HostStatus.NORMAL, new HashMap<>());
    }

    public Map<String, String> metadataCopy() {
        return new HashMap<>(metadata);
    }

    public void status(HostStatus newStatus) {
        this.status = newStatus;
    }

    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "status: %s, metadata: %s".formatted(status, metadata);
    }

    public void replaceMetadata(Map<String, String> newMetadata) {
        Preconditions.checkNotNull(newMetadata, "'newMetadata' can't be null");
        metadata.clear();
        metadata.putAll(newMetadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationState that = (ApplicationState) o;
        return status == that.status && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, metadata);
    }
}
