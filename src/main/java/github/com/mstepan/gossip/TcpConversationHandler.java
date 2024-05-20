package github.com.mstepan.gossip;

import github.com.mstepan.gossip.command.GossipCommand;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Optional;

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
                    DataInputStream dataIn = new DataInputStream(bufferedInNotUsed); ) {

                short tag = dataIn.readShort();

                Optional<GossipCommand> maybeCommand = GossipCommand.findByTag(tag);

                if (maybeCommand.isEmpty()) {
                    System.err.printf("Can't find Gossip command for tag: %d%n", tag);
                } else {
                    System.out.printf("Command: %s%n", maybeCommand.get());
                }
            }

        } catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        }
    }
}
