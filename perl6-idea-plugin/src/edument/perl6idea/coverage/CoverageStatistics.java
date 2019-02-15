// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.coverage;

import java.text.DecimalFormat;

public class CoverageStatistics {
    private static final DecimalFormat percentFormat = new DecimalFormat("###.0");

    private int coveredLines;
    private int coverableLines;

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
