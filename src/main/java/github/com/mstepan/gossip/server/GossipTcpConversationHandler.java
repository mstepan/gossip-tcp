package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedInputStream;
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
            try (InputStream in = clientSocket.getInputStream();
                    BufferedInputStream bufferedIn = new BufferedInputStream(in)) {

                MessageWrapper message = MessageWrapper.newBuilder().mergeFrom(bufferedIn).build();

                if (message.hasSynRequest()) {
                    // handle SYN
                    SynRequest synRequest = message.getSynRequest();
//                    System.out.printf("SYN request received: %s%n", synRequest);
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
