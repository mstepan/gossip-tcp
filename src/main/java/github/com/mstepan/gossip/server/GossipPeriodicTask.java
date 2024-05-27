package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.client.GossipClient;
import github.com.mstepan.gossip.command.digest.Ack;
import github.com.mstepan.gossip.command.digest.Ack2;
import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.state.HostInfo;
import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.state.NodeGlobalStateSnapshot;
import github.com.mstepan.gossip.util.ThreadUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GossipPeriodicTask implements Runnable {

    /** The initial delay before we start sending gossip messages to other nodes. */
    private static final long INITIAL_DELAY_IN_MS = 5_000L;

    /** Number of host that will be used for a single gossip cycle. */
    private static final int HOST_GOSSIP_COUNT = 3;

    /** Single gossip cycle period. Should be 1 sec after testing. */
    private static final long GOSSIP_CYCLE_PERIOD_IN_MS = 10_000L;

    @Override
    public void run() {
        System.out.printf("%s started%n", Thread.currentThread().getName());
        ThreadUtils.sleepMs(INITIAL_DELAY_IN_MS);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<HostInfo> peersToGossip = NodeGlobalState.INST.randomPeers(HOST_GOSSIP_COUNT);

                // System.out.printf("Selected peers: %s%n", peersToGossip);

                NodeGlobalStateSnapshot nodeGlobalStateSnapshot =
                        NodeGlobalState.INST.recordCycle();

                System.out.println("===========================================================");
                System.out.printf(
                        "Gossip cycle: %d%n", nodeGlobalStateSnapshot.heartbeat().version());
                System.out.println("===========================================================");

                for (HostInfo singleNode : peersToGossip) {
                    startGossipConversation(singleNode);
                }

                NodeGlobalState.INST.printState();

                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }

    private void startGossipConversation(HostInfo singleNode) {

        try (GossipClient client = GossipClient.newInstance(singleNode.host(), singleNode.port())) {

            List<DigestLine> curDigest = NodeGlobalState.INST.createDigest();

            Syn.Builder synBuilder = Syn.newBuilder();
            for (DigestLine digestLine : curDigest) {
                synBuilder.addDigests(digestLine);
            }

            GossipMessage synMessageRequest =
                    GossipMessage.newBuilder().setSyn(synBuilder.build()).build();

            // Send SYN message, receive ACK response
            GossipMessage synResponse = client.sendSynMessage(synMessageRequest);

            if (!synResponse.hasAck()) {
                throw new IllegalStateException(
                        "SYN message returned incorrect response. Expected ACK.");
            }

            Ack ackResponse = synResponse.getAck();

            List<DigestLine> digestWithMetadata =
                    NodeGlobalState.INST.updateState(ackResponse.getDigestsList());

            // Send ACK2 message
            Ack2.Builder ack2Builder = Ack2.newBuilder();

            for (DigestLine line : digestWithMetadata) {
                ack2Builder.addDigests(line);
            }

            GossipMessage ack2MessageRequest =
                    GossipMessage.newBuilder().setAck2(ack2Builder.build()).build();

            client.sendAck2Message(ack2MessageRequest);

            //            System.out.printf("<==> Gossip conversation COMPLETED with host: %s%n",
            // singleNode);

        } catch (Exception ex) {
            //            ex.printStackTrace();
            System.out.printf("<==??==> Gossip conversation FAILED with host: %s%n", singleNode);
        }
    }
}
