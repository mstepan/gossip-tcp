package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.digest.Ack;
import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

final class GossipTcpConversationHandler implements Runnable {

    private final Socket clientSocket;

    public GossipTcpConversationHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            try (InputStream in = new BufferedInputStream(clientSocket.getInputStream());
                    BufferedOutputStream out =
                            new BufferedOutputStream(clientSocket.getOutputStream())) {

                GossipMessage message = GossipMessage.newBuilder().mergeFrom(in).build();

                if (message.hasSyn()) {
                    // handle SYN request
                    Syn synMessage = message.getSyn();

                    for (DigestLine digestLine : synMessage.getDigestsList()) {
                        System.out.printf("Handling digest line: '%s'%n", digestLine);
                    }

                    // write ACK message
                    Ack ackMessage = Ack.newBuilder().build();

                    ackMessage.writeTo(out);
                    out.flush();
                } else if (message.hasAck()) {
                    // handle ACK
                    // TODO:
                } else if (message.hasAck2()) {
                    // handle ACK2
                    // TODO:
                } else {
                    System.out.println("Undefined message received");
                }
            }
        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        } finally {
            NetworkUtils.close(clientSocket);
        }
    }
}
