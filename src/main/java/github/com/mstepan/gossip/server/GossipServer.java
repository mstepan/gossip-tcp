package github.com.mstepan.gossip.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GossipServer {

    private static final int TCP_QUEUE_SIZE = 1024;

    private final int port;

    public GossipServer(int port) {
        this.port = port;
    }

    public void startAndWaitForShutdown() {

        final InetAddress address = getHostAddress();

        System.out.printf(
                "Server started at '%s:%d' (TCP)%n", address.getCanonicalHostName(), port);

        try (ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor()) {
            try (ServerSocket serverSocket = new ServerSocket(port, TCP_QUEUE_SIZE, address)) {

                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.printf(
                            "Client connected from '%s:%d'(TCP)%n",
                            clientSocket.getLocalAddress().getCanonicalHostName(),
                            clientSocket.getPort());
                    virtualPool.submit(new TcpConversationHandler(clientSocket));
                }
            }
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    private static InetAddress getHostAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            throw new IllegalStateException(
                    "Can't get localhost InetAddress: %s".formatted(ex.getMessage()));
        }
    }
}
