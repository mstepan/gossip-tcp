package github.com.mstepan.gossip.util;

import java.util.concurrent.TimeUnit;

public final class ThreadUtils {

    private ThreadUtils() {
        throw new AssertionError(
                "Can't instantiate utility-only class. Use defined static methods.");
    }

    public static Thread createDaemonThread(String name, Runnable task) {
        Thread thread = new Thread(task);
        thread.setName(name);
        thread.setDaemon(true);
        return thread;
    }

    public static void sleepMs(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException interEx) {
            // if interrupted, just propagate interruption flag
            Thread.currentThread().interrupt();
        }
    }
}
