package org.raku.comma.profiler.model;

public class Perl6ProfileThread {
    public int threadID;
    public int rootNodeID;

    public Perl6ProfileThread(int threadID, int rootNodeID) {
        this.threadID = threadID;
        this.rootNodeID = rootNodeID;
    }
}
