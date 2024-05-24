package github.com.mstepan.gossip.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public final class NetworkUtils {

    private NetworkUtils() {
        throw new AssertionError("Can't instantiate utility-only class");
    }

    public static DataInputStream socketInputStream(Socket socket) {
        try {
            return new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static DataOutputStream socketOutputStream(Socket socket) {
        try {
            return new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static void close(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public static void closeSilently(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
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
