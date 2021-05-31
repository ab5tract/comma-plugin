package edument.perl6idea.profiler.ui;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.UIUtil;
import edument.perl6idea.profiler.model.Perl6ProfileModelWithRatio;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class PercentageTableCellRenderer implements TableCellRenderer {

    public static final DecimalFormat MS_FORMAT = new DecimalFormat("#,###");
    public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableModel model = table.getModel();
        long unboxedValue = value instanceof Integer ?(long)(int)value : (long)value;
        double ratio;
        if (model instanceof Perl6ProfileModelWithRatio) {
            ratio = ((Perl6ProfileModelWithRatio)model).getRatio(unboxedValue, row, column);
        } else {
            return new JLabel("");
        }

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        container.setBorder(new JBEmptyBorder(0, 3, 0, 0));
        container.setBackground(table.getBackground());
        int width = table.getColumnModel().getColumn(column).getWidth();
        container.add(new MiniGraph(unboxedValue, ratio, width, table.getRowHeight()));
        return container;
    }

    private static class MiniGraph extends JComponent {
        private final static int PADDING = 3;
        private final double ratio;
        private final int barHeight;
        private final int width;
        private final long value;

        MiniGraph(long unboxedValue, double ratio, int width, int rowHeight) {
            this.value = unboxedValue;
            this.width = width;
            this.ratio = ratio >= 0
                         ? (ratio < 1 ? ratio : 1)
                         : 0;
            this.barHeight = rowHeight - 2 * PADDING;
        }

        @Override
        public void paint(Graphics g) {

            int shaded = (int)(ratio * width);
            if (shaded > 0) {
                g.setColor(JBColor.BLUE);
                g.fillRect(PADDING, PADDING, shaded, barHeight);
            }
            if (shaded < width) {
                // We want a light gray under both themes (otherwise the contrast on the
                // graph looks bad), thus this contortion to pick the color of the non-shaded
                // part of the bar.
                g.setColor(UIUtil.isUnderDarcula() ? JBColor.DARK_GRAY : JBColor.LIGHT_GRAY);
                g.fillRect(PADDING + shaded, PADDING, width - shaded - PADDING, barHeight);
            }
            g.setColor(UIUtil.isUnderDarcula() ? Color.BLACK : JBColor.WHITE);
            String percents = PERCENT_FORMAT.format(ratio * 100);
            String stringToPrint = String.format("%s%% (%s)", percents, MS_FORMAT.format(value));
            int textWidth = g.getFontMetrics().stringWidth(String.valueOf(stringToPrint));
            int textHeight = g.getFontMetrics().getHeight();
            int x = width / 2 - textWidth / 2;
            int y = barHeight / 2 + textHeight / 2;
            if (g instanceof Graphics2D)
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawString(stringToPrint, x, y);
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width - 2 * PADDING, barHeight + 2 * PADDING);
        }
    }
}
