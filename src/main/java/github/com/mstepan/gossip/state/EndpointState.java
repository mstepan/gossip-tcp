package github.com.mstepan.gossip.state;

import github.com.mstepan.gossip.command.digest.DigestLine;
import java.util.HashMap;

public record EndpointState(HostInfo host, HearbeatState heartbeat, ApplicationState application) {

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
                .putAllMetadata(new HashMap<>(application.metadata()))
                .build();
    }
}
