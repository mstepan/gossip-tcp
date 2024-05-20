package github.com.mstepan.gossip.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public final class NetworkUtils {

    private NetworkUtils() {
        throw new AssertionError("Can't instantiate utility-only class");
    }

    public static void close(Closeable socket) {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static InetAddress getHostAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            throw new IllegalStateException(
                "Can't get localhost InetAddress: %s".formatted(ex.getMessage()));
        }
    }
}
