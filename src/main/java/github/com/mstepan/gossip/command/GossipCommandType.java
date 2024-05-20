package github.com.mstepan.gossip.command;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public enum GossipCommandType {
    SYN((short) 0x00_01),
    ACK((short) 0x00_02),
    ACK_SYN((short) 0x00_03);

    private final short tag;

    GossipCommandType(short tag) {
        this.tag = tag;
    }

    public short tag() {
        return tag;
    }

    /** Create Gossip command from raw bytes array. */
    public static GossipCommandType fromBytes(byte[] rawData) {
        try (DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(rawData))) {
            short tag = dataIn.readShort();

            GossipCommandType gossipCommandType = findByTag(tag);

            if (gossipCommandType == null) {
                throw new IllegalStateException(
                        "Can't find Gossip command for tag: %d".formatted(tag));
            }

            return gossipCommandType;
        } catch (IOException ioEx) {
            // I/O exception won't be thrown here b/c we use in-memory array of bytes as a stream
            throw new IllegalStateException(ioEx);
        }
    }

    private static GossipCommandType findByTag(short tag) {
        for (GossipCommandType singleCommand : GossipCommandType.values()) {
            if (singleCommand.tag == tag) {
                return singleCommand;
            }
        }
        return null;
    }
}
