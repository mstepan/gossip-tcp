package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.client.GossipClient;
import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.GossipMessage;
import github.com.mstepan.gossip.command.digest.Syn;
import github.com.mstepan.gossip.state.HostInfo;
import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.state.NodeGlobalStateSnapshot;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GossipScheduledTask implements Runnable {

    /** The initial delay before we start sending gossip messages to other nodes. */
    private static final long INITIAL_DELAY_IN_MS = 5000L;

    /** Number of host that will be used for a single gossip cycle. */
    private static final int HOST_GOSSIP_COUNT = 3;

    /** Single gossip cycle period. Should be 1 sec after testing. */
    private static final long GOSSIP_CYCLE_PERIOD_IN_MS = 10_000L;

    public static Thread createThread() {
        Thread gossipThread = new Thread(new GossipScheduledTask());
        gossipThread.setName("GossipScheduledTask");
        gossipThread.setDaemon(true);
        return gossipThread;
    }

    @Override
    public void run() {
        System.out.println("Gossip task started");
        initialSleep();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<HostInfo> peersToGossip = NodeGlobalState.INST.randomPeers(HOST_GOSSIP_COUNT);

                NodeGlobalStateSnapshot nodeGlobalStateSnapshot =
                        NodeGlobalState.INST.recordCycle();

                System.out.println("===========================================================");
                System.out.printf(
                        "Gossip cycle: %d%n", nodeGlobalStateSnapshot.heartbeat().version());
                System.out.println("===========================================================");

                for (HostInfo singleNode : peersToGossip) {
                    startGossipConversation(singleNode, nodeGlobalStateSnapshot);
                }

                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }

    private static void initialSleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(INITIAL_DELAY_IN_MS);
        } catch (InterruptedException interEx) {
            // if interrupted, just propagate interruption flag
            Thread.currentThread().interrupt();
        }
    }

    private void startGossipConversation(
            HostInfo singleNode, NodeGlobalStateSnapshot stateSnapshot) {
        try (GossipClient client = GossipClient.newInstance(singleNode.host(), singleNode.port())) {

            List<DigestLine> curDigest = NodeGlobalState.INST.createDigest();

            Syn.Builder synBuilder = Syn.newBuilder();
            for (DigestLine digestLine : curDigest) {
                synBuilder.addDigests(digestLine);
            }

            GossipMessage synMessageRequest =
                    GossipMessage.newBuilder().setSyn(synBuilder.build()).build();

            GossipMessage ackMessageResponse = client.sendMessage(synMessageRequest);

            System.out.printf("Gossip conversation completed with host: %s%n", singleNode);

        } catch (Exception ex) {
            System.out.printf("Gossip conversation failed with host: %s%n", singleNode);
        }
    }
}
