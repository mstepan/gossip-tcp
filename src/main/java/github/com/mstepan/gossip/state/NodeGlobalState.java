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
 * Tracks gossip node state. Should be thread-safe. We will leverage enum-singleton to prevent the
 * instantiation of multiple instances of this class.
 */
public enum NodeGlobalState {
    INST;

    /** Keep a set of all nodes that the current host knows about. */
    private final Set<NodeInfo> nodes;

    private NodeInfo current;

    /** Epoch timestamp in seconds. */
    private long generation;

    /** Monotonically increased counter. Increment during each gossip cycle. */
    private long heartbit;

    /** Node status. For local usage only. Should not be shared during gossip communication. */
    private NodeStatus status;

    NodeGlobalState() {
        nodes = new HashSet<>();
        generation = Instant.now().getEpochSecond();
        heartbit = 0;
        status = NodeStatus.BOOTSTRAP;
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

    public synchronized NodeStateSnapshot recordCycle() {
        generation = Instant.now().getEpochSecond();
        ++heartbit;
        return snapshot();
    }

    public synchronized NodeStateSnapshot snapshot() {

        return new NodeStateSnapshot(generation, heartbit, status);
    }
}
