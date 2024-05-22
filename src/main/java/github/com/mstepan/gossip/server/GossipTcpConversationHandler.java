package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.command.digest.SynResponse;
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

                MessageWrapper message = MessageWrapper.newBuilder().mergeFrom(in).build();

                if (message.hasSynRequest()) {
                    // handle SYN request
                    SynRequest synRequest = message.getSynRequest();

                    for (DigestLine digestLine : synRequest.getDigestsList()) {
                        System.out.printf("Handling digest line: '%s'%n", digestLine);
                    }

                    // write SYN response
                    SynResponse synResponse = SynResponse.newBuilder().build();

                    synResponse.writeTo(out);
                    out.flush();
                } else if (message.hasAckRequest()) {
                    // handle ACK
                    // TODO:
                } else if (message.hasAck2Request()) {
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
