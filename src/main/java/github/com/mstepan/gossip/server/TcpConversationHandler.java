package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.digest.Digest;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

final class TcpConversationHandler implements Runnable {

    private final Socket clientSocket;

    public TcpConversationHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            try (InputStream in = clientSocket.getInputStream();
                    BufferedInputStream bufferedIn = new BufferedInputStream(in)) {

                Digest digest = Digest.newBuilder().mergeFrom(bufferedIn).build();

                System.out.printf("Received digest: %s%n", digest);
            }
        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        } finally {
            NetworkUtils.close(clientSocket);
        }
    }
}
