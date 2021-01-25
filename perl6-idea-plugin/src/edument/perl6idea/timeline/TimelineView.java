package edument.perl6idea.timeline;

import com.intellij.ui.ContextHelpLabel;
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
    private ContextHelpLabel help;

    public TimelineView() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.timeline = new Timeline();
        this.chart = new TimelineChart(timeline);
        this.add(chart);
        this.help = ContextHelpLabel.create(
            "<b>Zoom</b><br>Ctrl <em>+</em> Mouse Wheel / + / -<br>" +
            "<b>Move in time</b><br>Click and drag, left/right arrow keys<br>" +
            "<b>Scroll lanes</b><br>Mouse wheel, up/down arrow keys");
        this.add(this.help);
        this.scrollbar = new JBScrollBar(Adjustable.VERTICAL, 0, 3, 0, 3);
        this.add(this.scrollbar);
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

    public void endLiveUpdates() {
        chart.endLiveUpdates();
    }
}
