package github.com.mstepan.gossip;

import github.com.mstepan.gossip.server.GossipScheduledTask;
import github.com.mstepan.gossip.server.GossipServer;

public final class ApplicationMain {

    public static void main(String[] args) {

        Thread gossipThread = new Thread(new GossipScheduledTask());

        gossipThread.setName("GossipScheduledTask");
        gossipThread.setDaemon(true);
        gossipThread.start();

        if (args.length < 1) {
            System.err.println("Port value should be passed as a command line argument");
            return;
        }

        final int port = Integer.parseInt(args[0]);

        GossipServer server = new GossipServer(port);
        server.startAndWaitForShutdown();

        System.out.println("Application completed...");
    }
}
