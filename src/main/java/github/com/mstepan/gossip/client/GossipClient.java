package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.command.digest.SynResponse;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;

public class GossipClient {

    private final String host;

    private final int port;

    public GossipClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SynResponse sendMessage(MessageWrapper message) {
        try {
            System.out.printf("Connecting to gossip node at port: '%d'%n", port);

            try (Socket socket = new Socket(host, port);
                    OutputStream out = socket.getOutputStream();
                    BufferedOutputStream bufferedOut = new BufferedOutputStream(out)) {

                System.out.println("Sending SYN request");

                message.writeTo(bufferedOut);

                // TODO:

                SynResponse synResponse = SynResponse.newBuilder().build();

                System.out.printf("SYN sent successfully, received response: %s%n", synResponse);

                return synResponse;
            }
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static void main(String[] args) {
        GossipClient client =
                new GossipClient(NetworkUtils.getHostAddress().getCanonicalHostName(), 5001);

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

        SynRequest synRequest =
                SynRequest.newBuilder().addDigests(digestLine1).addDigests(digestLine2).build();

        MessageWrapper message = MessageWrapper.newBuilder().setSynRequest(synRequest).build();

        client.sendMessage(message);
    }
}
