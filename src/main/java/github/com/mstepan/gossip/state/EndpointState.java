package github.com.mstepan.gossip.state;

import github.com.mstepan.gossip.command.digest.DigestLine;

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
}
