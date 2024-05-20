package github.com.mstepan.gossip.server;

import java.util.concurrent.TimeUnit;

public class GossipScheduledTask implements Runnable {

    /** Single gossip cycle period. */
    private static final long GOSSIP_CYCLE_PERIOD_IN_MS = 1000L;

    @Override
    public void run() {

        System.out.println("Gossip task started");

        int cycleIdx = 0;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                System.out.printf("Gossip cycle %d%n", cycleIdx);
                ++cycleIdx;

                TimeUnit.MILLISECONDS.sleep(GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Gossip task completed");
    }
}
