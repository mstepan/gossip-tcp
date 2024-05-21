package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.GossipCommandType;
import github.com.mstepan.gossip.command.SyncCommand;
import github.com.mstepan.gossip.command.SyncLineInfo;
import github.com.mstepan.gossip.util.DataOut;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class GossipClient {

    private final int port;

    public GossipClient(int port) {
        this.port = port;
    }

    public void syncDigest() {
        try {

            System.out.printf("Connecting to gossip node at port: '%d'%n", port);

            try (Socket socket = new Socket(NetworkUtils.getHostAddress(), port);
                    OutputStream out = socket.getOutputStream();
                    BufferedOutputStream bufferedOut = new BufferedOutputStream(out);
                    DataOut dataOut = new DataOut(bufferedOut)) {

                System.out.println("Sending digest");

                SyncCommand syncCommand =
                        new SyncCommand(
                                List.of(
                                        new SyncLineInfo(
                                                "192.168.1.1",
                                                5001,
                                                Instant.now().getEpochSecond(),
                                                123L),
                                        new SyncLineInfo(
                                                "192.168.1.1",
                                                5002,
                                                Instant.now().getEpochSecond(),
                                                124L)));

                byte[] rawSyncCommand = syncCommand.toBytes();

                // tag length in bytes + raw command body
                int messageLength = 4 + rawSyncCommand.length;

                // 4 bytes, message length
                dataOut.writeInt(messageLength);

                // 4 bytes, command tag
                dataOut.writeInt(GossipCommandType.SYN.tag());

                // command body
                dataOut.writeBytes(rawSyncCommand);
            }
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }

        System.out.println("Sending digest completed");
    }

    public static void main(String[] args) {
        GossipClient client = new GossipClient(5001);
        client.syncDigest();
    }
}
