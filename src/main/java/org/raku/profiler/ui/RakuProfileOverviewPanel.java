package org.raku.profiler.ui;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.raku.profiler.model.RakuProfileData;
import org.raku.profiler.model.RakuProfileOverviewData;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RakuProfileOverviewPanel {
    private final RakuProfileOverviewData myData;

    public RakuProfileOverviewPanel(RakuProfileData data) {
        myData = data.getOverviewData();
    }

    public Component getPanel() {
        JPanel panel = new JPanel(new MigLayout());
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        JScrollPane scrollPane = new JBScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        String style = """
            <head><style>body {
            \tfont-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji"; }
            p { margin: -6px; }
            </style></head>""";
        textPane.setText("<html>" + style + generateStartText()
                         + generateGCText() + generateCallText() + generateOptText() + "</html>");
        textPane.setContentType("text/html");
        textPane.setBorder(JBUI.Borders.empty());
        textPane.setForeground(JBColor.foreground());
        panel.add(scrollPane, "pushx, pushy, growx, growy");
        return panel;
    }

    private String generateOptText() {
        String pattern = "<h3>Dynamic Optimization</h3>" +
                         "<p>Of <b>%,8d</b> specialized or JIT-compiled frames, there were <b>%,8d</b> deoptimizations (that's <b>%.2f%%</b> of all optimized frames).</p>" +
                         "<p>There were <b>%,8d object allocations</b>. %s.</p>" +
                         "<p>There were <b>%s</b> global deoptimizations triggered by the profiled code.</p>" +
                         "<p>During profilation, code in hot loops was <b>on-stack-replaced (OSR'd) %,8d times.</b></p>";
        return String.format(pattern,
                             myData.spesh + myData.jit,
                             myData.deopted, (100f * myData.deopted / (myData.spesh + myData.jit)),
                             myData.allocated,
                             myData.replaced != 0
                             ? String.format(
                                 "The dynamic optimizer was able to eliminate the need to allocate <b>%,8d additional objects</b> (that's <b>%.2f%%</b>)",
                                 myData.replaced, 100f * myData.replaced / (myData.replaced + myData.allocated))
                             : "not able to eliminate any allocations through scalar replacement",
                             myData.globalDeopt, myData.OSR
        );
    }

    private String generateCallText() {
        String pattern = "<h3>Call Frames</h3>" +
                         "<p>In total, <b>%,8d call frames</b> were entered and exited by the profiled code.</p>" +
                         "<p>Inlining eliminated the need to allocate <b>%,8d call frames</b> (that's <b>%.2f%%</b>).</p>";
        return String.format(pattern,
                             myData.entriesTotal, myData.entriesTotal - myData.inlineEntriesTotal,
                             (myData.entriesTotal - myData.inlineEntriesTotal) / (float)myData.entriesTotal * 100f
        );
    }

    private String generateGCText() {
        String pattern = "<h3>GC</h3>" +
                         "<p>GC runs have taken <b>%.2f%%</b> of total run time.</p>" +
                         "<p>The Garbage Collector ran <b>%s times</b>. %s</p>" +
                         "%s%s%s%s%s";
        return String.format(pattern,
                             100f * (myData.gcTotalTime / myData.totalTime),
                             myData.gcNumber, myData.fullGCNumber == 0
                                              ? "There was never a need to go through the entire heap."
                                              : myData.fullGCNumber + " GC runs inspected the entire heap.",
                             myData.gcNumber != 0
                             ? String.format("<p>Minor GC runs took between <b>%.2fms</b> and <b>%.2fms</b> with an average of <b>%.2fms</b>.</p>", myData.minMinorTime / 1000f, myData.maxMinorTime / 1000f, myData.avgMinorTime / 1000f)
                             : "",
                             myData.fullGCNumber > 1
                             ? String.format("<p>Major GC runs took between <b>%.2fms</b> and <b>%.2fms</b> with an average of <b>%.2fms</b>.</p>", myData.minMajorTime / 1000f, myData.maxMajorTime / 1000f, myData.avgMajorTime / 1000f)
                             : "",
                             myData.fullGCNumber == 1
                             ? String.format("<p>The Major GC run took <b>%.2fms</b>.</p>", myData.maxMajorTime / 1000f)
                             : "",
                             myData.gcNumber != 0
                             ? String.format("<p>Total time spent in GC was <b>%.2fms</b>.</p>", myData.gcTotalTime)
                             : "",
                             myData.fullGCNumber > 0
                             ? String.format("<p>Of that, minor collections accounted for <b>%,8d</b> and major collections accounted for <b>%d</b>.</p>", myData.totalMinor, myData.totalMajor)
                             : ""
                             );
    }

    private String generateStartText() {
        String pattern = "<h3>Overview</h3" +
                         "<p>The profiled code ran for <b>%,8d</b>ms. At the end of the program, <b>%d threads</b> were active.</p>" +
                         "<p>The dynamic optimizer has been active for <b>%.2f</b>ms.</p>";
        return String.format(pattern,
                             (int)myData.totalTime, myData.threads, myData.speshTime);
    }
}
