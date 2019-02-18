package edument.perl6idea.profiler;

import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ProfileNodeRenderer extends ColoredTableCellRenderer {
    private static final SimpleTextAttributes DEFAULT_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, null);
    private static final SimpleTextAttributes SPECIAL_NODE_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(
        JBColor.LIGHT_GRAY, Gray._130));
    private final String myBaseProjectPath;

    public Perl6ProfileNodeRenderer(String baseProjectPath) {
        myBaseProjectPath = baseProjectPath;
    }

    @Override
    protected void customizeCellRenderer(JTable table,
                                         @Nullable Object value,
                                         boolean selected,
                                         boolean hasFocus,
                                         int row,
                                         int column) {
        if (value == null) {
            append("<null>");
            return;
        }

        Perl6ProfileModel model = (Perl6ProfileModel)table.getModel();
        if (myBaseProjectPath == null || model.isCellInternal(table.convertRowIndexToModel(row), myBaseProjectPath)) {
            append(value.toString(), SPECIAL_NODE_ATTRIBUTES);
        }
        else {
            append(value.toString(), DEFAULT_ATTRIBUTES);
        }
    }
}
