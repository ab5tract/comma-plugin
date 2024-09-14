package org.raku.timeline;

import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.JBScrollBar;
import org.raku.timeline.client.ClientEvent;
import org.raku.timeline.model.Timeline;

import javax.swing.*;
import java.awt.*;

/** The timeline chart with scroll bar and so forth. */
public class TimelineView extends JPanel {
    private final Timeline timeline;
    private final TimelineChart chart;
    private final JBScrollBar scrollbar;

    public TimelineView() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.timeline = new Timeline();
        this.chart = new TimelineChart(timeline);
        this.add(chart);
        ContextHelpLabel help = ContextHelpLabel.create(
            "<b>Zoom</b><br>Ctrl <em>+</em> Mouse Wheel / + / -<br>" +
            "<b>Move in time</b><br>Click and drag, left/right arrow keys<br>" +
            "<b>Scroll lanes</b><br>Mouse wheel, up/down arrow keys");
        this.add(help);
        this.scrollbar = new JBScrollBar(Adjustable.VERTICAL, 0, 3, 0, 3);
        this.add(this.scrollbar);
        setupScrollHandling();
    }

    private void setupScrollHandling() {
        chart.setVisibleLanesChangedHandler(updated -> {
            if (scrollbar.getMaximum() != updated.totalLanes() - 1 ||
                    scrollbar.getModel().getExtent() != updated.lanesInView())
                scrollbar.setValues(updated.firstLaneInView(), updated.lanesInView(),
                                    0, updated.totalLanes() - 1);
        });
        scrollbar.addAdjustmentListener(e -> chart.setFirstLaneInView(e.getValue()));
    }

    public void updateWith(ClientEvent event) {
        timeline.incorporate(event);
        chart.updateFromTimeline();
    }

    public void endLiveUpdates() {
        chart.endLiveUpdates();
    }
}
