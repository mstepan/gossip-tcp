package github.com.mstepan.gossip.command;

import github.com.mstepan.gossip.util.DataIn;
import github.com.mstepan.gossip.util.DataOut;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SyncCommand implements GossipCommand {

    private final List<SyncLineInfo> digest;

    public SyncCommand(List<SyncLineInfo> digest) {
        this.digest = digest;
    }

    @Override
    public GossipCommandType type() {
        return GossipCommandType.SYN;
    }

    public byte[] toBytes() {
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                DataOut dataOut = new DataOut(byteArrayOut)) {

            // 4 bytes, digest size
            dataOut.writeInt(digest.size());

            for (SyncLineInfo line : digest) {
                dataOut.writeString(line.host());
                dataOut.writeInt(line.port());
                dataOut.writeLong(line.generation());
                dataOut.writeLong(line.heartbit());
            }

            return byteArrayOut.toByteArray();
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static GossipCommand fromStream(DataIn dataIn) {

        // 4 bytes, digest size
        int digestSize = dataIn.readInt();

        List<SyncLineInfo> allDigests = new ArrayList<>();

        for (int i = 0; i < digestSize; ++i) {
            String host = dataIn.readString();
            int port = dataIn.readInt();
            long generation = dataIn.readLong();
            long heartbeat = dataIn.readLong();
            allDigests.add(new SyncLineInfo(host, port, generation, heartbeat));
        }

        return new SyncCommand(allDigests);
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
