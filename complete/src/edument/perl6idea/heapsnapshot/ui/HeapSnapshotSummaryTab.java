package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.util.text.StringUtilRt;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;
import edument.perl6idea.heapsnapshot.Snapshot;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.util.function.ToLongFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class HeapSnapshotSummaryTab extends JPanel {
    private final HeapSnapshotCollection snapshotCollection;
    private final double[] indices;

    public HeapSnapshotSummaryTab(HeapSnapshotCollection snapshotCollection) {
        super(new MigLayout("ins 0, wrap 1, fillx"));
        this.snapshotCollection = snapshotCollection;
        this.indices = IntStream.range(0, snapshotCollection.snapshotList.size()).mapToDouble(i -> i).toArray();

        CC componentConstraints = new CC();
        componentConstraints.gapBottom("10px");
        add(new JLabel("<html>" + summarize() + "</html>"), componentConstraints);
        add(new XChartPanel<>(getHeapsizeChart()), componentConstraints);
        add(new XChartPanel<>(getObjectsChart()), componentConstraints);
        add(new XChartPanel<>(getFramesChart()), componentConstraints);
        add(new XChartPanel<>(getTypeObjectsChart()), componentConstraints);
    }

    private XYChart getObjectsChart() {
        XYChart chart = makeAreaChart("Objects");
        double[] objects = snapshotLongStream(h -> h.totalObjects).mapToDouble(i -> i).toArray();
        chart.addSeries("Total Objects", indices, objects);
        return chart;
    }

    private XYChart getFramesChart() {
        XYChart chart = makeAreaChart("Frames");
        double[] frames = snapshotLongStream(h -> h.totalFrames).mapToDouble(i -> i).toArray();
        chart.addSeries("Total Frames", indices, frames);
        return chart;
    }

    private XYChart getTypeObjectsChart() {
        XYChart chart = makeAreaChart("Type Objects");
        double[] types = snapshotLongStream(h -> h.totalTypeobjects).mapToDouble(i -> i).toArray();
        chart.addSeries("Total Type Objects", indices, types);
        return chart;
    }

    private XYChart getHeapsizeChart() {
        XYChart chart = makeAreaChart("Heapsize");
        double[] heapsizes = snapshotLongStream(h -> h.totalHeapSize).mapToDouble(i -> i / 1024f / 1024f).toArray();
        chart.addSeries("Total Heap Size, MB", indices, heapsizes);
        return chart;
    }

    @NotNull
    private static XYChart makeAreaChart(String title) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title(title).build();

        // Switch to area chart for memory representation
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        return chart;
    }

    private String summarize() {
        int size = snapshotCollection.snapshotList.size();
        if (size == 0) {
            return "There are <b>no snapshots</b>.";
        }

        StringBuilder s = new StringBuilder();
        if (size == 1) {
            s.append("There is <b>a single</b> snapshot.<br>");
        } else {
            s.append("There are <b>").append(size).append(" snapshots</b>.<br>");
        }

        int typeCount = snapshotCollection.typeData.typenameIndices.length;
        if (typeCount == 1) {
            s.append("There is <b>").append(typeCount).append(" distinct type object</b>.<br>");
        } else if (typeCount > 0) {
            s.append("There are <b>").append(typeCount).append(" distinct type objects</b>.<br>");
        }

        int staticFrameCount = snapshotCollection.staticFrameData.nameIndices.length;
        if (staticFrameCount == 1) {
            s.append("There is <b>").append(staticFrameCount).append(" static frame</b>.<br>");
        } else if (staticFrameCount > 0) {
            s.append("There are <b>").append(staticFrameCount).append(" static frames</b>.<br>");
        }

        long highestHeapSize = snapshotLongStream(h -> h.totalHeapSize).max().getAsLong();
        s.append("The heap size was <b>at most ").append(StringUtilRt.formatFileSize(highestHeapSize)).append("</b> ");
        long lowestHeapSize = snapshotLongStream(h -> h.totalHeapSize).min().getAsLong();
        s.append("and <b>at least ").append(StringUtilRt.formatFileSize(lowestHeapSize)).append("</b> ");
        long avgHeapSize = (long)snapshotLongStream(h -> h.totalHeapSize).average().getAsDouble();
        s.append("for an <b>average of ").append(StringUtilRt.formatFileSize(avgHeapSize)).append("</b>.<br>");

        return s.toString();
    }

    /*
     * We operate on `Snapshot`s, simple objects that don't need to be loaded in/out of memory every single time.
     * These statistics should NOT be computed with `SnapshotData` which weigh a lot of memory.
     */
    @NotNull
    private LongStream snapshotLongStream(ToLongFunction<Snapshot> map) {
        return snapshotCollection.snapshotList.stream().mapToLong(map);
    }
}
