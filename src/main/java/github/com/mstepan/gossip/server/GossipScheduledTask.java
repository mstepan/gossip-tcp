package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.state.NodeStateSnapshot;
import java.util.concurrent.TimeUnit;

public class GossipScheduledTask implements Runnable {

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

                System.out.printf("Gossip cycle: %s%n", snapshot);
                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }
}
