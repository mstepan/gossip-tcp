package github.com.mstepan.gossip;

import static github.com.mstepan.gossip.util.Preconditions.checkArgument;

import github.com.mstepan.gossip.server.GossipPeriodicTask;
import github.com.mstepan.gossip.server.GossipServer;
import github.com.mstepan.gossip.server.NodeStatusTrackerTask;
import github.com.mstepan.gossip.state.HostInfo;
import github.com.mstepan.gossip.state.HostType;
import github.com.mstepan.gossip.state.NodeGlobalState;
import github.com.mstepan.gossip.util.NetworkUtils;
import github.com.mstepan.gossip.util.Preconditions;
import github.com.mstepan.gossip.util.ThreadUtils;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
        name = "gossip",
        mixinStandardHelpOptions = true,
        version = "gossip-tcp 0.0.1",
        description = "Start Gossip host.")
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
        try {
            NodeGlobalState.INST.addCurrentNode(currentNode());

            for (String singleSeed : seeds) {
                String[] hostAndPort = singleSeed.split(":");

                checkArgument(
                        hostAndPort.length == 2,
                        "Incorrect seed host detected '%s', should be in format host:port."
                                .formatted(singleSeed));

                String seedHost = hostAndPort[0];
                int seedPort =
                        Preconditions.parseInt(
                                hostAndPort[1],
                                "Seed port is not an integer value: %s".formatted(hostAndPort[1]));

                HostInfo seedHostInfo = new HostInfo(seedHost, seedPort, HostType.SEED);

                NodeGlobalState.INST.addNode(seedHostInfo);
            }

            Thread statusTrackerThread =
                    ThreadUtils.createDaemonThread(
                            "node-status-tracker", new NodeStatusTrackerTask());
            Thread gossipThread =
                    ThreadUtils.createDaemonThread(
                            "gossip-periodic-task", new GossipPeriodicTask());

            if (startGossipConversation) {
                statusTrackerThread.start();
                gossipThread.start();
            }

            GossipServer server = new GossipServer(port);
            server.startAndWaitForShutdown();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }

        return 0;
    }

    private HostInfo currentNode() {
        String currentHost = NetworkUtils.getHostAddress().getCanonicalHostName();
        String hostAndPort = "%s:%s".formatted(currentHost, port);

        HostType currentType = seeds.contains(hostAndPort) ? HostType.SEED : HostType.NORMAL;

        return new HostInfo(currentHost, port, currentType);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ApplicationMain()).execute(args);
        System.exit(exitCode);
    }
}
