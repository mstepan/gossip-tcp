package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.GossipCommandType;
import github.com.mstepan.gossip.util.IOUtils;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
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
            try (InputStream inNotUsed = clientSocket.getInputStream();
                    BufferedInputStream bufferedInNotUsed = new BufferedInputStream(inNotUsed);
                    DataInputStream dataIn = new DataInputStream(bufferedInNotUsed)) {

                // Read message length
                short messageLength = dataIn.readShort();

                // Read all bytes of a message
                byte[] commandRawBytes = new byte[messageLength];
                dataIn.readFully(commandRawBytes);

                // Construct Gossip command from raw bytes
                GossipCommandType gossipCommandType = GossipCommandType.fromBytes(commandRawBytes);
                System.out.printf("Command: %s%n", gossipCommandType);
            }
        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        } finally {
            IOUtils.close(clientSocket);
        }
    }
}
