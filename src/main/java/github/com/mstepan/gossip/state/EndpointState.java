package github.com.mstepan.gossip.state;

import github.com.mstepan.gossip.command.digest.DigestLine;
import java.util.Objects;

public final class EndpointState {

    private final HostInfo host;
    private HearbeatState heartbeat;
    private ApplicationState application;

    public EndpointState(HostInfo host, HearbeatState heartbeat, ApplicationState application) {
        this.host = host;
        this.heartbeat = heartbeat;
        this.application = application;
    }

    public HostInfo host() {
        return host;
    }

    public HearbeatState heartbeat() {
        return heartbeat;
    }

    public void heartbeat(HearbeatState newHeartbeatState) {
        this.heartbeat = newHeartbeatState;
    }

    public ApplicationState application() {
        return application;
    }

    /** Create digest without metadata. */
    public DigestLine toDigestCompact() {
        return DigestLine.newBuilder()
                .setHost(host.host())
                .setPort(host.port())
                .setGeneration(heartbeat.generation())
                .setHeartbeat(heartbeat.version())
                .build();
    }

    public DigestLine toDigestFull() {
        return DigestLine.newBuilder()
                .setHost(host.host())
                .setPort(host.port())
                .setGeneration(heartbeat.generation())
                .setHeartbeat(heartbeat.version())
                .putAllMetadata(application.metadataCopy())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointState that = (EndpointState) o;
        return Objects.equals(host, that.host)
                && Objects.equals(heartbeat, that.heartbeat)
                && Objects.equals(application, that.application);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, heartbeat, application);
    }
}
