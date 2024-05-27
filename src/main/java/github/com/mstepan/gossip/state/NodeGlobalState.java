package github.com.mstepan.gossip.state;

import static github.com.mstepan.gossip.util.Preconditions.checkNotNull;

import github.com.mstepan.gossip.command.digest.DigestLine;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks gossip host state and associated metadata. Should be thread-safe. We will leverage
 * enum-singleton to prevent the instantiation of multiple instances of this class.
 */
public enum NodeGlobalState {
    INST;

    /** Track all known endpoints states */
    private final Map<String, EndpointState> endpoints;

    private String currentHostAndPort;

    NodeGlobalState() {
        endpoints = new HashMap<>();
    }

    public synchronized void addCurrentNode(HostInfo hostInfo) {
        currentHostAndPort = hostInfo.hostAndPort();

        endpoints.put(
                currentHostAndPort,
                new EndpointState(
                        hostInfo,
                        new HearbeatState(Instant.now().getEpochSecond(), 0),
                        new ApplicationState(
                                ApplicationState.AppStatus.BOOTSTRAP,
                                Map.of(
                                        "DISK_USAGE",
                                        "0%",
                                        "NODE_ID",
                                        UUID.randomUUID().toString()))));
    }

    public synchronized void addNode(HostInfo hostInfo) {
        System.out.printf("Node added %s%n", hostInfo);
        checkNotNull(hostInfo, "Can't add null 'host'.");
        putEndpoint(new EndpointState(hostInfo, HearbeatState.EMPTY, ApplicationState.EMPTY));
    }

    /**
     * To simplify things, we'll copy all known hosts(endpoints) into an ArrayList, randomly shuffle
     * the list, and then select the first 'numOfNodes' from the list.
     */
    public synchronized List<HostInfo> randomPeers(int numOfNodes) {
        List<Map.Entry<String, EndpointState>> endpointsCopy =
                new ArrayList<>(endpoints.entrySet());
        Collections.shuffle(endpointsCopy);

        List<HostInfo> randomSelection = new ArrayList<>(numOfNodes);

        Iterator<Map.Entry<String, EndpointState>> shuffledNodesIt = endpointsCopy.iterator();
        for (int i = 0; i < numOfNodes && shuffledNodesIt.hasNext(); ++i) {

            Map.Entry<String, EndpointState> singleEntry = shuffledNodesIt.next();

            // skip current host if specified as a SEED
            if (notCurrentNode(singleEntry.getValue())) {
                randomSelection.add(singleEntry.getValue().host());
            }
        }

        return randomSelection;
    }

    public synchronized NodeGlobalStateSnapshot recordCycle() {
        EndpointState currentEndpointState = endpoints.get(currentHostAndPort);

        // set new HeartBit state value
        currentEndpointState.heartbeat(currentEndpointState.heartbeat().next());

        // wait at least 3 gossip iterations till mark application status as NORMAL
        if (currentEndpointState.heartbeat().version() == 3L) {
            currentEndpointState.application().status(ApplicationState.AppStatus.NORMAL);
            currentEndpointState.application().addMetadata("DISK_USAGE", "50%");
        }

        return new NodeGlobalStateSnapshot(currentEndpointState.heartbeat());
    }

    public synchronized List<DigestLine> createDigest() {
        List<DigestLine> digest = new ArrayList<>();

        for (EndpointState endpoint : endpoints.values()) {
            digest.add(endpoint.toDigestCompact());
        }

        return digest;
    }

    public synchronized List<DigestLine> createDigestWithMetadata() {
        List<DigestLine> digest = new ArrayList<>();

        for (EndpointState endpoint : endpoints.values()) {
            digest.add(endpoint.toDigestFull());
        }

        return digest;
    }

    public synchronized List<DigestLine> updateState(List<DigestLine> digestData) {

        List<DigestLine> newerDigestWithMetadata = new ArrayList<>();

        for (DigestLine receivedDigestLine : digestData) {
            String endpointKey =
                    "%s:%d".formatted(receivedDigestLine.getHost(), receivedDigestLine.getPort());

            Map<String, String> receivedMetadata =
                    new HashMap<>(receivedDigestLine.getMetadataMap());

            EndpointState endpointState = endpoints.get(endpointKey);

            if (endpointState == null) {
                EndpointState newState =
                        new EndpointState(
                                new HostInfo(
                                        receivedDigestLine.getHost(),
                                        receivedDigestLine.getPort(),
                                        HostType.NORMAL),
                                new HearbeatState(
                                        receivedDigestLine.getGeneration(),
                                        receivedDigestLine.getHeartbeat()),
                                new ApplicationState(
                                        ApplicationState.AppStatus.NORMAL, receivedMetadata));

                endpoints.put(endpointKey, newState);
            } else {
                DigestLine curDigestLine = endpointState.toDigestFull();

                int cmpRes =
                        DigestDiffCalculator.GENERATION_THEN_HEARTBEAT_ASC.compare(
                                curDigestLine, receivedDigestLine);

                // cur digest is older, so update with a new state from received digest
                if (cmpRes < 0) {
                    EndpointState newState =
                            new EndpointState(
                                    endpointState.host(),
                                    new HearbeatState(
                                            receivedDigestLine.getGeneration(),
                                            receivedDigestLine.getHeartbeat()),
                                    new ApplicationState(
                                            ApplicationState.AppStatus.NORMAL, receivedMetadata));

                    endpoints.put(endpointKey, newState);
                }
            }
        }

        return newerDigestWithMetadata;
    }

    public synchronized void printState() {
        for (EndpointState endpointState : endpoints.values()) {
            System.out.println(
                    "===================================================================");
            System.out.printf(
                    "[%s] -> %s -> %s %n",
                    endpointState.host().hostAndPort(),
                    endpointState.heartbeat(),
                    endpointState.application());
            System.out.println(
                    "===================================================================");
        }
    }

    public synchronized void recalculateStates() {

        final long now = Instant.now().getEpochSecond();

        for (EndpointState endpointState : endpoints.values()) {

            // Skip any state recalculation for the current node, current node status will be set
            // explicitly
            if (isCurrentNode(endpointState)) {
                continue;
            }

            long generationDelta = endpointState.heartbeat().calculateDelta(now);

            // node generation value wasn't updated for 10 seconds, probably node is DOWN
            if (generationDelta >= 10L) {
                endpointState.application().status(ApplicationState.AppStatus.LEFT);
            } else {
                // mark node as normal state
                endpointState.application().status(ApplicationState.AppStatus.NORMAL);
            }
        }
    }

    /**
     * 'putEndpoint' method can be un-synchronized b/c will be used only from public methods that
     * all MUST be synchronized.
     */
    private void putEndpoint(EndpointState endpoint) {
        endpoints.put(endpoint.host().hostAndPort(), endpoint);
    }

    private boolean isCurrentNode(EndpointState endpoint) {
        return endpoint.host().hostAndPort().equals(currentHostAndPort);
    }

    private boolean notCurrentNode(EndpointState endpoint) {
        return !isCurrentNode(endpoint);
    }
}
