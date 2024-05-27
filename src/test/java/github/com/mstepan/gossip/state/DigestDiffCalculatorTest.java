package github.com.mstepan.gossip.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import github.com.mstepan.gossip.command.digest.DigestLine;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DigestDiffCalculatorTest {

    @Test
    public void diffSimpleCase() {

        List<DigestLine> curNodeDigest = new ArrayList<>();
        curNodeDigest.add(
                DigestLine.newBuilder()
                        .setHost("localhost")
                        .setPort(5001)
                        .setGeneration(100)
                        .setHeartbeat(3)
                        .putMetadata("DISK_USAGE", "75.00%")
                        .build());
        curNodeDigest.add(
                DigestLine.newBuilder()
                        .setHost("localhost")
                        .setPort(5002)
                        .setGeneration(0)
                        .setHeartbeat(0)
                        .build());

        List<DigestLine> otherNodeDigest = new ArrayList<>();
        otherNodeDigest.add(
                DigestLine.newBuilder()
                        .setHost("localhost")
                        .setPort(5002)
                        .setGeneration(200)
                        .setHeartbeat(15)
                        .putMetadata("DISK_USAGE", "5.00%")
                        .build());
        otherNodeDigest.add(
                DigestLine.newBuilder()
                        .setHost("localhost")
                        .setPort(5001)
                        .setGeneration(0)
                        .setHeartbeat(0)
                        .build());

        List<DigestLine> diff = DigestDiffCalculator.diff(curNodeDigest, otherNodeDigest);

        assertEquals(2, diff.size());

        DigestLine expectedLine1 =
                DigestLine.newBuilder()
                        .setHost("localhost")
                        .setPort(5001)
                        .setGeneration(100)
                        .setHeartbeat(3)
                        .putMetadata("DISK_USAGE", "75.00%")
                        .build();
        assertEquals(expectedLine1, diff.get(0));

        DigestLine expectedLine2 =
                DigestLine.newBuilder()
                        .setHost("localhost")
                        .setPort(5002)
                        .setGeneration(0)
                        .setHeartbeat(0)
                        .build();
        assertEquals(expectedLine2, diff.get(1));
    }
}
