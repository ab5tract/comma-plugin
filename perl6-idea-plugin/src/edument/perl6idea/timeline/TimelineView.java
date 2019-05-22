package edument.perl6idea.timeline;

import edument.perl6idea.timeline.client.ClientEvent;
import edument.perl6idea.timeline.model.Timeline;

import javax.swing.*;
import java.awt.*;

/** The timeline chart with scroll bars and so forth. */
public class TimelineView extends JPanel {
    private Timeline timeline;
    private TimelineChart chart;

    public TimelineView() {
        super(new BorderLayout());
        this.timeline = new Timeline();
        this.chart = new TimelineChart(timeline);
        this.add(chart, BorderLayout.CENTER);
    }

    public void updateWith(ClientEvent event) {
        timeline.incorporate(event);
        chart.repaint();
    }
}
