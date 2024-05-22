package github.com.mstepan.gossip;

import github.com.mstepan.gossip.server.GossipScheduledTask;
import github.com.mstepan.gossip.server.GossipServer;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
        name = "gossip",
        mixinStandardHelpOptions = true,
        version = "gossip-tcp 0.0.1",
        description = "Start Gossip node.")
final class ApplicationMain implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-p", "--port"},
            required = true,
            description = "Choose a port number to start the server.")
    private int port;

    @CommandLine.Option(
            names = {"-g", "--gossip"},
            description = "Start Gossip broadcasting immediately.")
    private boolean startGossipConversation;

    @CommandLine.Option(
            names = {"-s", "--seeds"},
            required = true,
            description = "Specify list of seed nodes.")
    List<String> seeds;

    @Override
    public Integer call() {
        System.out.printf("Initial seeds: %s%n", seeds);

        Thread gossipThread = GossipScheduledTask.createThread();

        if (startGossipConversation) {
            gossipThread.start();
        }

        GossipServer server = new GossipServer(port);
        server.startAndWaitForShutdown();

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ApplicationMain()).execute(args);
        System.exit(exitCode);
    }
}
