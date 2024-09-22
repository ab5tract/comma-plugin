package org.raku.comma.formatter;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.raku.comma.RakuLanguage;
import org.raku.comma.filetypes.RakuScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RakuCodeStylePanel extends TabbedLanguageCodeStylePanel {
    public RakuCodeStylePanel(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        super(RakuLanguage.INSTANCE, settings, originalSettings);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        addIndentOptionsTab(settings);
        addWrappingAndBracesTab(settings);
        addSpacesTab(settings);
        addEditorTab(settings);
    }

    private void addEditorTab(CodeStyleSettings settings) {
        addTab(new MyEditorPanel(settings));
    }

    private static class MyEditorPanel extends CodeStyleAbstractPanel {
        private JCheckBox myConvertToUnicodeCB;

        MyEditorPanel(CodeStyleSettings settings) {super(settings);}

        @Override
        protected @NotNull String getTabTitle() {
            return "Editor Behavior";
        }

        @Override
        protected int getRightMargin() {
            return 0;
        }

        @Override
        protected @Nullable EditorHighlighter createHighlighter(@NotNull EditorColorsScheme scheme) {
            FileType fileType = getFileType();
            return FileTypeEditorHighlighterProviders.getInstance().forFileType(fileType).getEditorHighlighter(
                null, fileType, null, scheme);
        }

        @Override
        protected @NotNull FileType getFileType() {
            return RakuScriptFileType.INSTANCE;
        }

        @Override
        protected @Nullable String getPreviewText() {
            return null;
        }

        @Override
        public void apply(CodeStyleSettings settings) {
            settings.getCustomSettings(RakuCodeStyleSettings.class).CONVERT_TO_UNICODE = myConvertToUnicodeCB.isSelected();
        }

        @Override
        public boolean isModified(CodeStyleSettings settings) {
            return settings.getCustomSettings(RakuCodeStyleSettings.class).CONVERT_TO_UNICODE != myConvertToUnicodeCB.isSelected();
        }

        @Override
        public @Nullable JComponent getPanel() {
            JPanel panel = new JPanel(new VerticalFlowLayout());
            myConvertToUnicodeCB = new JCheckBox("Convert operators to Unicode");
            panel.add(myConvertToUnicodeCB);
            return panel;
        }

        @Override
        protected void resetImpl(CodeStyleSettings settings) {
            myConvertToUnicodeCB.setSelected(settings.getCustomSettings(RakuCodeStyleSettings.class).CONVERT_TO_UNICODE);
        }
    }
}
