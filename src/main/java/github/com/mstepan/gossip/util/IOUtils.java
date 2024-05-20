package github.com.mstepan.gossip.util;

import java.io.Closeable;
import java.io.IOException;

public final class IOUtils {

    private IOUtils() {
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
}
