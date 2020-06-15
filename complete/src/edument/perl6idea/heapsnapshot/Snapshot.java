package edument.perl6idea.heapsnapshot;

/**
 * Class to hold metadata from a Snapshot that is part of a HeapSnapshotCollection.
 */
public class Snapshot {
    Long snapTime;
    Long gcSeqNum;
    Integer snapshotIndex;
    Long totalHeapSize;
    Long totalObjects;
    Long totalTypeobjects;
    Long totalStables;
    Long totalFrames;
    Long totalRefs;

    public Snapshot(Integer snapshotIndex,
                    Long snapTime,
                    Long gcSeqNum,
                    Long totalHeapSize,
                    Long totalObjects,
                    Long totalTypeobjects,
                    Long totalStables,
                    Long totalFrames,
                    Long totalRefs) {
        this.snapshotIndex = snapshotIndex;
        this.snapTime = snapTime;
        this.gcSeqNum = gcSeqNum;
        this.totalHeapSize = totalHeapSize;
        this.totalObjects = totalObjects;
        this.totalTypeobjects = totalTypeobjects;
        this.totalStables = totalStables;
        this.totalFrames = totalFrames;
        this.totalRefs = totalRefs;
    }
}
