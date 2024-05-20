package github.com.mstepan.gossip.command;

import github.com.mstepan.gossip.util.DataIn;
import java.util.Optional;

public class GossipCommandFactory {

    public GossipCommand newInstance(byte[] commandRawBytes) {

        try (DataIn in = new DataIn(commandRawBytes)) {

            // Read GossipCommand tag value to detect command type
            int tag = in.readInt();

            Optional<GossipCommandType> maybeGossipCommandType = GossipCommandType.findByTag(tag);

            if (maybeGossipCommandType.isEmpty()) {
                throw new IllegalStateException(
                        "Undefined gossip command for tag: %d".formatted(tag));
            }

            return switch (maybeGossipCommandType.get()) {
                case SYN -> SyncCommand.fromStream(in);
                case ACK -> new AckCommand();
                case ACK2 -> new Ack2Command();
            };
        }
    }
}
