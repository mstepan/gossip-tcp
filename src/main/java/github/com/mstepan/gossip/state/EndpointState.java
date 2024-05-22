package github.com.mstepan.gossip.state;

import static github.com.mstepan.gossip.util.Preconditions.checkNotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Tracks gossip node state and associated metadata. Should be thread-safe. We will leverage
 * enum-singleton to prevent the instantiation of multiple instances of this class.
 */
public enum EndpointState {
    INST;

    /** Keep a set of all nodes that the current host knows about. */
    private final Set<NodeInfo> nodes;

    private NodeInfo current;

    private HearbitState heartbitState;

    /** Node status. For local usage only. Should not be shared during gossip communication. */
    private ApplicationState appState;

    EndpointState() {
        nodes = new HashSet<>();
        heartbitState = new HearbitState(Instant.now().getEpochSecond(), 0);
        appState = new ApplicationState(AppStatus.BOOTSTRAP, 0.0);
    }

    public synchronized void addCurrentNode(NodeInfo node) {
        current = node;
    }

    public synchronized void addNode(NodeInfo node) {
        System.out.printf("Node added %s%n", node);

        checkNotNull(node, "Can't add null 'node'.");
        nodes.add(node);
    }

    /**
     * To simplify things, we'll copy all known hosts into an ArrayList, randomly shuffle the list,
     * and then select the first 'numOfNodes' from the list.
     */
    public synchronized List<NodeInfo> randomPeers(int numOfNodes) {
        List<NodeInfo> nodesCopy = new ArrayList<>(nodes);
        Collections.shuffle(nodesCopy);

        List<NodeInfo> randomSelection = new ArrayList<>(numOfNodes);

        Iterator<NodeInfo> shuffledNodesIt = nodesCopy.iterator();
        for (int i = 0; i < numOfNodes && shuffledNodesIt.hasNext(); ++i) {
            randomSelection.add(shuffledNodesIt.next());
        }

        return randomSelection;
    }

    public synchronized EndpointStateSnapshot recordCycle() {
        // set new HeartBit state value
        heartbitState =
                new HearbitState(Instant.now().getEpochSecond(), heartbitState.hearbit() + 1);

        // wait at least 3 gossip iterations till mark application status as NORMAL
        if (heartbitState.hearbit() == 3) {
            appState = new ApplicationState(AppStatus.NORMAL, 50.0);
        }
        return new EndpointStateSnapshot(heartbitState, appState);
    }
}
