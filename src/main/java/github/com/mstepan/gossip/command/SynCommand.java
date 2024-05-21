package github.com.mstepan.gossip.command;

import java.util.Iterator;
import java.util.List;

public class SynCommand implements GossipCommand {

    private final List<SyncLineInfo> digest;

    public SynCommand(List<SyncLineInfo> digest) {
        this.digest = digest;
    }

    @Override
    public GossipCommandType type() {
        return GossipCommandType.SYN;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("digest: [");

        Iterator<SyncLineInfo> it = digest.iterator();

        if (it.hasNext()) {
            buf.append(it.next());
        }

        while (it.hasNext()) {
            buf.append(", ").append(it.next());
        }

        buf.append("]");

        return buf.toString();
    }
}
