package edument.perl6idea.timeline;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.timeline.model.Logged;
import edument.perl6idea.timeline.model.Timeline;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Renders the timeline chart, with axes and currently visible data. */
public class TimelineChart extends JPanel {
    // The timeline we're rendering.
    private Timeline timeline;

    // Fonts and font metrics.
    private Font font = JBUI.Fonts.label();
    private Font categoryNameFont = font.deriveFont(Font.BOLD);
    private Font moduleNameFont = categoryNameFont.deriveFont((float)categoryNameFont.getSize() + 4);
    private FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    private final int moduleNameHeight = (int)new TextLayout("Sample", moduleNameFont, fontRenderContext)
            .getBounds().getHeight();
    private final int textHeight = (int)new TextLayout("Sample", font, fontRenderContext)
            .getBounds().getHeight();

    // Padding sizes.
    private static final int chartPadding = 10;
    private static final int labelPadding = 5;

    // How spaced out the ticks on the time scale are, and what is the dash size?
    private static final int tickSpacing = 100;
    private static final int tickDashSize = 10;

    // The current tick interval in seconds.
    private double tickInterval = 1.0;

    // The current starting position.
    private double startTime = 0.0;

    public TimelineChart(Timeline timeline) {
        this.timeline = timeline;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Set us up to paint.
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // If there's no data, then just render text that we're waiting for it.
        // Otherwise, render the chart.
        if (timeline.isEmpty())
            paintWaitingLabel(g2d);
        else
            paintChart(g2d);
    }

    private void paintWaitingLabel(Graphics2D g) {
        String message = "Waiting for timeline data...";
        TextLayout layout = new TextLayout(message, font, fontRenderContext);
        int x = (int)(getWidth() / 2 - layout.getBounds().getWidth());
        int y = (int)(getHeight() / 2 - layout.getBounds().getHeight());
        g.drawString(message, x, y);
    }

    // Information about a single line on the chart to render.
    private static class RenderLine {
        private List<Logged> loggedItems;
        private int top;

        public RenderLine(List<Logged> loggedItems, int top) {
            this.loggedItems = loggedItems;
            this.top = top;
        }
    }

    private void paintChart(Graphics2D g) {
        // Paint labels on the left, and build up lists of timeline elements to render
        // at what position.
        List<RenderLine> linesToRender = new ArrayList<>();
        int curY = chartPadding;
        int maxLabelWidth = 150;
        Map<String, Map<String, Map<String, List<Logged>>>> modules = timeline.getData();
        for (String module : modules.keySet()) {
            // Paint module name.
            Dimension modDims = paintName(g, curY, moduleNameFont, moduleNameHeight, module);
            maxLabelWidth = Math.max(maxLabelWidth, modDims.width);
            curY += modDims.height;

            // Visit categories.
            Map<String, Map<String, List<Logged>>> categories = modules.get(module);
            for (String category : categories.keySet()) {
                // Paint category name.
                Dimension catDims = paintName(g, curY, categoryNameFont, textHeight, category);
                maxLabelWidth = Math.max(maxLabelWidth, catDims.width);
                curY += catDims.height;

                // Visit the names.
                Map<String, List<Logged>> names = categories.get(category);
                for (String name : names.keySet()) {
                    // Add set of things to render at this point.
                    linesToRender.add(new RenderLine(names.get(name), curY));

                    // Paint name.
                    Dimension nameDims = paintName(g, curY, font, textHeight, name);
                    maxLabelWidth = Math.max(maxLabelWidth, nameDims.width);
                    curY += nameDims.height;
                }
            }
        }

        // Calculate the time range we can fit onto the graph.
        int spaceAvailable = getWidth() - (2 * chartPadding + maxLabelWidth);
        int ticksPossible = Math.max(spaceAvailable / tickSpacing, 2);

        // Paint the axes.
        paintLeftAxis(g, curY, chartPadding + maxLabelWidth);
        paintTimeAxis(g, chartPadding + maxLabelWidth, curY, ticksPossible);
    }

    private Dimension paintName(Graphics2D g, int y, Font font, int height, String module) {
        TextLayout layout = new TextLayout(module, font, fontRenderContext);
        g.setFont(font);
        g.drawString(module, chartPadding, y + labelPadding + height);
        return new Dimension((int)layout.getBounds().getWidth() + labelPadding,
                             height + 2 * labelPadding);
    }

    private void paintLeftAxis(Graphics2D g, int bottomY, int x) {
        g.setColor(JBColor.BLACK);
        g.drawLine(x, chartPadding, x, bottomY);
    }

    private void paintTimeAxis(Graphics2D g, int leftX, int topY, int ticksPossible) {
        // Draw the axis line itself.
        g.setColor(JBColor.BLACK);
        g.drawLine(leftX, topY, leftX + ticksPossible * tickSpacing, topY);
        g.setFont(font);

        // Paint the numbers on the line.
        int curTickX = leftX;
        for (int i = 0; i <= ticksPossible; i++) {
            // Draw the tick mark.
            g.drawLine(curTickX, topY, curTickX, topY + tickDashSize);

            // Paint the number.
            String number = String.format("%.3G", startTime + i * tickInterval);
            TextLayout layout = new TextLayout(number, font, fontRenderContext);
            int numberWidth = (int)layout.getBounds().getWidth();
            g.drawString(number, curTickX - (numberWidth / 2), topY + tickDashSize + labelPadding + textHeight);

            curTickX += tickSpacing;
        }
    }
}
