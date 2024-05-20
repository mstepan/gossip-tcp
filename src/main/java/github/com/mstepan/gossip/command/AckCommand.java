package github.com.mstepan.gossip.command;

public class AckCommand implements GossipCommand {

    @Override
    public GossipCommandType type() {
        return GossipCommandType.ACK;
    }
}
