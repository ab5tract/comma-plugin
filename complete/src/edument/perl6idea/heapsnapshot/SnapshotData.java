package edument.perl6idea.heapsnapshot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Class to hold the actual raw data from a snapshot inside a heap snapshot collection.
 */
public class SnapshotData {
    public static final int KIND_OBJECT = 1;
    public static final int KIND_TYPE_OBJECT = 2;
    public static final int KIND_FRAME = 4;
    private final HeapSnapshotCollection snapshotCollection;

    public void forgetData() {
        collectableKind = null;
        collectableSize = null;
        collectableTypeOrFrameIndex = null;
        collectableReferencesCount = null;
        collectableReferencesStart = null;
        collectableUnmanagedSize = null;
        referenceDescription = null;
        referenceTarget = null;
        topIDs = null;
        topScores = null;
    }

    public SnapshotData(HeapSnapshotCollection collection) {
        this.snapshotCollection = collection;
    }

    /**
     * Will read all available datablocks into fields of the object, but an
     * exception inside a datablock will result in a null in the field rather
     * than an exception.
     *
     * @throws IOException if reading data from the file fails spectacularly
     */
    public void ensureData() throws IOException {
        for (TocEntry e : dataLocations) {
            Object datablock = HeapSnapshotCollection.readCompressedDatablock(this.sourceFile, e);
            switch (e.kind) {
                case "colkind":
                    collectableKind = (short[])datablock;
                    break;
                case "colsize":
                    collectableSize = (short[])datablock;
                    break;
                case "coltofi":
                    collectableTypeOrFrameIndex = (int[])datablock;
                    break;
                case "colrfcnt":
                    collectableReferencesCount = (int[])datablock;
                    break;
                case "colrfstr":
                    collectableReferencesStart = (long[])datablock;
                    break;
                case "colusize":
                    collectableUnmanagedSize = (long[])datablock;
                    break;
                case "refdescr":
                    referenceDescription = (long[])datablock;
                    break;
                case "reftrget":
                    referenceTarget = (long[])datablock;
                    break;
                case "topIDs":
                    topIDs = (long[])datablock;
                    break;
                case "topscore":
                    topScores = (long[])datablock;
                    break;
            }
        }
    }

    public static class StringData {
        public StringData() {
            stringDataPieces = new ArrayList<>();
            strings = new ArrayList<>(1024);
        }

        public final List<TocEntry> stringDataPieces;
        public final List<String> strings;
    }

    public static class StaticFrameData {
        public StaticFrameData() {
            namePieces = new ArrayList<>();
            cuidPieces = new ArrayList<>();
            filePieces = new ArrayList<>();
            linePieces = new ArrayList<>();
        }

        public final List<TocEntry> namePieces;
        public final List<TocEntry> cuidPieces;
        public final List<TocEntry> filePieces;
        public final List<TocEntry> linePieces;

        public int[] nameIndices;
        public int[] cuidIndices;
        public int[] fileIndices;
        public int[] lineIndices;
    }

    public static class TypeData {
        public TypeData() {
            reprnamePieces = new ArrayList<>();
            typenamePieces = new ArrayList<>();
        }

        public final List<TocEntry> reprnamePieces;
        public final List<TocEntry> typenamePieces;
        public int[] typenameIndices;
        public int[] reprnameIndices;
    }

    TocEntry positionOfToc;
    List<TocEntry> dataLocations;

    public short[] collectableKind;
    public short[] collectableSize;
    public int[] collectableTypeOrFrameIndex; // "type or frame index" based on kind
    public int[] collectableReferencesCount; // how many outgoing references does this collectable have?
    public long[] collectableReferencesStart;
    public long[] collectableUnmanagedSize;
    public long[] referenceDescription;
    public long[] referenceTarget;
    public long[] topIDs;
    public long[] topScores;

    RandomAccessFile sourceFile;

    public List<Reference> getReferences(int id) {
        List<Reference> refs = new ArrayList<>();

        int numRefs = collectableReferencesCount[id];
        long refsStart = collectableReferencesStart[id];

        for (int i = 0; i < numRefs; i++) {
            long refID = refsStart + i;
            long to = referenceTarget[(int)refID];
            long description = referenceDescription[(int)refID];
            long kind = description & 3;
            long index = description >> 2;
            refs.add(new Reference(to, (int)kind, (int)index));
        }

        return refs;
    }

    public static class Reference {
        public long target;
        public int kind;
        public int index;

        public Reference(long target, int kind, int index) {
            this.target = target;
            this.kind = kind;
            this.index = index;
        }
    }

    private List<Integer> distances;
    private List<Integer> preds;
    private List<Long> predRefs;

