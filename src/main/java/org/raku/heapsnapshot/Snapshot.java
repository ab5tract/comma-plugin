package org.raku.heapsnapshot;

/**
 * Class to hold metadata from a Snapshot that is part of a HeapSnapshotCollection.
 */
public class Snapshot {
    final public Long snapTime;
    final public Long gcSeqNum;
    final public Integer snapshotIndex;
    final public Long totalHeapSize;
    final public Long totalObjects;
    final public Long totalTypeobjects;
    final public Long totalStables;
    final public Long totalFrames;
    final public Long totalRefs;

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
