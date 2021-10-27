package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.util.text.StringUtilRt;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;
import edument.perl6idea.heapsnapshot.Snapshot;
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
    private double[] indices;

    public HeapSnapshotSummaryTab(HeapSnapshotCollection snapshotCollection) {
        super(new MigLayout("wrap 2"));
        this.snapshotCollection = snapshotCollection;
        this.indices = IntStream.range(0, snapshotCollection.snapshotList.size()).mapToDouble(i -> i).toArray();

        add(new JLabel(summarize()), "span 2");
        add(new XChartPanel<>(getHeapsizeChart()));
        add(new XChartPanel<>(getObjectsChart()));
        add(new XChartPanel<>(getFramesAndTypeObjectsChart()));
    }

    private XYChart getObjectsChart() {
        XYChart chart = makeAreaChart("Objects");
        double[] objects = snapshotLongStream(h -> h.totalObjects).mapToDouble(i -> i).toArray();
        chart.addSeries("Total Objects", indices, objects);
        return chart;
    }

    private XYChart getFramesAndTypeObjectsChart() {
        XYChart chart = makeAreaChart("Frames & Type Objects");
        double[] frames = snapshotLongStream(h -> h.totalFrames).mapToDouble(i -> i).toArray();
        double[] types = snapshotLongStream(h -> h.totalTypeobjects).mapToDouble(i -> i).toArray();
        chart.addSeries("Total Frames", indices, frames);
        chart.addSeries("Total Type Objects", indices, types);
        return chart;
    }

    private XYChart getHeapsizeChart() {
        XYChart chart = makeAreaChart("Heapsize");
        double[] heapsizes = snapshotLongStream(h -> h.totalHeapSize).mapToDouble(i -> i).toArray();
        chart.addSeries("Total Heap Size", indices, heapsizes);
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
            return "There are no snapshots.";
        }

        StringBuilder s = new StringBuilder();
        if (size == 1) {
            s.append("There is a single snapshot. ");
        } else {
            s.append("There are ").append(size).append(" snapshots.\n");
        }

        int typeCount = snapshotCollection.typeData.typenamePieces.size();
        s.append("There are ").append(typeCount).append(" type objects.\n");

        int staticFrameCount = snapshotCollection.staticFrameData.namePieces.size();
        s.append("There are ").append(staticFrameCount).append(" static frames.\n");

        long highestHeapSize = snapshotLongStream(h -> h.totalHeapSize).max().getAsLong();
        s.append("The heap size was at most ").append(highestHeapSize).append(" bytes ");
        long lowestHeapSize = snapshotLongStream(h -> h.totalHeapSize).max().getAsLong();
        s.append("and at least ").append(StringUtilRt.formatFileSize(lowestHeapSize)).append(" bytes ");
        long avgHeapSize = (long)snapshotLongStream(h -> h.totalHeapSize).average().getAsDouble();
        s.append("for an average of ").append(StringUtilRt.formatFileSize(avgHeapSize)).append(" bytes.\n");

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
