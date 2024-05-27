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
     * @param cur - represents current node digest
     * @param other - represents received from another node digest.
     * @return the difference between 2 digests.
     */
    public static List<DigestLine> diff(List<DigestLine> cur, List<DigestLine> other) {
        Preconditions.checkNotNull(cur, "Can't calculate digest difference, 'base' digest is null");
        Preconditions.checkNotNull(
                other, "Can't calculate digest difference, 'other' digest is null");

        // Sort both lists so that we can do merge using single pass
        cur.sort(HOST_PORT_ASC);
        other.sort(HOST_PORT_ASC);

        Iterator<DigestLine> curIt = cur.iterator();
        Iterator<DigestLine> otherIt = other.iterator();

        List<DigestLine> digestDiff = new ArrayList<>();

        DigestLine curLast = nextOrNull(curIt);
        DigestLine otherLast = nextOrNull(otherIt);

        while (!(curLast == null && otherLast == null)) {

            if (curLast == null) {
                digestDiff.add(otherLast);
                otherLast = nextOrNull(otherIt);
            } else if (otherLast == null) {
                digestDiff.add(curLast);
                curLast = nextOrNull(curIt);
            } else {

                // handling digest for the same host
                if (isSameHost(curLast, otherLast)) {

                    int digestCmpRes = GENERATION_THEN_HEARTBEAT_ASC.compare(curLast, otherLast);

                    // 'curLast' has newer data, send digest line with metadata
                    if (digestCmpRes > 0) {
                        digestDiff.add(curLast);
                    }
                    // 'curLast' has older data, send digest line without metadata
                    else if (digestCmpRes < 0) {
                        digestDiff.add(removeMetadata(curLast));
                    }

                    curLast = nextOrNull(curIt);
                    otherLast = nextOrNull(otherIt);
                }
                // different hosts, add the smallest hosts as new to a digest difference
                else {
                    int digestHostCmpRes = HOST_PORT_ASC.compare(curLast, otherLast);

                    // move 'baseLast' forward
                    if (digestHostCmpRes > 0) {
                        digestDiff.add(curLast);
                        curLast = nextOrNull(curIt);
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

    private static DigestLine removeMetadata(DigestLine line) {
        return DigestLine.newBuilder()
                .setHost(line.getHost())
                .setPort(line.getPort())
                .setGeneration(line.getGeneration())
                .setHeartbeat(line.getHeartbeat())
                .build();
    }

    private static boolean isSameHost(DigestLine left, DigestLine right) {
        return left.getHost().equals(right.getHost()) && left.getPort() == right.getPort();
    }

    private static DigestLine nextOrNull(Iterator<DigestLine> it) {
        return it.hasNext() ? it.next() : null;
    }
}
