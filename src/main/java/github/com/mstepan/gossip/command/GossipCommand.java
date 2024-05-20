package github.com.mstepan.gossip.command;

import java.util.Optional;

public enum GossipCommand {
    SYN((short) 0x00_01),
    ACK((short) 0x00_02),
    ACK_SYN((short) 0x00_03);

    private final short tag;

    GossipCommand(short tag) {
        this.tag = tag;
    }

    public static Optional<GossipCommand> findByTag(short tag) {
        for (GossipCommand singleCommand : GossipCommand.values()) {
            if (singleCommand.tag == tag) {
                return Optional.of(singleCommand);
            }
        }
        return Optional.empty();
    }
}
