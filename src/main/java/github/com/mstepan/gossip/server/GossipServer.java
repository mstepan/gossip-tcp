package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GossipServer {

    private static final int TCP_QUEUE_SIZE = 1024;

    private final int port;

    public GossipServer(int port) {
        this.port = port;
    }

    public void startAndWaitForShutdown() {

        final InetAddress address = NetworkUtils.getHostAddress();

        System.out.printf(
                "Server listening at '%s:%d' (TCP)%n", address.getCanonicalHostName(), port);

        try (ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor()) {
            try (ServerSocket serverSocket = new ServerSocket(port, TCP_QUEUE_SIZE, address)) {

                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    virtualPool.submit(new GossipTcpConversationHandler(clientSocket));
                }
            }
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }
}
