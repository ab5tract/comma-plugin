package edument.perl6idea.profiler.ui;

import com.intellij.openapi.ui.ComboBox;
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
    private final List<String> myModuleNames;
    private final List<String> myModuleBasePaths;
    private final ComboBox<Perl6ProfilerFrameResultFilter> myHideExternalsCombo;

    public Perl6ProfileNodeRenderer(List<String> moduleBasePaths, List<String> moduleNames, ComboBox<Perl6ProfilerFrameResultFilter> hideExternalsComboBox) {
        myModuleNames = moduleNames;
        myModuleBasePaths = moduleBasePaths;
        myHideExternalsCombo = hideExternalsComboBox;
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
        if (myModuleNames == null || model.isCellInternal(table.convertRowIndexToModel(row), myModuleNames, myModuleBasePaths,
                                                          myHideExternalsCombo.getItem())) {
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