    private void ensureBFS() {
        if (distances != null)
            return;

        List<Integer> distances = new ArrayList<>();
        List<Integer> preds = new ArrayList<>();
        List<Long> predRefs = new ArrayList<>();
        List<Byte> color = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        color.add(0, (byte)1);
        distances.add(0, 0);
        preds.add(0, -1);
        predRefs.add(0, -1L);

        queue.push(0);

        while (!queue.isEmpty()) {
            int curCol = queue.removeFirst();
            int numRefs = collectableReferencesCount[curCol];
            long refsStart = collectableReferencesStart[curCol];
            for (int i = 0; i < numRefs; i++) {
                long refID = refsStart + i;
                int to = (int)referenceTarget[(int)refID];
                if (color.size() < to + 1) while (color.size() != to + 1) color.add((byte)0);
                if (color.get(to) == 0) {
                    color.set(to, (byte)1);
                    if (distances.size() < to + 1) while (distances.size() != to + 1) distances.add(0);
                    distances.set(to, distances.get(curCol) + 1);
                    if (preds.size() < to + 1) while (preds.size() != to + 1) preds.add(0);
                    preds.set(to, curCol);
                    if (predRefs.size() < to + 1) while (predRefs.size() != to + 1) predRefs.add(0L);
                    predRefs.set(to, refID);
                    queue.push(to);
                }
            }
            color.set(curCol, (byte)2);
        }

        this.distances = distances;
        this.preds = preds;
        this.predRefs = predRefs;
    }

    public Deque<String> getShortestPath(int id) {
        ensureBFS();
        Deque<String> path = new ArrayDeque<>();
        int curCol = id;

        while (curCol != -1) {
            path.addFirst(describeCol(curCol) + " (" + curCol + ")");

            Long predRef = predRefs.get(curCol);
            if (predRef >= 0) {
                long description = referenceDescription[Math.toIntExact(predRef)];
                // Can be 0 (unknown), 1 (ref) or 2 (str)
                long kind = description & 3;
                long index = description >> 2;
                switch ((int)kind) {
                    case 2: { // string
                        path.addFirst(snapshotCollection.stringData.strings.get((int)index));
                        break;
                    }
                    case 1: { // index
                        path.addFirst("Index " + index);
                        break;
                    }
                    default: { // 0 is unknown
                        path.addFirst("Unknown");
                    }
                }
            }
            curCol = preds.get(curCol);
        }

        return path;
    }

    public String describeCol(int col) {
        short kind = collectableKind[col];
        switch (kind) {
            case 1: // Object
                return getTypenameForObject(this, col, kind) + " (Object)";
            case 2: // Type object
                return getTypenameForObject(this, col, kind) + " (Type Object)";
            case 3: // STable
                return getTypenameForObject(this, col, kind) + " (STable)";
            case 4: // Frame
                return getTypenameForObject(this, col, kind) + " (Frame)";
            case 5: // PermRoots
                return "Permanent roots";
            case 6: // InstanceRoots
                return "VM Instance Roots";
            case 7: // CStackRoots
                return "C Stack Roots";
            case 8: // ThreadRoots
                return "Thread Roots";
            case 9: // Root
                return "Root";
            case 10: // InterGenerationalRoots
                return "Inter-generational Roots";
            case 11: // CallStackRoots
                return "Call Stack Roots";
            default:
                return "???";
        }
    }

    public String getTypenameForObject(SnapshotData data, int i, int kind) {
        return getTypename(getTypeOrFrameIndex(data.collectableTypeOrFrameIndex[i], kind));
    }

    public TypeOrFrameIndex getTypeOrFrameIndex(int i, int kind) {
        return new TypeOrFrameIndex(i, kind);
    }

    @NotNull
    public static String getTypename(TypeOrFrameIndex id) {
        String entry = id.fetch();
        return (entry == null ? "?" : entry) + "#" + id.index;
    }

    /**
     * Wrap typeOrFrameIndex in a data class so we're never confused
     *  if someone is an object ID or a staticFrameData/typeData ID.
     */
    public class TypeOrFrameIndex {
        public final int index;
        public final int kind;

        TypeOrFrameIndex(int index, int kind) {
            this.index = index;
            this.kind = kind;
        }

        @Nullable
        public String fetch() {
            return snapshotCollection.stringData.strings.get(fetchIndex());
        }

        public int fetchIndex() {
            return isFrame()
                   ? snapshotCollection.staticFrameData.nameIndices[index]
                   : snapshotCollection.typeData.typenameIndices[index];
        }

        public boolean isFrame() {
            return kind == KIND_FRAME;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeOrFrameIndex other = (TypeOrFrameIndex)o;
            return index == other.index &&
                   kind == other.kind;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, kind);
        }
    }
}
