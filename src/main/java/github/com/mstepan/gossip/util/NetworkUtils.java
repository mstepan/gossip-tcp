package github.com.mstepan.gossip.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;

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

    private static final String ETHERNET_0_NETWORK_INTERFACE_NAME = "en0";

    public static InetAddress getHostAddress() {
        try {
            Optional<NetworkInterface> maybeNic =
                    NetworkInterface.networkInterfaces()
                            .filter(nic -> ETHERNET_0_NETWORK_INTERFACE_NAME.equals(nic.getName()))
                            .findFirst();

            if (maybeNic.isEmpty()) {
                throw new IllegalStateException(
                        "Can't find network interface with name '%s'"
                                .formatted(ETHERNET_0_NETWORK_INTERFACE_NAME));
            }
            Optional<InetAddress> address =
                    maybeNic.get()
                            .inetAddresses()
                            .filter(adr -> adr instanceof Inet4Address)
                            .findFirst();

            if (address.isEmpty()) {
                throw new IllegalStateException(
                        "Can't find IPv4 address for network interface '%s'"
                                .formatted(ETHERNET_0_NETWORK_INTERFACE_NAME));
            }
            return address.get();

        } catch (SocketException ex) {
            throw new IllegalStateException(
                    "Can't get localhost InetAddress: %s".formatted(ex.getMessage()));
        }
    }

    public static void main(String[] args) throws Exception {

        //        NetworkInterface.networkInterfaces()
        //                .forEach(
        //                        (nic) -> {
        //                            System.out.printf("%s, %s%n", nic.getName(),
        // nic.inetAddresses().toList());
        //                        });
    }
}
