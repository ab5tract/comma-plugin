package org.raku.coverage;

public record CoverageStatistics(int coveredLines, int coverableLines) {

    public int percent() {
        return (int)Math.round(100 * (double)coveredLines / (double)coverableLines);
    }

    public boolean isPoor() {
        return (double)coveredLines / (double)coverableLines < 0.05;
    }

    public boolean isGood() {
        return (double)coveredLines / (double)coverableLines > 0.75;
    }
}
