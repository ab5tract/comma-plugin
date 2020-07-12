package edument.perl6idea.profiler.ui;

import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import edument.perl6idea.profiler.model.Perl6ProfileModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class Perl6ProfileNodeRenderer extends ColoredTableCellRenderer {
    private static final SimpleTextAttributes DEFAULT_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, null);
    private static final SimpleTextAttributes SPECIAL_NODE_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(
        JBColor.LIGHT_GRAY, Gray._130));
    private final List<String> myModuleBasePaths;

    public Perl6ProfileNodeRenderer(List<String> moduleBasePaths) {
        myModuleBasePaths = moduleBasePaths;
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

        SimpleTextAttributes style;
        if (myModuleBasePaths == null || model.isCellInternal(table.convertRowIndexToModel(row), myModuleBasePaths)) {
            style = SPECIAL_NODE_ATTRIBUTES;
        } else {
            style = DEFAULT_ATTRIBUTES;
        }

        int modelColumn = table.convertColumnIndexToModel(column);
        if (model.needsSpecialRendering(modelColumn)) {
            append(model.renderNode(modelColumn, value), style);
        } else {
            append(value.toString(), style);
        }
    }
}
