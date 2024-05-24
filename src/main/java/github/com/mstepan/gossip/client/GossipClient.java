package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.digest.Ack;
import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;

public class GossipClient implements AutoCloseable {

    private final Socket socket;

    private final BufferedInputStream in;

    private final BufferedOutputStream out;

    public static GossipClient newInstance(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            return new GossipClient(
                    socket,
                    new BufferedInputStream(socket.getInputStream()),
                    new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private GossipClient(Socket socket, BufferedInputStream in, BufferedOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void close() {
        NetworkUtils.closeSilently(in);
        NetworkUtils.closeSilently(out);
        NetworkUtils.closeSilently(socket);
    }

    public Ack sendMessage(GossipMessage message) {
        try {
            message.writeTo(out);
            out.flush();

            Ack ackMessage = Ack.newBuilder().build();

            return ackMessage;
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static void main(String[] args) {
        try (GossipClient client =
                GossipClient.newInstance(
                        NetworkUtils.getHostAddress().getCanonicalHostName(), 5001)) {
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

            Syn synMessage =
                    Syn.newBuilder().addDigests(digestLine1).addDigests(digestLine2).build();

            GossipMessage message = GossipMessage.newBuilder().setSyn(synMessage).build();

            client.sendMessage(message);
        }
    }
}
