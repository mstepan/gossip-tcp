package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;

public class GossipClient implements AutoCloseable {

    private final Socket socket;

    private final InputStream in;

    private final OutputStream out;

    public static GossipClient newInstance(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
            return new GossipClient(socket);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private GossipClient(Socket socket) {
        this.socket = socket;
        this.in = NetworkUtils.socketInputStream(socket);
        this.out = NetworkUtils.socketOutputStream(socket);
    }

    @Override
    public void close() {
        NetworkUtils.closeSilently(in);
        NetworkUtils.closeSilently(out);
        NetworkUtils.closeSilently(socket);
    }

    public GossipMessage sendMessage(GossipMessage request) {
        try {
            request.writeTo(out);
            out.flush();

            GossipMessage response = GossipMessage.newBuilder().mergeFrom(in).build();
            return response;
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
                            .setHeartbeat(5L)
                            .build();

            DigestLine digestLine2 =
                    DigestLine.newBuilder()
                            .setHost("192.168.1.1")
                            .setPort(5002)
                            .setGeneration(Instant.now().getEpochSecond())
                            .setHeartbeat(3L)
                            .build();

            Syn synMessage =
                    Syn.newBuilder().addDigests(digestLine1).addDigests(digestLine2).build();

            GossipMessage message = GossipMessage.newBuilder().setSyn(synMessage).build();

            client.sendMessage(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
