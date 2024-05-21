package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.GossipCommand;
import github.com.mstepan.gossip.command.GossipCommandFactory;
import github.com.mstepan.gossip.util.DataIn;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.BufferedInputStream;
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
            try (InputStream in = clientSocket.getInputStream();
                    BufferedInputStream bufferedIn = new BufferedInputStream(in);
                    DataIn dataIn = new DataIn(bufferedIn)) {

                // Read message length
                int messageLength = dataIn.readInt();

                // Read all bytes of a message
                byte[] commandRawBytes = new byte[messageLength];
                dataIn.readBytes(commandRawBytes);

                GossipCommandFactory factory = new GossipCommandFactory();
                GossipCommand command = factory.newInstance(commandRawBytes);

                System.out.printf(
                        "Command received: %s, with body: %s%n",
                        command.type(), command);
            }
        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        } finally {
            NetworkUtils.close(clientSocket);
        }
    }
}
