package edument.perl6idea.heapsnapshot;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold the actual raw data from a snapshot inside a heap snapshot collection.
 */
public class SnapshotData {
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

    /**
     * Will read all available datablocks into fields of the object, but an
     * exception inside a datablock will result in a null in the field rather
     * than an exception.
     * @throws IOException if reading data from the file fails spectacularly
     */
    public void ensureData() throws IOException {
        for (TocEntry e : dataLocations) {
            Object datablock = HeapSnapshotCollection.readCompressedDatablock(this.sourceFile, e);
            switch (e.kind) {
                case "colkind":
                    collectableKind = (short[]) datablock;
                    break;
                case "colsize":
                    collectableSize = (short[]) datablock;
                    break;
                case "coltofi":
                    collectableTypeOrFrameIndex = (int[]) datablock;
                    break;
                case "colrfcnt":
                    collectableReferencesCount = (int[]) datablock;
                    break;
                case "colrfstr":
                    collectableReferencesStart = (long[]) datablock;
                    break;
                case "colusize":
                    collectableUnmanagedSize = (long[]) datablock;
                    break;
                case "refdescr":
                    referenceDescription = (long[]) datablock;
                    break;
                case "reftrget":
                    referenceTarget = (long[]) datablock;
                    break;
                case "topIDs":
                    topIDs = (long[]) datablock;
                    break;
                case "topscore":
                    topScores = (long[]) datablock;
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
}
