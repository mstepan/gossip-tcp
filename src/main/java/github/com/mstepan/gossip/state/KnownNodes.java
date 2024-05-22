package github.com.mstepan.gossip.state;

import static github.com.mstepan.gossip.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Keep a list of all nodes that the current host knows about. */
public enum KnownNodes {
    INST;

    private final Set<NodeInfo> nodes = new HashSet<>();

    public void addNode(NodeInfo node) {
        System.out.printf("Node added %s%n", node);

        checkNotNull(node, "Can't add null 'node'.");
        nodes.add(node);
    }

    /**
     * To simplify things, we'll copy all known hosts into an ArrayList, randomly shuffle the list,
     * and then select the first 'numOfNodes' from the list, skipping the current node.
     */
    public List<NodeInfo> randomPeers(int numOfNodes) {
        List<NodeInfo> nodesCopy = new ArrayList<>(nodes);
        Collections.shuffle(nodesCopy);

        List<NodeInfo> randomSelection = new ArrayList<>(numOfNodes);

        Iterator<NodeInfo> shuffledNodesIt = nodesCopy.iterator();
        for (int i = 0; i < numOfNodes && shuffledNodesIt.hasNext(); ++i) {
            NodeInfo candidate = shuffledNodesIt.next();

            if (!candidate.currentNode()) {
                randomSelection.add(candidate);
            }
        }

        return randomSelection;
    }
}
