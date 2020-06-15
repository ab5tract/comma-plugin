package edument.perl6idea.heapsnapshot;

/**
 * Class to hold metadata from a Snapshot that is part of a HeapSnapshotCollection.
 */
public class Snapshot {
    final Long snapTime;
    final Long gcSeqNum;
    final Integer snapshotIndex;
    final Long totalHeapSize;
    final Long totalObjects;
    final Long totalTypeobjects;
    final Long totalStables;
    final Long totalFrames;
    final Long totalRefs;

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
