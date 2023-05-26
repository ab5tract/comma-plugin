package edument.perl6idea.heapsnapshot;

import com.intellij.openapi.diagnostic.Logger;
import io.airlift.compress.MalformedInputException;
import io.airlift.compress.zstd.ZstdDecompressor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HeapSnapshotCollection {
    static final Logger LOG = Logger.getInstance(HeapSnapshotCollection.class);

    /**
     * A reference to the file this data was made from.
     *
     * Necessary because data is read lazily.
     */
    final RandomAccessFile inputFile;

    /**
     * The outermost TOC of the file points at one TOC for each snapshot. They go here.
     */
    List<TocEntry> snapshotTocEntries;
    /**
     * TOCs in the file that have metadata; filemeta or snapmeta exist at the moment.
     */
    List<TocEntry> metadataTocEntries;

    /**
     * List of Snapshot objects populated from snapmeta datablocks.
     */
    public final List<Snapshot> snapshotList;
    /**
     * List of SnapshotData objects, potentially without data, ready to be reified.
     */
    List<SnapshotData> snapshotDataList;

    /**
     * String data, which is made up from multiple datablocks across the file.
     */
    public final SnapshotData.StringData stringData;
    /**
     * Static Frame data, which is made up from multiple datablocks across the file.
     */
    public final SnapshotData.StaticFrameData staticFrameData;
    /**
     * Type data, which is made up from multiple datablocks across the file.
     */
    public final SnapshotData.TypeData typeData;

    /**
     * The filemeta specifies a sub-version of the format for smaller changes to the format.
     */
    Long metadataSubversion;
    /**
     * Time when the heap snapshot collection was created inside MoarVM.
     */
    Long startTime;
    /**
     * PID recorded when the heap snapshot collection was created inside MoarVM.
     */
    Long pid;

    /**
     * The highscore data inside the snapshot is just two blobs, "topIDs" and "topscores",
     * the meaning of the numbers inside is derived from this structure information.
     * By default, moarvm spits out 40 entries for each "highscore table", and the
     * tables that are written out are "types_by_count", "frames_by_count", "types_by_size",
     * and "frames_by_size".
     * The topIDs and topscores blobs will thus contain 40 entries giving IDs of types in one
     * and their count in the other, then 40 entries giving IDs of frames / counts of frames,
     * then 40 more for frames/count and another 40 for types/size.
     */
    HighscoreStructure highscoreStructure;

    public HeapSnapshotCollection(RandomAccessFile inputFile) throws IOException {
        this.inputFile = inputFile;
        long outerTocPosition;
        /* Last written TOC has its address put at the very end of the file */
        final long endOfOuterToc = inputFile.length() - 8;
        String greeting = readGreeting(inputFile);
        if (!Objects.equals(greeting, "MoarHeapDumpv003"))
            throw new IOException("Could not parse heap file, wrong format, expected version 3, got: '" + greeting + "'");
        inputFile.seek(endOfOuterToc);
        outerTocPosition = readLong(inputFile);

        /* Every individual snapshot in the file has a TOC entry in the outermost table.
        * These are of type "toc" themselves. */
        snapshotTocEntries = new ArrayList<>(16);
        metadataTocEntries = new ArrayList<>(4);

        /* snapshotList contains Snapshot objects which hold metadata, like counts */
        snapshotList = new ArrayList<>(16);
        /* snapshotDataList contains SnapshotData objects which hold (lazily) the actual data from the
         * compressed blobs. */
        snapshotDataList = new ArrayList<>(16);

        /* strings, staticframe data and type data is shared across snapshots, but since the snapshot
        * profiler writes the file one snapshot at a time, there can be additional strings/sfs/types for a
        * subset of the snapshots. Their data just gets concatenated after decompression - the compression
        * is not "linked", however; it's a fresh compression state each time. */
        stringData = new SnapshotData.StringData();
        staticFrameData = new SnapshotData.StaticFrameData();
        typeData = new SnapshotData.TypeData();

        TocEntry[] outerToc = readTocAt(inputFile, outerTocPosition);

        List<List<TocEntry>> partsOfSnapshots = new ArrayList<>();

        for (TocEntry e : outerToc) {
            if (e.kind.equals("filemeta")) {
                inputFile.seek(e.position);
                final String foundKind = readKindAsString(inputFile);
                if (!foundKind.equals(e.kind)) {
                    throw new IllegalStateException("Excepted kind of block to match: " + foundKind + " != " + e.kind);
                }

                int contentLength = Math.toIntExact(readLong(inputFile));

                byte[] content = new byte[contentLength];
                inputFile.readFully(content, 0, contentLength);

                String metaContent = new String(content, StandardCharsets.UTF_8);
                JSONObject metadata = new JSONObject(metaContent);
                highscoreStructure = new HighscoreStructure();
                JSONObject highscoreData = metadata.getJSONObject("highscore_structure");
                highscoreStructure.entryCount = (highscoreData.opt("entry_count") == null) ? 0 : highscoreData.getLong("entry_count");
                highscoreStructure.dataOrder = new ArrayList<>(4);
                JSONArray dataOrder = (highscoreData.opt("data_order") == null) ? new JSONArray() : highscoreData.getJSONArray("data_order");
                for (int index = 0; index < dataOrder.length(); index++) {
                    try {
                        highscoreStructure.dataOrder.add(index, dataOrder.getString(index));
                    } catch (JSONException jsonException) {
                        LOG.error("Metadata contained something that is not a string at index " + index, jsonException);
                    }
                }

                pid = (metadata.opt("pid") == null) ? 0 : metadata.getLong("pid");
                startTime = (metadata.opt("start_time") == null) ? 0 : metadata.getLong("start_time");
                metadataSubversion = (metadata.opt("subversion") == null) ? 0 : metadata.getLong("subversion");

                metadataTocEntries.add(e);
            } else if (e.kind.equals("toc")) {
                final TocEntry[] innerTocEntries = readTocAt(inputFile, e.position);
                snapshotTocEntries.add(e);
                partsOfSnapshots.add(Arrays.asList(innerTocEntries));
            }
        }
        int snapshotIndex = 0;
        for (List<TocEntry> innerParts : partsOfSnapshots) {
            List<TocEntry> dataPieces = new ArrayList<>(8);
            Snapshot snapshotObject;
            SnapshotData snapshotData = new SnapshotData(this);
            for (TocEntry innerToc : innerParts) {
                if (innerToc.kind.equals("snapmeta")) {
                    inputFile.seek(innerToc.position);
                    final String foundKind = readKindAsString(inputFile);
                    if (!foundKind.equals(innerToc.kind)) {
                        throw new IllegalStateException("Excepted kind of block to match: " + foundKind + " != " + innerToc.kind);
                    }

                    int contentLength = Math.toIntExact(readLong(inputFile));

                    byte[] content = new byte[contentLength];
                    inputFile.readFully(content, 0, contentLength);

                    String metaContent = new String(content, StandardCharsets.UTF_8);
                    JSONObject metadata = new JSONObject(metaContent);
                    snapshotObject = new Snapshot(snapshotIndex,
                            (metadata.opt("snap_time") == null) ? null : metadata.getLong("snap_time"),
                            (metadata.opt("gc_seq_num") == null) ? null : metadata.getLong("gc_seq_num"),
                            (metadata.opt("total_heap_size") == null) ? null : metadata.getLong("total_heap_size"),
                            (metadata.opt("total_objects") == null) ? null : metadata.getLong("total_objects"),
                            (metadata.opt("total_typeobjects") == null) ? null : metadata.getLong("total_typeobjects"),
                            (metadata.opt("total_stables") == null) ? null : metadata.getLong("total_stables"),
                            (metadata.opt("total_frames") == null) ? null : metadata.getLong("total_frames"),
                            (metadata.opt("total_refs") == null) ? null : metadata.getLong("total_refs")
                    );
                    snapshotList.add(snapshotIndex, snapshotObject);
                    metadataTocEntries.add(innerToc);
                } else {
                    inputFile.seek(innerToc.position);
                    String kind = readKindAsString(inputFile);

                    switch (kind) {
                        case "strings":
                            stringData.stringDataPieces.add(innerToc);
                            readIntoStringHeap(inputFile, innerToc, stringData.strings);
                            break;
                        case "typename":
                            typeData.typenamePieces.add(innerToc);
                            break;
                        case "reprname":
                            typeData.reprnamePieces.add(innerToc);
                            break;
                        case "sfname":
                            staticFrameData.namePieces.add(innerToc);
                            break;
                        case "sfline":
                            staticFrameData.linePieces.add(innerToc);
                            break;
                        case "sffile":
                            staticFrameData.filePieces.add(innerToc);
                            break;
                        case "sfcuid":
                            staticFrameData.cuidPieces.add(innerToc);
                            break;
                        case "colkind":
                        case "colsize":
                        case "coltofi":
                        case "colrfcnt":
                        case "colrfstr":
                        case "colusize":
                        case "refdescr":
                        case "reftrget":
                        case "topIDs":
                        case "topscore":
                            dataPieces.add(innerToc);
                            break;
                        default:
                            LOG.error("Unrecognized inner kind: " + kind);
                    }
                }
            }
            snapshotData.positionOfToc = snapshotTocEntries.get(snapshotIndex);
            snapshotData.dataLocations = dataPieces;
            snapshotData.sourceFile = inputFile;
            snapshotDataList.add(snapshotIndex, snapshotData);
            snapshotIndex++;
        }

        typeData.typenameIndices = readAllCompressedDatablocksInt(inputFile, typeData.typenamePieces);
        typeData.reprnameIndices = readAllCompressedDatablocksInt(inputFile, typeData.reprnamePieces);
        staticFrameData.nameIndices = readAllCompressedDatablocksInt(inputFile, staticFrameData.namePieces);
        staticFrameData.lineIndices = readAllCompressedDatablocksInt(inputFile, staticFrameData.linePieces);
        staticFrameData.fileIndices = readAllCompressedDatablocksInt(inputFile, staticFrameData.filePieces);
        staticFrameData.cuidIndices = readAllCompressedDatablocksInt(inputFile, staticFrameData.cuidPieces);
    }

    /* Reads all the TOCs for a given list and returns a single array with all elements appended.
     */
    static int[] readAllCompressedDatablocksInt(RandomAccessFile f, List<TocEntry> tocs) throws IOException {
        int[] result = new int[0];

        for (TocEntry toc : tocs) {
            int[] o = (int[]) readCompressedDatablock(f, toc);

            int offset = result.length;
            result = Arrays.copyOf(result, offset + o.length);
            System.arraycopy(o, 0, result, offset, o.length);
        }
        return result;
    }

    static String readGreeting(RandomAccessFile f) throws IOException {
        byte[] greetData = new byte[16];
        if (f.length() > 16)
            f.readFully(greetData, 0, 16);
        else
            return null;
        return new String(greetData, StandardCharsets.UTF_8);
    }

    static Long readLong(RandomAccessFile f) throws IOException {
        byte[] longData = new byte[8];
        f.readFully(longData, 0, 8);
        ByteBuffer b = ByteBuffer.wrap(longData);
        b.order(ByteOrder.LITTLE_ENDIAN);
        return b.getLong();
    }

    static Short readShort(RandomAccessFile f) throws IOException {
        byte[] longData = new byte[2];
        f.readFully(longData, 0, 2);
        ByteBuffer b = ByteBuffer.wrap(longData);
        b.order(ByteOrder.LITTLE_ENDIAN);
        return b.getShort();
    }

    /** Read a compressed datablock that has its start and end given by the toc from the file f
     * @returns null if decompression fails, otherwise a low-level array of integer, long, short, or byte.
     */
    static Object readCompressedDatablock(RandomAccessFile f, TocEntry toc) throws IOException {
        /* Seek the beginning of the TOC, ignore the kind */
        f.seek(toc.position);
        readKindAsString(f);

        short sizePerEntry = readShort(f);
        /* Blocks start with their size, but since that requires seeking backwards after writing,
         * or buffering a load up front, they sometimes end up 0; we rely on the toc instead, since
         * that is always written afterwards. */
        Long size = readLong(f);

        byte[] wholeBlock = new byte[Math.toIntExact(toc.end - f.getFilePointer())];
        f.readFully(wholeBlock, 0, wholeBlock.length);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(wholeBlock.length);
        inputBuffer.put(wholeBlock);
        inputBuffer.flip();

        /* we make a very rough estimation that the result will be around
         * 2x as big as the input. It's often better than that, haven't done any
         * measurement of this yet. */
        ByteBuffer resultBuffer = ByteBuffer.allocateDirect((wholeBlock.length >> 3) << 4);

        ZstdDecompressor decompressor = new ZstdDecompressor();
        while (true) {
            try {
                decompressor.decompress(inputBuffer, resultBuffer);
                break;
            }
            catch (MalformedInputException | IllegalArgumentException e) {
                if (e.getMessage().contains("too small") || e.getMessage().contains("limit")) {
                    resultBuffer = ByteBuffer.allocateDirect(resultBuffer.limit() * 2);
                }
                else {
                    throw e;
                }
            }
        }
        resultBuffer.flip();
        resultBuffer.order(ByteOrder.LITTLE_ENDIAN);

        if (sizePerEntry == 8) {
            LongBuffer castedBuffer = resultBuffer.asLongBuffer();
            long[] castedFinalResult = new long[castedBuffer.limit()];
            castedBuffer.get(castedFinalResult, 0, castedBuffer.limit());
            return castedFinalResult;
        }
        else if (sizePerEntry == 4) {
            IntBuffer castedBuffer = resultBuffer.asIntBuffer();
            int[] castedFinalResult = new int[castedBuffer.limit()];
            castedBuffer.get(castedFinalResult, 0, castedBuffer.limit());
            return castedFinalResult;
        }
        else if (sizePerEntry == 2) {
            ShortBuffer castedBuffer = resultBuffer.asShortBuffer();
            short[] castedFinalResult = new short[castedBuffer.limit()];
            castedBuffer.get(castedFinalResult, 0, castedBuffer.limit());
            return castedFinalResult;
        }
        else {
            ByteBuffer castedBuffer = resultBuffer.asReadOnlyBuffer();
            byte[] castedFinalResult = new byte[castedBuffer.limit()];
            castedBuffer.get(castedFinalResult, 0, castedBuffer.limit());
            return castedFinalResult;
        }
    }

    static void readIntoStringHeap(RandomAccessFile f, TocEntry toc, List<String> strings) throws IOException {
        Long size = readLong(f);

        byte[] wholeBlock = new byte[Math.toIntExact(toc.end - f.getFilePointer())];
        f.readFully(wholeBlock, 0, wholeBlock.length);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(wholeBlock.length);
        inputBuffer.put(wholeBlock);
        inputBuffer.flip();

        byte[] finalResult;
        int pos;

        ByteBuffer resultBuffer = ByteBuffer.allocateDirect(((wholeBlock.length >> 3) << 4));
        resultBuffer.order(ByteOrder.LITTLE_ENDIAN);

        finalResult = new byte[(wholeBlock.length >> 1) << 2];

        pos = 0;

        /* Read the entire compressed block at once */
        ZstdDecompressor decompressor = new ZstdDecompressor();
        while (true) {
            try {
                decompressor.decompress(inputBuffer, resultBuffer);
                break;
            } catch (MalformedInputException e) {
                if (e.getMessage().contains("too small")) {
                    resultBuffer = ByteBuffer.allocateDirect(resultBuffer.limit() * 2);
                } else {
                    throw e;
                }
            }
        }

        resultBuffer.flip();
        final int amountToTake = resultBuffer.limit() - resultBuffer.position();
        if (pos + amountToTake > finalResult.length) {
            byte[] orig = finalResult;
            finalResult = new byte[Math.max(finalResult.length * 2, finalResult.length + amountToTake)];
            System.arraycopy(orig, 0, finalResult, 0, orig.length);
        }
        resultBuffer.get(finalResult, pos, amountToTake);
        pos += amountToTake;

        /* Split apart strings; they are stored as a length prefix and then raw utf8 data */

        ByteBuffer stringReadBuffer = ByteBuffer.wrap(finalResult);
        stringReadBuffer.limit(pos);
        stringReadBuffer.order(ByteOrder.LITTLE_ENDIAN);
        while (stringReadBuffer.hasRemaining()) {
            int strlen = stringReadBuffer.getInt();
            byte[] stringBytes = new byte[strlen];

            stringReadBuffer.get(stringBytes, 0, strlen);
            String toAdd = new String(stringBytes, StandardCharsets.UTF_8);
            strings.add(toAdd);
        }
    }

    static TocEntry[] readTocAt(RandomAccessFile f, long outerTocPosition) throws IOException {
        f.seek(outerTocPosition);

        String outerTocKind = readKindAsString(f);

        if (!outerTocKind.equals("toc")) {
            throw new IllegalStateException("expected end-of-file offset to point at a TOC; saw this instead: " + outerTocKind);
        }

        int tocCount = Math.toIntExact(readLong(f));
        TocEntry[] outerToc = new TocEntry[tocCount];

        for (int ti = 0; ti < tocCount; ti++) {
            byte[] data = new byte[24];
            f.readFully(data, 0, 24);
            outerToc[ti] = new TocEntry(data);
        }
        return outerToc;
    }

    static String readKindAsString(RandomAccessFile f) throws IOException {
        /* Kinds are 8-byte long fields that look very much like ascii strings, but don't really
         * mean anything apart from an identifier that will be matched. However, only up to the first
         * null byte will be used (a few versions of moarvm wrote garbage after the initial piece of text)
         */
        byte[] kind = new byte[8];
        f.readFully(kind, 0, 8);
        return new String(TocEntry.grabKindWithoutZeroes(ByteBuffer.wrap(kind)), StandardCharsets.UTF_8);
    }


    /**
     * Access a snapshot at a given index. Will cause data to be read from the file.
     *
     * @param snapshotIndex Index of the snapshot in question. These indices start at 0 and are contiguous.
     * @return A SnapshotData object with all its data decompressed and ready to be used.
     * @throws ArrayIndexOutOfBoundsException if the snapshot index is invalid.
     * @throws IOException if the file used to create this HeapSnapshotCollection couldn't be read for some reason
     */
    public SnapshotData getSnapshotData(int snapshotIndex) throws ArrayIndexOutOfBoundsException, IOException {
        if (snapshotIndex >= snapshotTocEntries.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        SnapshotData snapshotData = snapshotDataList.get(snapshotIndex);
        snapshotData.ensureData();
        return snapshotData;
    }

    static class HighscoreStructure {
        Long entryCount;
        List<String> dataOrder;
    }
}
