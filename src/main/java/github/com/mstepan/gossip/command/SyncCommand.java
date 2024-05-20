package github.com.mstepan.gossip.command;

import github.com.mstepan.gossip.util.DataOut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncCommand implements GossipCommand {

    private final List<SyncLineInfo> digest = new ArrayList<>();


    @Override
    public GossipCommand fromBytes(byte[] rawData) {
        return null;
    }

    @Override
    public byte[] toBytes() {
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
             DataOut dataOut = new DataOut(byteArrayOut)) {

            // 2 bytes, Gossip command tag
            dataOut.writeShort(GossipCommandType.SYN.tag());

            // 4 bytes, digest size
            dataOut.writeInt(digest.size());

            for( SyncLineInfo line : digest){
                dataOut.writeString(line.host());
                dataOut.writeLong(line.generation());
                dataOut.writeLong(line.heartbit());
            }

            return byteArrayOut.toByteArray();
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

}
