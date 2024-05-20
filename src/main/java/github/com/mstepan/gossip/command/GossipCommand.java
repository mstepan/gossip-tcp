package github.com.mstepan.gossip.command;

public interface GossipCommand {

    GossipCommand fromBytes(byte[] rawData);

    byte[] toBytes();
}
