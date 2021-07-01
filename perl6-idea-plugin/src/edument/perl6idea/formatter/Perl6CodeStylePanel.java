package edument.perl6idea.formatter;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6CodeStylePanel extends TabbedLanguageCodeStylePanel {
    public Perl6CodeStylePanel(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        super(Perl6Language.INSTANCE, settings, originalSettings);
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
        protected String getTabTitle() {
            return "Editor Behavior";
        }

        @Override
        protected int getRightMargin() {
            return 0;
        }

        @Override
        protected @Nullable EditorHighlighter createHighlighter(EditorColorsScheme scheme) {
            FileType fileType = getFileType();
            return FileTypeEditorHighlighterProviders.INSTANCE.forFileType(fileType).getEditorHighlighter(
                ProjectUtil.guessCurrentProject(getPanel()), fileType, null, scheme);
        }

        @Override
        protected @NotNull FileType getFileType() {
            return Perl6ScriptFileType.INSTANCE;
        }

        @Override
        protected @Nullable String getPreviewText() {
            return null;
        }

        @Override
        public void apply(CodeStyleSettings settings) throws ConfigurationException {
            settings.getCustomSettings(Perl6CodeStyleSettings.class).CONVERT_TO_UNICODE = myConvertToUnicodeCB.isSelected();
        }

        @Override
        public boolean isModified(CodeStyleSettings settings) {
            return settings.getCustomSettings(Perl6CodeStyleSettings.class).CONVERT_TO_UNICODE != myConvertToUnicodeCB.isSelected();
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
            myConvertToUnicodeCB.setSelected(settings.getCustomSettings(Perl6CodeStyleSettings.class).CONVERT_TO_UNICODE);
        }
    }
}
