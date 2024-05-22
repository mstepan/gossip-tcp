package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.client.GossipClient;
import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.command.digest.MessageWrapper;
import github.com.mstepan.gossip.command.digest.SynRequest;
import github.com.mstepan.gossip.state.KnownNodes;
import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.state.NodeInfo;
import github.com.mstepan.gossip.state.NodeStateSnapshot;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GossipScheduledTask implements Runnable {

    /** Number of host that will be used for a single gossip cycle. */
    private static final int HOST_GOSSIP_COUNT = 3;

    /** Single gossip cycle period. */
    private static final long GOSSIP_CYCLE_PERIOD_IN_MS = 1000L;

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
                    synMessage(singleNode);
                }

                System.out.printf("Gossip cycle: %s%n", snapshot);
                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }

    private void synMessage(NodeInfo singleNode) {
        GossipClient client = new GossipClient(singleNode.host(), singleNode.port());

        DigestLine digestLine1 =
                DigestLine.newBuilder()
                        .setHost("192.168.1.1")
                        .setPort(5001)
                        .setGeneration(Instant.now().getEpochSecond())
                        .setHeartbit(5L)
                        .build();

        SynRequest synRequest =
                SynRequest.newBuilder().addDigests(digestLine1).addDigests(digestLine1).build();

        MessageWrapper message = MessageWrapper.newBuilder().setSynRequest(synRequest).build();

        client.sendMessage(message);
    }
}
