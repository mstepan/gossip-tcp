package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.client.GossipClient;
import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.command.digest.SynResponse;
import github.com.mstepan.gossip.state.NodeGlobalState;
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
        sleepMs(INITIAL_DELAY_IN_MS);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<NodeInfo> peersToGossip = NodeGlobalState.INST.randomPeers(HOST_GOSSIP_COUNT);
                for (NodeInfo singleNode : peersToGossip) {
                    startGossipConversation(singleNode);
                }

                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }

    private static void sleepMs(long delayInMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(delayInMs);
        } catch (InterruptedException interEx) {
            Thread.currentThread().interrupt();
        }
    }

    private void startGossipConversation(NodeInfo singleNode) {

        System.out.printf("Starting gossip conversation with node: %s.%n", singleNode);

        try (GossipClient client = GossipClient.newInstance(singleNode.host(), singleNode.port())) {
            SynRequest.Builder synRequestBuilder = SynRequest.newBuilder();

            //        DigestLine digestLine1 =
            //                DigestLine.newBuilder()
            //                        .setHost("192.168.1.1")
            //                        .setPort(5001)
            //                        .setGeneration(Instant.now().getEpochSecond())
            //                        .setHeartbit(5L)
            //                        .build();
            //
            //        synRequestBuilder.addDigests(digestLine1);

            MessageWrapper message =
                    MessageWrapper.newBuilder().setSynRequest(synRequestBuilder.build()).build();

            SynResponse synResponse = client.sendMessage(message);

            System.out.printf("Gossip conversation completed for node: %s .", singleNode);

        } catch (Exception ex) {
            System.out.printf("Gossip conversation failed with node: %s%n.", singleNode);
        }
    }
}
