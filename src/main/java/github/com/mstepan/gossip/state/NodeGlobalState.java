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
                        new ApplicationState(ApplicationState.AppStatus.BOOTSTRAP, 0.0)));
    }

    public synchronized void addNode(HostInfo hostInfo) {
        System.out.printf("Node added %s%n", hostInfo);

        checkNotNull(hostInfo, "Can't add null 'host'.");
        endpoints.put(
                hostInfo.hostAndPort(),
                new EndpointState(hostInfo, HearbeatState.EMPTY, ApplicationState.EMPTY));
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
            if (!singleEntry.getValue().host().hostAndPort().equals(currentHostAndPort)) {
                randomSelection.add(singleEntry.getValue().host());
            }
        }

        return randomSelection;
    }

    public synchronized NodeGlobalStateSnapshot recordCycle() {
        EndpointState currentEndpointState = endpoints.get(currentHostAndPort);
        EndpointState newEndpointState;

        // set new HeartBit state value
        HearbeatState newHeartbeat = currentEndpointState.heartbeat().next();

        // wait at least 3 gossip iterations till mark application status as NORMAL
        if (currentEndpointState.heartbeat().version() == 3L) {
            ApplicationState newAppState =
                    new ApplicationState(ApplicationState.AppStatus.NORMAL, 50.0);
            newEndpointState =
                    new EndpointState(currentEndpointState.host(), newHeartbeat, newAppState);
        } else {
            newEndpointState =
                    new EndpointState(
                            currentEndpointState.host(),
                            newHeartbeat,
                            currentEndpointState.application());
        }

        endpoints.put(currentHostAndPort, newEndpointState);

        return new NodeGlobalStateSnapshot(newEndpointState.heartbeat());
    }

    public synchronized List<DigestLine> createDigest() {
        List<DigestLine> digest = new ArrayList<>();

        for (EndpointState endpoint : endpoints.values()) {
            digest.add(endpoint.toDigestCompact());
        }

        return digest;
    }
}
