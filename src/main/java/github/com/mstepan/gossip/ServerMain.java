package github.com.mstepan.gossip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ServerMain {

    private static final int TCP_QUEUE_SIZE = 1024;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Port value should be passed as a command line argument");
            return;
        }

        final InetAddress address = getHostAddress();
        final int port = Integer.parseInt(args[0]);

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
            System.err.printf("Error occurred: %s%n", ioEx.getMessage());
            return;
        }

        System.out.println("Main completed...");
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
