package org.raku.profiler.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import org.raku.profiler.model.RakuProfileModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class RakuProfileNodeRenderer extends ColoredTableCellRenderer {
    private static final SimpleTextAttributes DEFAULT_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, null);
    private static final SimpleTextAttributes SPECIAL_NODE_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(
        JBColor.LIGHT_GRAY, Gray._130));
    private final List<String> myModuleNames;
    private final List<String> myModuleBasePaths;
    private final ComboBox<RakuProfilerFrameResultFilter> myHideExternalsCombo;

    public RakuProfileNodeRenderer(List<String> moduleBasePaths, List<String> moduleNames, ComboBox<RakuProfilerFrameResultFilter> hideExternalsComboBox) {
        myModuleNames = moduleNames;
        myModuleBasePaths = moduleBasePaths;
        myHideExternalsCombo = hideExternalsComboBox;
    }

    @Override
    protected void customizeCellRenderer(@NotNull JTable table,
                                         @Nullable Object value,
                                         boolean selected,
                                         boolean hasFocus,
                                         int row,
                                         int column) {
        if (value == null) {
            append("<null>");
            return;
        }

        RakuProfileModel model = (RakuProfileModel)table.getModel();

        SimpleTextAttributes style;
        if (myModuleNames == null || model.isCellInternal(table.convertRowIndexToModel(row), myModuleNames, myModuleBasePaths,
                                                          (RakuProfilerFrameResultFilter)myHideExternalsCombo.getSelectedItem())) {
            style = SPECIAL_NODE_ATTRIBUTES;
        } else {
            style = DEFAULT_ATTRIBUTES;
        }
        append(value.toString(), style);
    }
}
