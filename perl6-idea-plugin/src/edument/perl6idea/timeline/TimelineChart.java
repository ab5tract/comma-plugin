package edument.perl6idea.timeline;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Consumer;
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

    // Indentation space for children.
    private static final int childIndent = 15;

    // Expander icon size.
    private final int expanderSize = textHeight - labelPadding;

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

    // Controls rendering an icon for opening/closing a section
    private enum Expander { None, Opener, Closer }

    // Information about the number of lanes and those in view, used for vertical
    // scrolling support, along with an event handler.
    private int yAxisEnd;
    private boolean reachedEnd;
    private int totalLanes = 0;
    private int lanesInView = 0;
    private int firstLane = 0;
    private Consumer<VisibleLanesChanged> visibleLanesChangedHandler;

    // Information about a rendered area on the chart, for handling tooltips and expansions.
    private static class VisibleLogged {
        private final Rectangle area;
        private final Logged logged;

        public VisibleLogged(Rectangle area, Logged logged) {
            this.area = area;
            this.logged = logged;
        }

        public boolean contains(Point p) {
            return area.contains(p);
        }

        public Logged getLogged() {
            return logged;
        }
    }
    private List<VisibleLogged> visibleLoggeds = new ArrayList<>();

    // Information about a label, so we can detect clicks on those.
    private static class VisibleLabel {
        private final Rectangle area;
        private final String key;

        public VisibleLabel(Rectangle area, String key) {
            this.area = area;
            this.key = key;
        }

        public boolean contains(Point point) {
            return area.contains(point);
        }

        public String getKey() {
            return key;
        }
    }
    private List<VisibleLabel> visibleLabels = new ArrayList<>();

    // Expansion status of each item, keyed on the path to the item.
    private Map<String, Boolean> expanded = new HashMap<>();

    public TimelineChart(Timeline timeline) {
        this.timeline = timeline;
        addMouseEventHandlers();
        addResizeHandler();
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
                // Only care if we're in the graph area.
                if (!graphArea.contains(e.getPoint()))
                    return;

                // If Ctrl is held down, then we're zooming.
                if (e.isControlDown()) {
                    // Calculate new tick interval.
                    double factor = e.getWheelRotation() < 0 ? 0.5 : 2.0;
                    int notches = Math.abs(e.getWheelRotation());
                    for (int i = 0; i < notches; i++)
                        tickInterval *= factor;
                }

                // Otherwise, we're scrolling up/down. */
                else {
                    firstLane += e.getWheelRotation();
                    if (firstLane < 0)
                        firstLane = 0;
                    else if (firstLane > totalLanes - lanesInView)
                        firstLane = totalLanes - lanesInView;
                    fireVisibleLanesChangedHandler();
                    repaint();
                }

                // Repaint the graph at this size.
                repaint();
            }
        });

        MouseAdapter dragAndMoveHandler = new MouseAdapter() {
            private boolean moving = false;
            private Point lastPoint;
            private Logged currentTooltipLogged;
            private JBPopup currentPopup;

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
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                if (e.getButton() == MouseEvent.BUTTON1 && !graphArea.contains(point)) {
                    for (VisibleLabel label : visibleLabels) {
                        if (label.contains(point)) {
                            String key = label.getKey();
                            Boolean current = expanded.get(key);
                            expanded.put(key, current == null ? true : !current);
                            repaint();
                            fireVisibleLanesChangedHandler();
                            break;
                        }
                    }
                }
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

            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getButton() != 0)
                    return;
                maybeShowTooltip(e.getPoint());
            }

            private void maybeShowTooltip(Point point) {
                if (graphArea.contains(point)) {
                    for (VisibleLogged visible : visibleLoggeds) {
                        if (visible.contains(point)) {
                            if (visible.getLogged() != currentTooltipLogged) {
                                closeActiveTooltip();
                                showTooltip(point, visible);
                            }
                            return;
                        }
                    }
                }
                closeActiveTooltip(); // Because we're not in any
            }

            private void showTooltip(Point point, VisibleLogged visible) {
                currentTooltipLogged = visible.getLogged();
                JBPopup popup = JBPopupFactory.getInstance()
                          .createComponentPopupBuilder(new TimelineTooltip(currentTooltipLogged), null)
                          .setFocusOwners(new Component[] { TimelineChart.this })
                          .createPopup();
                currentPopup = popup;
                popup.show(new RelativePoint(TimelineChart.this, point));
            }

            private void closeActiveTooltip() {
                if (currentTooltipLogged != null) {
                    currentPopup.cancel();
                    Disposer.dispose(currentPopup);
                    currentPopup = null;
                    currentTooltipLogged = null;
                }
            }
        };
        addMouseListener(dragAndMoveHandler);
        addMouseMotionListener(dragAndMoveHandler);
    }

    private void addResizeHandler() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fireVisibleLanesChangedHandler();
            }
        });
    }

    public void updateFromTimeline() {
        repaint();
        fireVisibleLanesChangedHandler();
    }

    public void setVisibleLanesChangedHandler(Consumer<VisibleLanesChanged> visibleLanesChangedHandler) {
        this.visibleLanesChangedHandler = visibleLanesChangedHandler;
    }

    public void setFirstLaneInView(int firstLaneInView) {
        firstLane = firstLaneInView;
    }

    private void fireVisibleLanesChangedHandler() {
        if (visibleLanesChangedHandler != null)
            visibleLanesChangedHandler.consume(new VisibleLanesChanged(
                    totalLanes, firstLane, lanesInView));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Set us up to paint.
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        visibleLoggeds.clear();
        visibleLabels.clear();

        // Calculate the end of the y axis and clear "reached end" flag,
        // as well as the current lane view state.
        yAxisEnd = this.getHeight() - (
                chartPadding +                              // Padding at bottom
                tickDashSize + labelPadding + textHeight +  // Axis
                10);                                        // Fudge :-)
        reachedEnd = false;
        totalLanes = 0;
        lanesInView = 0;

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
            boolean darken = true;
            for (Logged item : loggedItems) {
                if (item instanceof Event) {
                    Event event = (Event)item;
                    if (event.getWhen() >= startTime && event.getWhen() < endTime) {
                        setColor(g, darken, item);
                        renderEvent(g, startingX, event.getWhen(), event);
                    }
                }
                else if (item instanceof Task) {
                    Task task = (Task)item;
                    if (task.getStart() < endTime && task.getEnd() >= startTime) {
                        double boundedStart = Math.max(startTime, task.getStart());
                        double boundedEnd = Math.min(endTime, task.getEnd());
                        setColor(g, darken, item);
                        renderTask(g, startingX, boundedStart, boundedEnd, task);
                    }
                }
                darken = !darken;
            }
        }

        private void setColor(Graphics2D g, boolean darken, Logged item) {
            Color baseColor = colorForItem(item);
            g.setColor(darken ? baseColor.darker() : baseColor);
        }

        private void renderEvent(Graphics2D g, int startingX, double when, Event event) {
            int left = startingX + (int)((when - startTime) / tickInterval * tickSpacing);
            int width = 2 * (textHeight / 3);
            Polygon triangle = new Polygon();
            triangle.addPoint(left, top + labelPadding);
            triangle.addPoint(left, top + labelPadding + textHeight);
            triangle.addPoint(left + width, top + labelPadding + textHeight / 2);
            g.fillPolygon(triangle);
            Rectangle rect = new Rectangle(left, top + labelPadding, width, textHeight);
            visibleLoggeds.add(new VisibleLogged(rect, event));
        }

        private void renderTask(Graphics2D g, int startingX, double start, double end, Task task) {
            int startPixel = (int)((start - startTime) / tickInterval * tickSpacing);
            int endPixel = (int)((end - startTime) / tickInterval * tickSpacing);
            int width = Math.max(endPixel - startPixel, 1);
            Rectangle rect = new Rectangle(startingX + startPixel, top + labelPadding, width, textHeight);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
            visibleLoggeds.add(new VisibleLogged(rect, task));
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
            boolean moduleExpanded = expanded(module, true);
            if (isInView(curY, moduleNameHeight)) {
                Dimension modDims = paintName(g, 0, curY, moduleNameFont, moduleNameHeight,
                                              module, moduleExpanded ? Expander.Closer : Expander.Opener);
                maxLabelWidth = Math.max(maxLabelWidth, modDims.width);
                visibleLabels.add(new VisibleLabel(
                        new Rectangle(chartPadding, curY, modDims.width, modDims.height),
                        module));
                curY += modDims.height;
            }

            // Skip the categories if the module is not expanded.
            if (!moduleExpanded)
                continue;

            // Visit categories.
            Map<String, Map<String, LaneGroup>> categories = modules.get(module);
            for (String category : categories.keySet()) {
                // Paint category name.
                String categoryKey = module + "\0" + category;
                boolean categoryExpanded = expanded(categoryKey, true);
                if (isInView(curY, textHeight)) {
                    Dimension catDims = paintName(g, 0, curY, categoryNameFont, textHeight,
                                                  category, categoryExpanded ? Expander.Closer : Expander.Opener);
                    maxLabelWidth = Math.max(maxLabelWidth, catDims.width);
                    visibleLabels.add(new VisibleLabel(
                            new Rectangle(chartPadding, curY, catDims.width, catDims.height),
                            categoryKey));
                    curY += catDims.height;
                }

                // Skip the names if the category is not expanded.
                if (!categoryExpanded)
                    continue;

                // Visit the names.
                Map<String, LaneGroup> names = categories.get(category);
                for (String name : names.keySet()) {
                    // Paint name.
                    String nameKey = categoryKey + "\0" + name;
                    List<Lane> lanes = names.get(name).getLanes();
                    boolean expandable = doesAnyLaneHaveChildren(lanes);
                    boolean isExpanded = expandable && expanded(nameKey, false);
                    boolean firstLaneInView = isInView(curY, textHeight);
                    if (firstLaneInView) {
                        Dimension nameDims = paintName(g, 0, curY, font, textHeight, name,
                                                       expandable ? (isExpanded ? Expander.Closer : Expander.Opener) : Expander.None);
                        maxLabelWidth = Math.max(maxLabelWidth, nameDims.width);
                        visibleLabels.add(new VisibleLabel(
                                new Rectangle(chartPadding, curY, nameDims.width, nameDims.height),
                                nameKey));
                    }

                    // Add the lanes to render.
                    boolean curLaneInView = firstLaneInView;
                    for (Lane lane : lanes) {
                        if (curLaneInView) {
                            linesToRender.add(new RenderLine(lane.getEntries(), curY));
                            curY += 2 * labelPadding + textHeight;
                        }
                        if (isExpanded) {
                            Dimension childNameDims = renderChildLanes(g, lane.getChildTaskLaneGroups(),
                                    linesToRender, curY, 1, nameKey);
                            maxLabelWidth = Math.max(maxLabelWidth, childNameDims.width);
                            curY += childNameDims.height;
                        }
                        curLaneInView = isInView(curY, textHeight);
                    }
                }
            }
        }

        // Calculate the time range we can fit onto the graph.
        int spaceAvailable = getWidth() - (2 * chartPadding + maxLabelWidth);
        int ticksPossible = Math.max(spaceAvailable / tickSpacing, 2);

        // Paint the axes.
        paintLeftAxis(g, yAxisEnd, chartPadding + maxLabelWidth);
        paintTimeAxis(g, chartPadding + maxLabelWidth, yAxisEnd, ticksPossible);

        // Paint elements in view.
        for (RenderLine line : linesToRender)
            line.render(g, chartPadding + maxLabelWidth + 1, ticksPossible);

        // Stash the rectangle of the painted area, for handling mouse events.
        graphArea = new Rectangle(chartPadding + maxLabelWidth, chartPadding,
                                  ticksPossible * tickSpacing, yAxisEnd);
    }

    private Dimension renderChildLanes(Graphics2D g, Map<String, LaneGroup> namedLaneGroups,
                                       List<RenderLine> linesToRender, int curY,
                                       int indent, String parentKey) {
        int addedHeight = 0;
        int maxLabelWidth = 0;
        for (String name : namedLaneGroups.keySet()) {
            // Render the name of the child lane.
            String childKey = parentKey + "\0" + name;
            List<Lane> lanes = namedLaneGroups.get(name).getLanes();
            boolean expandable = doesAnyLaneHaveChildren(lanes);
            boolean isExpanded = expandable && expanded(childKey, false);
            boolean firstLaneInView = isInView(curY, textHeight);
            if (firstLaneInView) {
                Dimension nameDims = paintName(g, indent * childIndent, curY, font, textHeight, name,
                                               expandable ? (isExpanded ? Expander.Closer : Expander.Opener) : Expander.None);
                maxLabelWidth = Math.max(maxLabelWidth, nameDims.width);
                visibleLabels.add(new VisibleLabel(
                        new Rectangle(chartPadding + indent * childIndent, curY, nameDims.width, nameDims.height),
                        childKey));
            }

            // Add the lanes to render.
            boolean curLaneInView = firstLaneInView;
            for (Lane lane : lanes) {
                if (curLaneInView) {
                    linesToRender.add(new RenderLine(lane.getEntries(), curY));
                    curY += 2 * labelPadding + textHeight;
                    addedHeight += 2 * labelPadding + textHeight;
                }
                if (isExpanded) {
                    Dimension childNameDims = renderChildLanes(g, lane.getChildTaskLaneGroups(),
                            linesToRender, curY, indent + 1, childKey);
                    maxLabelWidth = Math.max(maxLabelWidth, childNameDims.width);
                    curY += childNameDims.height;
                    addedHeight += childNameDims.height;
                }
                curLaneInView = isInView(curY, textHeight);
            }
        }
        return new Dimension(maxLabelWidth, addedHeight);
    }

    private boolean isInView(int curY, int labelTextHeight) {
        // Increment total number of lanes.
        int laneNum = totalLanes++;

        // Should not have already run out of space.
        if (reachedEnd)
            return false;

        // Should be beyond the first lane to show.
        if (laneNum < firstLane)
            return false;

        // Is there space for it?
        int predictedHeight = 2 * labelPadding + labelTextHeight;
        if (curY + predictedHeight > yAxisEnd) {
            // Out of space.
            reachedEnd = false;
            return false;
        }

        // Otherwise, it'll fit.
        lanesInView++;
        return true;
    }

    private boolean doesAnyLaneHaveChildren(List<Lane> lanes) {
        for (Lane lane : lanes)
            if (!lane.getChildTaskLaneGroups().isEmpty())
                return true;
        return false;
    }

    private Dimension paintName(Graphics2D g, int x, int y, Font font, int height,
                                String module, Expander expander) {
        TextLayout layout = new TextLayout(module, font, fontRenderContext);
        g.setFont(font);
        g.setColor(JBColor.BLACK);
        g.drawString(module, chartPadding + x, y + labelPadding + height);
        Dimension area = new Dimension(x + (int)layout.getBounds().getWidth() + labelPadding,
                height + 2 * labelPadding);
        if (expander != Expander.None) {
            int centering = height - expanderSize;
            paintExpander(g, chartPadding + area.width,
                         y + labelPadding + centering, expander);
            area.width += 2 * labelPadding + expanderSize;
        }
        return area;
    }

    private void paintExpander(Graphics2D g, int x, int y, Expander expander) {
        Polygon triangle = new Polygon();
        if (expander == Expander.Opener) {
            triangle.addPoint(x, y);
            triangle.addPoint(x + expanderSize, y);
            triangle.addPoint(x + expanderSize / 2, y + expanderSize);
        }
        else {
            triangle.addPoint(x + expanderSize / 2, y);
            triangle.addPoint(x, y + expanderSize);
            triangle.addPoint(x + expanderSize, y + expanderSize);
        }
        g.setColor(JBColor.DARK_GRAY);
        g.fillPolygon(triangle);
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
            String number = String.format("%.4G", startTime + i * tickInterval);
            TextLayout layout = new TextLayout(number, font, fontRenderContext);
            int numberWidth = (int)layout.getBounds().getWidth();
            g.drawString(number, curTickX - (numberWidth / 2), topY + tickDashSize + labelPadding + textHeight);

            curTickX += tickSpacing;
        }
    }

    private boolean expanded(String key, boolean defaultValue) {
        return expanded.computeIfAbsent(key, k -> defaultValue);
    }

    public void endLiveUpdates() {
        terminateTicker();
    }

    private void terminateTicker() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }
}
