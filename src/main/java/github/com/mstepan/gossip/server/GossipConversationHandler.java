package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.command.digest.Ack;
import github.com.mstepan.gossip.command.digest.Ack2;
import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.state.DigestDiffCalculator;
import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.util.NetworkUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

final class GossipConversationHandler implements Runnable {

    private final Socket clientSocket;

    public GossipConversationHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            try (DataInputStream in = NetworkUtils.socketInputStream(clientSocket);
                    DataOutputStream out = NetworkUtils.socketOutputStream(clientSocket)) {

                System.out.printf("Gossip conversation started%n");

                int dataLength = in.readInt();

                byte[] rawData = new byte[dataLength];
                in.readNBytes(rawData, 0, rawData.length);

                GossipMessage request = GossipMessage.newBuilder().mergeFrom(rawData).build();

                System.out.printf("Gossip message received%n");

                if (request.hasSyn()) {
                    // handle SYN request
                    Syn synMessage = request.getSyn();

                    printDigest("SYN", synMessage.getDigestsList());

                    // write ACK request
                    Ack.Builder ackMessageBuilder = Ack.newBuilder();

                    List<DigestLine> curNodeDigest = NodeGlobalState.INST.createDigest();

                    // TODO: UnsupportedOperationException here
                    List<DigestLine> receivedDigest = new ArrayList<>(synMessage.getDigestsList());

                    List<DigestLine> diff =
                            DigestDiffCalculator.diff(curNodeDigest, receivedDigest);

                    for (DigestLine diffLine : diff) {
                        ackMessageBuilder.addDigests(diffLine);
                    }

                    GossipMessage response =
                            GossipMessage.newBuilder().setAck(ackMessageBuilder.build()).build();

                    byte[] rawResponse = response.toByteArray();
                    out.writeInt(rawResponse.length);
                    out.write(rawResponse);
                    out.flush();
                } else if (request.hasAck2()) {
                    // handle ACK2
                    Ack2 ack2Message = request.getAck2();
                    printDigest("ACK2", ack2Message.getDigestsList());

                    // TODO:
                } else {
                    System.out.println("Undefined request received");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // System.err.printf("%s: %s%n", ex, ex.getMessage());
        } finally {
            NetworkUtils.close(clientSocket);
        }
    }

    private void printDigest(String messageType, List<DigestLine> digest) {
        System.out.printf(
                "========================= %s Digest received=========================%n",
                messageType);
        for (DigestLine digestLine : digest) {
            System.out.printf("Line: %s%n", digestLine);
        }
        System.out.printf(
                "======================================================================%n");
    }
}
