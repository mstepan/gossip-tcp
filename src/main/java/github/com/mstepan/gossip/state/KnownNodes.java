package github.com.mstepan.gossip.state;

import static github.com.mstepan.gossip.util.Preconditions.checkNotNull;

import java.util.HashSet;
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

    public List<NodeInfo> randomPeers(int numOfNodes) {

        return null;
    }
}
