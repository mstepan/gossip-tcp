package github.com.mstepan.gossip.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class DataIn implements AutoCloseable {

    private final DataInputStream originalStream;

    public DataIn(InputStream originalStream) {
        Objects.requireNonNull(originalStream, "'originalStream' can't be null");
        this.originalStream = new DataInputStream(originalStream);
    }

    public DataIn(byte[] streamAsBytes) {
        Objects.requireNonNull(streamAsBytes, "'streamAsBytes' can't be null");
        this.originalStream = new DataInputStream(new ByteArrayInputStream(streamAsBytes));
    }

    //    public short readShort() {
    //        try {
    //            return originalStream.readShort();
    //        } catch (IOException ioEx) {
    //            throw new IllegalStateException(ioEx);
    //        }
    //    }

    public int readInt() {
        try {
            return originalStream.readInt();
        } catch (IOException ioEx) {
            System.err.printf("Error: %s%n", ioEx.getMessage());
            throw new IllegalStateException(ioEx);
        }
    }

    public long readLong() {
        try {
            return originalStream.readLong();
        } catch (IOException ioEx) {
            System.err.printf("Error: %s%n", ioEx.getMessage());
            throw new IllegalStateException(ioEx);
        }
    }

    public String readString() {
        try {
            int strLength = originalStream.readInt();
            byte[] strBytes = new byte[strLength];
            originalStream.readFully(strBytes);
            return new String(strBytes);
        } catch (IOException ioEx) {
            System.err.printf("Error: %s%n", ioEx.getMessage());
            throw new IllegalStateException(ioEx);
        }
    }

    public void readBytes(byte[] arr) {
        try {
            originalStream.readFully(arr);
        } catch (EOFException eofEx) {
            System.err.printf("EOF exception when reading %d bytes%n", arr.length);
            throw new IllegalStateException(eofEx);
        } catch (IOException ioEx) {
            System.err.printf("Error: %s%n", ioEx.getMessage());
            throw new IllegalStateException(ioEx);
        }
    }

    @Override
    public void close() {
        NetworkUtils.close(originalStream);
    }
}
