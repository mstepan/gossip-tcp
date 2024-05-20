package github.com.mstepan.gossip.command;

import java.util.Optional;

public enum GossipCommandType {
    SYN(0x01),
    ACK(0x02),
    ACK2(0x03);

    private final int tag;

    GossipCommandType(int tag) {
        this.tag = tag;
    }

    public int tag() {
        return tag;
    }

    public static Optional<GossipCommandType> findByTag(int tag) {
        for (GossipCommandType singleCommand : GossipCommandType.values()) {
            if (singleCommand.tag == tag) {
                return Optional.of(singleCommand);
            }
        }
        return Optional.empty();
    }
}
