package edument.perl6idea.timeline;

import com.intellij.ui.components.JBScrollBar;
import edument.perl6idea.timeline.client.ClientEvent;
import edument.perl6idea.timeline.model.Timeline;

import javax.swing.*;
import java.awt.*;

/** The timeline chart with scroll bar and so forth. */
public class TimelineView extends JPanel {
    private Timeline timeline;
    private TimelineChart chart;
    private JBScrollBar scrollbar;

    public TimelineView() {
        super(new BorderLayout());
        this.timeline = new Timeline();
        this.chart = new TimelineChart(timeline);
        this.add(chart, BorderLayout.CENTER);
        this.scrollbar = new JBScrollBar(Adjustable.VERTICAL, 0, 3, 0, 3);
        this.add(this.scrollbar, BorderLayout.EAST);
        setupScrollHandling();
    }

    private void setupScrollHandling() {
        chart.setVisibleLanesChangedHandler(updated -> {
            if (scrollbar.getMaximum() != updated.getTotalLanes() -1 ||
                    scrollbar.getModel().getExtent() != updated.getLanesInView())
                scrollbar.setValues(updated.getFirstLaneInView(), updated.getLanesInView(),
                        0, updated.getTotalLanes() - 1);
        });
        scrollbar.addAdjustmentListener(e -> chart.setFirstLaneInView(e.getValue()));
    }

    public void updateWith(ClientEvent event) {
        timeline.incorporate(event);
        chart.updateFromTimeline();
    }
}
