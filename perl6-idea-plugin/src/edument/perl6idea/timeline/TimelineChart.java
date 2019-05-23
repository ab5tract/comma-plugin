package edument.perl6idea.timeline;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.timeline.model.*;
import edument.perl6idea.timeline.model.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    // Colors we've assigned, plus a color generator.
    private ColorGenerator colorGen = new ColorGenerator();
    private Map<String, Color> assignedColors = new HashMap<>();

    // The current area of the graph.
    private Rectangle graphArea = new Rectangle();

    // Used to get ticks to update the graph end point.
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public TimelineChart(Timeline timeline) {
        this.timeline = timeline;
        addMouseEventHandlers();
        executor.scheduleAtFixedRate(
                () -> ApplicationManager.getApplication().invokeLater(() -> {
                    timeline.tick();
                    repaint();
                }), 100, 100, TimeUnit.MILLISECONDS);
    }

    private void addMouseEventHandlers() {
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Only zoom when Ctrl is held down and in graph area.
                if (!e.isControlDown() && graphArea.contains(e.getPoint()))
                    return;

                // Calculate new tick interval.
                double factor = e.getWheelRotation() < 0 ? 0.5 : 2.0;
                int notches = Math.abs(e.getWheelRotation());
                for (int i = 0; i < notches; i++)
                    tickInterval *= factor;

                // Repaint the graph at this size.
                repaint();
            }
        });
        MouseAdapter dragHandler = new MouseAdapter() {
            private boolean moving = false;
            private Point lastPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && graphArea.contains(e.getPoint())) {
                    moving = true;
                    lastPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                moving = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (moving) {
                    Point newPoint = e.getPoint();
                    if (graphArea.contains(newPoint)) {
                        // See how far we moved since the last event, and update the
                        // latest point to the current one for next time around.
                        int xDiff = (int)lastPoint.getX() - (int)newPoint.getX();
                        lastPoint = newPoint;

                        // Work out how many ticks we moved and update the start time
                        // accordingly; hang on to the amount we didn't yet use.
                        int wholeTicks = xDiff / tickSpacing;
                        int leftoverPixels = xDiff - wholeTicks * tickSpacing;
                        lastPoint.translate(leftoverPixels, 0);
                        startTime += wholeTicks * tickInterval;
                        if (startTime < 0.0)
                            startTime = 0.0;

                        // Repaint in new position.
                        repaint();
                    }
                    else {
                        moving = false;
                    }
                }
            }
        };
        addMouseListener(dragHandler);
        addMouseMotionListener(dragHandler);
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
    private class RenderLine {
        private List<Logged> loggedItems;
        private int top;

        public RenderLine(List<Logged> loggedItems, int top) {
            this.loggedItems = loggedItems;
            this.top = top;
        }

        public void render(Graphics2D g, int startingX, int numTicks) {
            // Go through the items, find those in view, and render them.
            double endTime = Math.min(startTime + tickInterval * numTicks, timeline.getEndTime());
            for (Logged item : loggedItems) {
                g.setColor(colorForItem(item));
                if (item instanceof Event) {
                    Event event = (Event)item;
                    if (event.getWhen() >= startTime && event.getWhen() < endTime)
                        renderEvent(g, startingX, event.getWhen());
                }
                else if (item instanceof Task) {
                    Task task = (Task)item;
                    if (task.getStart() < endTime && task.getEnd() >= startTime) {
                        double boundedStart = Math.max(startTime, task.getStart());
                        double boundedEnd = Math.min(endTime, task.getEnd());
                        renderTask(g, startingX, boundedStart, boundedEnd);
                    }
                }
            }
        }

        private void renderEvent(Graphics2D g, int x, double when) {
            int midPixel = (int)((when - startTime) / tickInterval * tickSpacing);
            // TODO Render
        }

        private void renderTask(Graphics2D g, int startingX, double start, double end) {
            int startPixel = (int)((start - startTime) / tickInterval * tickSpacing);
            int endPixel = (int)((end - startTime) / tickInterval * tickSpacing);
            int width = Math.max(endPixel - startPixel, 1);
            g.fillRect(startingX + startPixel, top + labelPadding, width, textHeight);
        }
    }

    private Color colorForItem(Logged item) {
        String key = item.getModule() + "\0" + item.getCategory() + "\0" + item.getName();
        return assignedColors.computeIfAbsent(key, k -> colorGen.nextColor());
    }

    private void paintChart(Graphics2D g) {
        // Paint labels on the left, and build up lists of timeline elements to render
        // at what position.
        List<RenderLine> linesToRender = new ArrayList<>();
        int curY = chartPadding;
        int maxLabelWidth = 150;
        Map<String, Map<String, Map<String, LaneGroup>>> modules = timeline.getData();
        for (String module : modules.keySet()) {
            // Paint module name.
            Dimension modDims = paintName(g, curY, moduleNameFont, moduleNameHeight, module);
            maxLabelWidth = Math.max(maxLabelWidth, modDims.width);
            curY += modDims.height;

            // Visit categories.
            Map<String, Map<String, LaneGroup>> categories = modules.get(module);
            for (String category : categories.keySet()) {
                // Paint category name.
                Dimension catDims = paintName(g, curY, categoryNameFont, textHeight, category);
                maxLabelWidth = Math.max(maxLabelWidth, catDims.width);
                curY += catDims.height;

                // Visit the names.
                Map<String, LaneGroup> names = categories.get(category);
                for (String name : names.keySet()) {
                    // Paint name.
                    Dimension nameDims = paintName(g, curY, font, textHeight, name);
                    maxLabelWidth = Math.max(maxLabelWidth, nameDims.width);

                    // Add the lanes to render.
                    for (Lane lane : names.get(name).getLanes()) {
                        linesToRender.add(new RenderLine(lane.getEntries(), curY));
                        curY += nameDims.height;
                    }
                }
            }
        }

        // Calculate the time range we can fit onto the graph.
        int spaceAvailable = getWidth() - (2 * chartPadding + maxLabelWidth);
        int ticksPossible = Math.max(spaceAvailable / tickSpacing, 2);

        // Paint the axes.
        paintLeftAxis(g, curY, chartPadding + maxLabelWidth);
        paintTimeAxis(g, chartPadding + maxLabelWidth, curY, ticksPossible);

        // Paint elements in view.
        for (RenderLine line : linesToRender)
            line.render(g, chartPadding + maxLabelWidth + 1, ticksPossible);

        // Stash the rectangle of the painted area, for handling mouse events.
        graphArea = new Rectangle(chartPadding + maxLabelWidth, chartPadding,
                                  ticksPossible * tickSpacing, curY - chartPadding);
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
