package edument.perl6idea.coverage;

public class CoverageStatistics {
    private final int coveredLines;
    private final int coverableLines;

    public CoverageStatistics(int coveredLines, int coverableLines) {
        this.coveredLines = coveredLines;
        this.coverableLines = coverableLines;
    }

    public int getCoveredLines() {
        return coveredLines;
    }

    public int getCoverableLines() {
        return coverableLines;
    }

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
