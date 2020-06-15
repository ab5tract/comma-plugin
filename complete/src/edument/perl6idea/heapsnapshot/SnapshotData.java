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
            sourceFile.seek(e.position);
            HeapSnapshotCollection.readKindAsString(sourceFile);

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

        List<TocEntry> stringDataPieces;

        List<String> strings;
    }

    public static class StaticFrameData {
        public StaticFrameData() {
            namePieces = new ArrayList<>();
            cuidPieces = new ArrayList<>();
            filePieces = new ArrayList<>();
            linePieces = new ArrayList<>();
        }

        List<TocEntry> namePieces;
        List<TocEntry> cuidPieces;
        List<TocEntry> filePieces;
        List<TocEntry> linePieces;
    }

    public static class TypeData {
        public TypeData() {
            reprnamePieces = new ArrayList<>();
            typenamePieces = new ArrayList<>();
        }

        List<TocEntry> reprnamePieces;
        List<TocEntry> typenamePieces;
    }

    TocEntry positionOfToc;
    List<TocEntry> dataLocations;

    short[] collectableKind;
    short[] collectableSize;
    int[] collectableTypeOrFrameIndex; // "type or frame index" based on kind
    int[] collectableReferencesCount; // how many outgoing references does this collectable have?
    long[] collectableReferencesStart;
    long[] collectableUnmanagedSize;
    long[] referenceDescription;
    long[] referenceTarget;
    long[] topIDs;
    long[] topScores;

    RandomAccessFile sourceFile;
}
