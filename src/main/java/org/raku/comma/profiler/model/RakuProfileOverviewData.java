package org.raku.comma.profiler.model;

public class RakuProfileOverviewData {
    public int spesh;
    public int jit;
    public int deopted;
    public int globalDeopt;
    public int OSR;
    public int allocated;
    public int replaced;
    public float totalTime;
    public float speshTime;
    public int threads;
    public float gcTotalTime;
    public int gcNumber;
    public int fullGCNumber;
    public int entriesTotal;
    public int inlineEntriesTotal;
    public int minMinorTime;
    public int maxMinorTime;
    public int avgMinorTime;
    public int minMajorTime;
    public int maxMajorTime;
    public int avgMajorTime;
    public int totalMajor;
    public int totalMinor;
}
