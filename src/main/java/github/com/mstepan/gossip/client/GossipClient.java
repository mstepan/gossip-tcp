package github.com.mstepan.gossip.client;

import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GossipClient implements AutoCloseable {

    private final Socket socket;

    private final DataInputStream in;

    private final DataOutputStream out;

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

    /** Send SYN message and read ACK as a response. */
    public GossipMessage sendSynMessage(GossipMessage request) {
        try {
            byte[] rawRequest = request.toByteArray();
            out.writeInt(rawRequest.length);
            out.write(rawRequest);
            out.flush();

            int responseLength = in.readInt();
            byte[] rawResponse = new byte[responseLength];
            in.readNBytes(rawResponse, 0, rawResponse.length);

            return GossipMessage.newBuilder().mergeFrom(rawResponse).build();
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    /** Send ACK2 message without reading anything back. */
    public void sendAck2Message(GossipMessage request) {
        try {
            byte[] rawRequest = request.toByteArray();
            out.writeInt(rawRequest.length);
            out.write(rawRequest);
            out.flush();
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static void main(String[] args) {
        try (GossipClient client =
                GossipClient.newInstance(
                        NetworkUtils.getHostAddress().getCanonicalHostName(), 5001)) {
            //            DigestLine digestLine1 =
            //                    DigestLine.newBuilder()
            //                            .setHost("192.168.1.1")
            //                            .setPort(5001)
            //                            .setGeneration(Instant.now().getEpochSecond())
            //                            .setHeartbeat(5L)
            //                            .build();
            //
            //            DigestLine digestLine2 =
            //                    DigestLine.newBuilder()
            //                            .setHost("192.168.1.1")
            //                            .setPort(5002)
            //                            .setGeneration(Instant.now().getEpochSecond())
            //                            .setHeartbeat(3L)
            //                            .build();

            Syn synMessage = Syn.newBuilder().build();

            GossipMessage message = GossipMessage.newBuilder().setSyn(synMessage).build();

            GossipMessage response = client.sendSynMessage(message);

            System.out.printf("Response: %s%n", response);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
