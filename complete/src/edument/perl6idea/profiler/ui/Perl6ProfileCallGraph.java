package edument.perl6idea.profiler.ui;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import edument.perl6idea.profiler.model.Perl6ProfileCall;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Perl6ProfileCallGraph extends JPanel {
    public static final int SIDE_OFFSET = 10;
    public static final int ITEM_HEIGHT = 16;
    public static final int START_Y_OFFSET = 26;
    public static final Color BASIC_ROOT_COLOR = JBColor.BLUE;
    private final Perl6ProfileData myProfileData;
    private final Perl6ProfileCallGraphPanel myGraphPanel;
    private Perl6ProfileCall myRoot;
    private Rectangle graphSizes;
    private Queue<CallItem> callItems = new ConcurrentLinkedQueue<>();
    private int maxHeight = START_Y_OFFSET;
    private JScrollPane myScroll;
    private int myRootHeight;

    public Perl6ProfileCallGraph(Perl6ProfileData profileData, Perl6ProfileCallGraphPanel panel) {
        myScroll = panel.getScrollPane();
        myGraphPanel = panel;
        myProfileData = profileData;
        myRoot = profileData.getProfileCallById(1, 15, null);
        addMouseEventHandlers();
        updateAxis();
    }

    private void addMouseEventHandlers() {
        MouseAdapter clickAdapterHandler = new MouseAdapter() {
            private CallItem currentTooltipCall;
            private JBPopup currentPopup;

            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                if (e.getButton() == MouseEvent.BUTTON1 && graphSizes.contains(point)) {
                    for (CallItem item : callItems) {
                        if (item.contains(point)) {
                            myRoot = myProfileData.getProfileCallById(item.myCall.id, 15, item.myCall.parent);
                            repaint();
                            updateAxis();
                        }
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
                if (graphSizes.contains(point)) {
                    for (CallItem item : callItems) {
                        if (item.contains(point)) {
                            if (item != currentTooltipCall) {
                                closeActiveTooltip();
                                showTooltip(point, item);
                            }
                            return;
                        }
                    }
                }
                closeActiveTooltip();
            }

            private void showTooltip(Point point, CallItem item) {
                currentTooltipCall = item;
                JBPopup popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(new CallGraphTooltip(currentTooltipCall), null)
                    .setFocusOwners(new Component[] { Perl6ProfileCallGraph.this })
                    .createPopup();
                currentPopup = popup;
                popup.show(new RelativePoint(Perl6ProfileCallGraph.this, point));
            }

            private void closeActiveTooltip() {
                if (currentTooltipCall != null) {
                    currentPopup.cancel();
                    Disposer.dispose(currentPopup);
                    currentPopup = null;
                    currentTooltipCall = null;
                }
            }
        };
        addMouseListener(clickAdapterHandler);
        addMouseMotionListener(clickAdapterHandler);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        callItems = new ConcurrentLinkedQueue<>();
        maxHeight = -1;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        paintGraph(g2d);
        graphSizes = new Rectangle(SIDE_OFFSET, START_Y_OFFSET, getWidth() - 2 * SIDE_OFFSET, getHeight() - START_Y_OFFSET);
    }

    private void paintGraph(Graphics2D g) {
        int currentY = START_Y_OFFSET;
        boolean shouldScroll = false;

        currentY = drawParentBreadcrumbs(g, currentY);

        // Draw root
        g.setColor(BASIC_ROOT_COLOR);
        // We will use that to scroll to the root element after painting is done
        if (currentY != myRootHeight) {
            myRootHeight = currentY;
            shouldScroll = true;
        }
        drawCall(g, myRoot, SIDE_OFFSET, getWidth() - 2 * SIDE_OFFSET, currentY);
        currentY += ITEM_HEIGHT;

        drawChildren(g, myRoot, BASIC_ROOT_COLOR, SIDE_OFFSET, getWidth() - 2 * SIDE_OFFSET, currentY);
        Dimension size = new Dimension(getParent().getWidth(), START_Y_OFFSET + maxHeight);
        setPreferredSize(size);
        revalidate();
        if (shouldScroll)
            myScroll.getVerticalScrollBar().setValue(myRootHeight - 3 * ITEM_HEIGHT);
    }

    private int drawParentBreadcrumbs(Graphics2D g, int height) {
        g.setColor(JBColor.LIGHT_GRAY);
        int currentItemHeight = height;
        if (myRoot.parent != null) {
            Deque<Perl6ProfileCall> parents = new ArrayDeque<>();
            Perl6ProfileCall nextParent = myRoot.parent;
            while (nextParent != null) {
                parents.addFirst(nextParent);
                nextParent = nextParent.parent;
            }
            for (Perl6ProfileCall call : parents) {
                drawCall(g, call, SIDE_OFFSET, getWidth() - 2 * SIDE_OFFSET, currentItemHeight);
                currentItemHeight += ITEM_HEIGHT;
            }
        }
        return currentItemHeight;
    }

    private void updateAxis() {
        String overallTimeString = String.format("Time: %s μs", myRoot.inclusiveTime);
        myGraphPanel.getTimeLabel().setText(overallTimeString);
    }

    public void switchToThread(int newRootID) {
        myRoot = myProfileData.getProfileCallById(newRootID, 15, null);
        repaint();
        JScrollBar scrollBar = myGraphPanel.getScrollPane().getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

    private static Color getBrighterColor(Color color) {
        return ColorHelper.adjustColor(color, 8);
    }

    private static Color getDarkerColor(Color color) {
        return ColorHelper.adjustColor(color, -9);
    }

    private boolean drawCall(Graphics2D g, Perl6ProfileCall root, int startX, int callRectWidth, int height) {
        callItems.add(new CallItem(new Rectangle(startX, height, callRectWidth, ITEM_HEIGHT), root));
        if (height > maxHeight) {
            maxHeight = height;
        }

        Color background = g.getColor();
        String callName = root.name.isEmpty() ? "<anon>" : root.name;

        // Draw a filled rectangle
        g.fillRect(startX, height, callRectWidth, ITEM_HEIGHT);

        // Draw a border
        g.setColor(JBColor.BLACK);
        g.drawRect(startX, height, callRectWidth, ITEM_HEIGHT);


        // Draw text
        // Set a contrast color
        g.setColor(getComplimentaryColor(background));
        // Get sizes of name label
        FontMetrics fm = g.getFontMetrics();
        // Check if name fits in this width and try to minimify if not
        callName = minifyRoutineName(g, callRectWidth, callName, fm);

        // If no name is returned, just not enough width, skip whole node
        if (callName == null)
            return false;

        Rectangle2D labelRect = fm.getStringBounds(callName, g);
        int textStartX = startX + (int)(callRectWidth / 2 - (labelRect.getCenterX()));
        int textStartY = (int)(height + ITEM_HEIGHT / 2 - labelRect.getCenterY());
        g.drawString(callName, textStartX, textStartY);

        // Reset color to background one once we drew border and label
        g.setColor(background);
        // Return if we should draw children of this call
        return callName.length() > 1;
    }

    private static Color getComplimentaryColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    @Nullable
    private static String minifyRoutineName(Graphics2D g, int callRectWidth, String callName, FontMetrics fm) {
        Rectangle2D labelRect = fm.getStringBounds(callName, g);

        // There are number of minification stages for routine name to fit into its rectangle
        // If it is smaller than a rectangle, immediately return it:
        if (labelRect.getWidth() < callRectWidth)
            return callName;

        // As long as we need we need to minify it, try to chop off chars...
        while (callName.length() != 0 && labelRect.getWidth() > callRectWidth) {
            callName = callName.substring(0, callName.length() - 1);
            labelRect = fm.getStringBounds(callName + "...", g);
        }

        // If it fits with some characters dropped plus the "..." suffix
        if (callName.length() != 0) {
            return callName + "...";
        } else {
            // We try "-" as minimal name before returning "don't draw at all" flag
            if (fm.getStringBounds("-", g).getWidth() <= callRectWidth)
                return "-";
        }
        return null;
    }

    private void drawChildren(Graphics2D g, Perl6ProfileCall call, Color color, int callStartX, int maxItemWidth, int lineHeight) {
        // We need to move to the right for every child, starting from far left point of the parent
        int currentX = callStartX;
        // We print children recursively depth-first, so need to re-apply level color on every next item
        Color currentLevelColor = g.getColor();
        if (call.callees == null)
            return;
        for (Perl6ProfileCall childCall : call.callees) {
            g.setColor(currentLevelColor);
            // We need to calculate x - where to start, and width - how long it is
            int width = (int)(maxItemWidth * ((float)childCall.inclusiveTime / call.inclusiveTime));
            if (width == 0)
                continue;
            if (drawCall(g, childCall, currentX, width, lineHeight)) {
                g.setColor(getBrighterColor(color));
                drawChildren(g, childCall, getBrighterColor(color), currentX, width, lineHeight + ITEM_HEIGHT);
            }
            currentX += width;
        }
    }

    private static class ColorHelper {
        private static Color adjustColor(Color color, float delta) {
            float[] hsl = RGBtoHSL(color);
            float multiplier = (100.0f + delta) / 100.0f;
            hsl[2] = delta > 0 ? Math.min(100.0f, hsl[2] * multiplier) : Math.max(0.0f, hsl[2] * multiplier);
            return HSLtoRGB(hsl);
        }

        private static Color HSLtoRGB(float[] hsb) {
            float h = hsb[0];
            float s = hsb[1];
            float l = hsb[2];
            h = h % 360.0f;
            h /= 360f;
            s /= 100f;
            l /= 100f;

            float q = l < 0.5 ? l * (1 + s) : (l + s) - (s * l);
            float p = 2 * l - q;

            float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
            float g = Math.max(0, HueToRGB(p, q, h));
            float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

            r = Math.min(r, 1.0f);
            g = Math.min(g, 1.0f);
            b = Math.min(b, 1.0f);
            return new Color(r, g, b);
        }

        private static float HueToRGB(float p, float q, float h) {
            if (h < 0) h += 1;
            if (h > 1) h -= 1;

            if (6 * h < 1) {
                return p + ((q - p) * 6 * h);
            } else if (2 * h < 1) {
                return q;
            } else if (3 * h < 2) {
                return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
            }

            return p;
        }

        private static float[] RGBtoHSL(Color color) {
            float[] rgb = color.getRGBColorComponents(null);
            float r = rgb[0];
            float g = rgb[1];
            float b = rgb[2];

            //Minimum and Maximum RGB values are used in the HSL calculations
            float min = Math.min(r, Math.min(g, b));
            float max = Math.max(r, Math.max(g, b));

            // Calculate the Hue
            float h = 0;

            if (max == min)
                h = 0;
            else if (max == r)
                h = ((60 * (g - b) / (max - min)) + 360) % 360;
            else if (max == g)
                h = (60 * (b - r) / (max - min)) + 120;
            else if (max == b)
                h = (60 * (r - g) / (max - min)) + 240;

            // Calculate the Luminance
            float l = (max + min) / 2;

            // Calculate the Saturation
            float s;
            if (max == min)
                s = 0;
            else if (l <= .5f)
                s = (max - min) / (max + min);
            else
                s = (max - min) / (2 - max - min);

            return new float[]{h, s * 100, l * 100};
        }
    }

    public class CallItem {
        private final Rectangle myArea;
        public final Perl6ProfileCall myCall;

        public CallItem(Rectangle area, Perl6ProfileCall call) {
            myArea = area;
            myCall = call;
        }

        public boolean contains(Point point) {
            return myArea.contains(point);
        }
    }
}
