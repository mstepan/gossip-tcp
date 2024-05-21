package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.SynMessage;
import github.com.mstepan.gossip.command.digest.WrapperMessage;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;

public class GossipClient {

    private final int port;

    public GossipClient(int port) {
        this.port = port;
    }

    public void sendMessage(WrapperMessage message) {
        try {
            System.out.printf("Connecting to gossip node at port: '%d'%n", port);

            try (Socket socket = new Socket(NetworkUtils.getHostAddress(), port);
                    OutputStream out = socket.getOutputStream();
                    BufferedOutputStream bufferedOut = new BufferedOutputStream(out)) {

                System.out.println("Sending SYN digest");

                message.writeTo(bufferedOut);
            }
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }

        System.out.println("Digest sent successfully");
    }

    public static void main(String[] args) {
        GossipClient client = new GossipClient(5001);

        DigestLine digestLine1 =
                DigestLine.newBuilder()
                        .setHost("192.168.1.1")
                        .setPort(5001)
                        .setGeneration(Instant.now().getEpochSecond())
                        .setHeartbit(5L)
                        .build();

        DigestLine digestLine2 =
                DigestLine.newBuilder()
                        .setHost("192.168.1.1")
                        .setPort(5002)
                        .setGeneration(Instant.now().getEpochSecond())
                        .setHeartbit(3L)
                        .build();

        SynMessage synMessage =
                SynMessage.newBuilder().addDigests(digestLine1).addDigests(digestLine2).build();

        WrapperMessage message = WrapperMessage.newBuilder().setDigest(synMessage).build();

        client.sendMessage(message);
    }
}
