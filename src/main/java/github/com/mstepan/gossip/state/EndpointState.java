package github.com.mstepan.gossip.state;

import github.com.mstepan.gossip.command.digest.DigestLine;
import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> digestMetadata = new HashMap<>();

        // Add any additional metadata
        digestMetadata.put("DISK_USAGE", "%.2f%%".formatted(application.diskSpaceUsage()));

        return DigestLine.newBuilder()
                .setHost(host.host())
                .setPort(host.port())
                .setGeneration(heartbeat.generation())
                .setHeartbeat(heartbeat.version())
                .putAllMetadata(digestMetadata)
                .build();
    }
}
