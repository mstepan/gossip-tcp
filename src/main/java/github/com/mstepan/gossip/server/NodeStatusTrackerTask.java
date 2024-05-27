package github.com.mstepan.gossip.server;

import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.util.ThreadUtils;
import java.util.concurrent.TimeUnit;

public class NodeStatusTrackerTask implements Runnable {

    private static final long INITIAL_DELAY_IN_MS = 5_000L;

    @Override
    public void run() {
        System.out.printf("%s started%n", Thread.currentThread().getName());

        ThreadUtils.sleepMs(INITIAL_DELAY_IN_MS);

        while (!Thread.currentThread().isInterrupted()) {
            try {

                NodeGlobalState.INST.recalculateStates();

                TimeUnit.MILLISECONDS.sleep(2000L);
            } catch (InterruptedException interEx) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
