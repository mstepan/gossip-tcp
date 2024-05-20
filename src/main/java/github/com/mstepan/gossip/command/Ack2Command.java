package github.com.mstepan.gossip.command;

public class Ack2Command implements GossipCommand {

    @Override
    public GossipCommandType type() {
        return GossipCommandType.ACK2;
    }
}
