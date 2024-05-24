package github.com.mstepan.gossip.state;

import github.com.mstepan.gossip.command.digest.DigestLine;
import github.com.mstepan.gossip.util.Preconditions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class DigestDiffCalculator {

    private static final Comparator<DigestLine> HOST_PORT_ASC =
            Comparator.comparing(DigestLine::getHost).thenComparingInt(DigestLine::getPort);

    private static final Comparator<DigestLine> GENERATION_THEN_HEARTBEAT_ASC =
            Comparator.comparingLong(DigestLine::getGeneration)
                    .thenComparingLong(DigestLine::getHeartbeat);

    /**
     * @param base - represents current node digest
     * @param other - represents received from anotehr node digest.
     * @return the difference between 2 digests.
     */
    public static List<DigestLine> diff(List<DigestLine> base, List<DigestLine> other) {
        Preconditions.checkNotNull(
                base, "Can't calculate digest difference, 'base' digest is null");
        Preconditions.checkNotNull(
                other, "Can't calculate digest difference, 'other' digest is null");

        base.sort(HOST_PORT_ASC);
        other.sort(HOST_PORT_ASC);

        Iterator<DigestLine> baseIt = base.iterator();
        Iterator<DigestLine> otherIt = other.iterator();

        List<DigestLine> digestDiff = new ArrayList<>();

        DigestLine baseLast = nextOrNull(baseIt);
        DigestLine otherLast = nextOrNull(otherIt);

        while (!(baseLast == null && otherLast == null)) {

            if (baseLast == null) {
                digestDiff.add(otherLast);
                otherLast = nextOrNull(otherIt);
            } else if (otherLast == null) {
                digestDiff.add(baseLast);
                baseLast = nextOrNull(baseIt);
            } else {

                // handling digest for the same host
                if (isSameHost(baseLast, otherLast)) {

                    int digestCmpRes = GENERATION_THEN_HEARTBEAT_ASC.compare(baseLast, otherLast);

                    // 'baseLast' has newer data
                    if (digestCmpRes > 0) {
                        digestDiff.add(baseLast);
                    }
                    // 'otherLast' has newer data
                    else if (digestCmpRes < 0) {
                        digestDiff.add(otherLast);
                    }
                }
                // different hosts, add the smallest hosts as new to a digest difference
                else {
                    int digestHostCmpRes = HOST_PORT_ASC.compare(baseLast, otherLast);

                    // move 'baseLast' forward
                    if (digestHostCmpRes > 0) {
                        digestDiff.add(baseLast);
                        baseLast = nextOrNull(baseIt);
                    }
                    // move 'otherLast' forward
                    else {
                        digestDiff.add(otherLast);
                        otherLast = nextOrNull(otherIt);
                    }
                }
            }
        }

        return digestDiff;
    }

    private static boolean isSameHost(DigestLine left, DigestLine right) {
        return left.getHost().equals(right.getHost()) && left.getPort() == right.getPort();
    }

    private static DigestLine nextOrNull(Iterator<DigestLine> it) {
        return it.hasNext() ? it.next() : null;
    }
}
