package github.com.mstepan.gossip.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public final class DataOut implements AutoCloseable {

    private final DataOutputStream originalStream;

    public DataOut(OutputStream originalStream) {
        this.originalStream =
                new DataOutputStream(
                        Objects.requireNonNull(originalStream, "'originalStream' can't be null"));
    }

    //    public void writeShort(short value) {
    //        try {
    //            originalStream.writeShort(value);
    //        } catch (IOException ioEx) {
    //            throw new IllegalStateException(ioEx);
    //        }
    //    }

    public void writeInt(int value) {
        try {
            originalStream.writeInt(value);
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public void writeLong(long value) {
        try {
            originalStream.writeLong(value);
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public void writeString(String str) {
        try {
            originalStream.writeInt(str.length());
            originalStream.writeBytes(str);
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    public void writeBytes(byte[] arr) {
        try {
            originalStream.write(arr);
        } catch (IOException ioEx) {
            throw new IllegalStateException(ioEx);
        }
    }

    @Override
    public void close() {
        NetworkUtils.close(originalStream);
    }
}
