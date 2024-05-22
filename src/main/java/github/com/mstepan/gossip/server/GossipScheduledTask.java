package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.client.GossipClient;
import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.command.digest.SynResponse;
import github.com.mstepan.gossip.state.KnownNodes;
import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.state.NodeInfo;
import github.com.mstepan.gossip.state.NodeStateSnapshot;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GossipScheduledTask implements Runnable {

    /** Number of host that will be used for a single gossip cycle. */
    private static final int HOST_GOSSIP_COUNT = 3;

    /** Single gossip cycle period. Should be 1 sec after testing. */
    private static final long GOSSIP_CYCLE_PERIOD_IN_MS = 30_000L;

    public static Thread createThread() {
        Thread gossipThread = new Thread(new GossipScheduledTask());
        gossipThread.setName("GossipScheduledTask");
        gossipThread.setDaemon(true);
        return gossipThread;
    }

    @Override
    public void run() {

        System.out.println("Gossip task started");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                NodeStateSnapshot snapshot = NodeGlobalState.INST.recordCycle();

                List<NodeInfo> peersToGossip = KnownNodes.INST.randomPeers(HOST_GOSSIP_COUNT);

                for (NodeInfo singleNode : peersToGossip) {
                    try {
                        System.out.printf("Trying to send SYN request to %s%n", singleNode);
                        SynResponse synResponse = synMessage(singleNode);
                        System.out.printf("SYN response: %s%n", synResponse);
                    } catch (Exception ex) {
                        System.err.printf(
                                "SYN failed for node '%s' with message '%s'%n",
                                singleNode, ex.getMessage());
                    }
                }

                System.out.printf("Gossip cycle completed with state: %s%n", snapshot);

                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }

    private SynResponse synMessage(NodeInfo singleNode) {
        GossipClient client = new GossipClient(singleNode.host(), singleNode.port());

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

        return client.sendMessage(message);
    }
}
