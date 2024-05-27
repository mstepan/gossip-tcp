package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.util.ThreadUtils;
import java.util.concurrent.TimeUnit;

public class NodeStatusTrackerTask implements Runnable {

    @Override
    public void run() {
        System.out.printf("%s started%n", Thread.currentThread().getName());

        ThreadUtils.sleepMs(GossipPeriodicTask.INITIAL_DELAY_IN_MS);

        while (!Thread.currentThread().isInterrupted()) {
            try {

                NodeGlobalState.INST.recalculateStates();

                TimeUnit.MILLISECONDS.sleep(GossipPeriodicTask.GOSSIP_CYCLE_PERIOD_IN_MS);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
