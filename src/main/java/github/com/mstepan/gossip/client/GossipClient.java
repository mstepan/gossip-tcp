package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.SyncCommand;
import github.com.mstepan.gossip.util.DataOut;
import github.com.mstepan.gossip.util.NetworkUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class GossipClient {

    private final int port;

    public GossipClient(int port) {
        this.port = port;
    }

    public void syncDigest() {
        try {
            try (Socket socket = new Socket(NetworkUtils.getHostAddress(), port);
                    OutputStream out = socket.getOutputStream();
                    BufferedOutputStream bufferedOut = new BufferedOutputStream(out);
                    DataOut dataOut = new DataOut(bufferedOut)) {

                SyncCommand syncCommand = new SyncCommand();
                byte[] rawSyncCommand = syncCommand.toBytes();

                dataOut.writeShort(rawSyncCommand.length);
                bufferedOut.write(rawSyncCommand);
            }
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static void main(String[] args) {
        GossipClient client = new GossipClient(5001);
        client.syncDigest();
    }
}
