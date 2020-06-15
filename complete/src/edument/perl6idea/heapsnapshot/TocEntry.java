package edument.perl6idea.heapsnapshot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A class that represents an entry in a TOC block inside a HeapSnapshotCollection file.
 *
 * In the file itself, it is made up of 8 bytes giving an identifier (behaves a lot like a string) followed
 * by a starting position and end position relative to the file's start.
 */
public class TocEntry {
    public final String kind;
    public final Long position;
    public final Long end;

    public Integer length() {
        return Math.toIntExact(end - position);
    }

    public TocEntry(byte[] input) {
        this(ByteBuffer.wrap(input));
    }

    public TocEntry(ByteBuffer input) {
        byte[] kindBytes = grabKindWithoutZeroes(input);

        input.order(ByteOrder.LITTLE_ENDIAN);

        kind = new String(kindBytes,
                StandardCharsets.UTF_8);
        position = input.getLong();
        end = input.getLong();
    }

    static byte[] grabKindWithoutZeroes(ByteBuffer input) {
        byte[] kindBytes = new byte[8];
        input.get(kindBytes, 0, 8);
        for (int i = 0; i < 8; i++) {
            if (kindBytes[i] == 0) {
                kindBytes = Arrays.copyOfRange(kindBytes, 0, i);
                return kindBytes;
            }
        }
        return kindBytes;
    }

    static TocEntry[] readAll(ByteBuffer input) {
        if (input.remaining() % 24 != 0) {
            throw new IllegalArgumentException("Expected a multiple of 24 bytes to read TOCs");
        }
        final int entries = input.remaining() / 24;
        final TocEntry[] tocEntries = new TocEntry[entries];
        for (int i = 0; i < entries; i++) {
            tocEntries[i] = new TocEntry(input);
        }
        return tocEntries;
    }
}
