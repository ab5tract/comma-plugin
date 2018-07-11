package edument.perl6idea.testing;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Perl6TestSettingsEditor extends SettingsEditor<Perl6TestRunConfiguration> {
    public Perl6TestSettingsEditor(Project project) {
        super();
    }

    @Override
    protected void resetEditorFrom(@NotNull Perl6TestRunConfiguration configuration) {
    }

    @Override
    protected void applyEditorTo(@NotNull Perl6TestRunConfiguration configuration) {
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return new JLabel("Tests");
    }
}
