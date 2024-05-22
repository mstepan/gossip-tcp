package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.client.GossipClient;
import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.command.digest.SynResponse;
import github.com.mstepan.gossip.state.EndpointState;
import github.com.mstepan.gossip.state.EndpointStateSnapshot;
import github.com.mstepan.gossip.state.NodeInfo;
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
                List<NodeInfo> peersToGossip = EndpointState.INST.randomPeers(HOST_GOSSIP_COUNT);

                EndpointStateSnapshot endpointStateSnapshot = EndpointState.INST.recordCycle();

                System.out.println("===========================================================");
                System.out.printf(
                        "Gossip cycle: %d%n", endpointStateSnapshot.heartbitState().hearbit());
                System.out.println("===========================================================");

                for (NodeInfo singleNode : peersToGossip) {
                    startGossipConversation(singleNode, endpointStateSnapshot);
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

    private void startGossipConversation(NodeInfo singleNode, EndpointStateSnapshot stateSnapshot) {
        try (GossipClient client = GossipClient.newInstance(singleNode.host(), singleNode.port())) {
            SynRequest.Builder synRequestBuilder = SynRequest.newBuilder();

            MessageWrapper message =
                    MessageWrapper.newBuilder().setSynRequest(synRequestBuilder.build()).build();

            SynResponse synResponse = client.sendMessage(message);

            System.out.printf("Gossip conversation completed with node: %s%n", singleNode);

        } catch (Exception ex) {
            System.out.printf("Gossip conversation failed with node: %s%n", singleNode);
        }
    }
}
