package edument.perl6idea.formatter;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.ui.components.fields.IntegerField;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.intellij.psi.codeStyle.CodeStyleConstraints.MAX_INDENT_SIZE;
import static com.intellij.psi.codeStyle.CodeStyleConstraints.MIN_INDENT_SIZE;

public class Perl6IndentOptionsEditor extends SmartIndentOptionsEditor {
    private static final String LABEL_INDENT_LABEL = ApplicationBundle.message("editbox.indent.label.indent");

    private IntegerField myLabelIndent;
    private JLabel myLabelIndentLabel;

    private JCheckBox myLabelIndentAbsolute;

    @Override
    protected void addComponents() {
        super.addComponents();

        myLabelIndent = new IntegerField(LABEL_INDENT_LABEL, MIN_INDENT_SIZE, MAX_INDENT_SIZE);
        myLabelIndent.setColumns(4);
        add(myLabelIndentLabel = new JLabel(LABEL_INDENT_LABEL), myLabelIndent);

        myLabelIndentAbsolute = new JCheckBox(ApplicationBundle.message("checkbox.indent.absolute.label.indent"));
        add(myLabelIndentAbsolute, true);
    }

    @Override
    public boolean isModified(final CodeStyleSettings settings, final CommonCodeStyleSettings.IndentOptions options) {
        boolean isModified = super.isModified(settings, options);
        isModified |= isFieldModified(myLabelIndent, options.LABEL_INDENT_SIZE);
        isModified |= isFieldModified(myLabelIndentAbsolute, options.LABEL_INDENT_ABSOLUTE);
        return isModified;
    }

    @Override
    public void apply(final CodeStyleSettings settings, final CommonCodeStyleSettings.IndentOptions options) {
        super.apply(settings, options);
        options.LABEL_INDENT_SIZE = myLabelIndent.getValue();
        options.LABEL_INDENT_ABSOLUTE = myLabelIndentAbsolute.isSelected();
    }

    @Override
    public void reset(@NotNull final CodeStyleSettings settings, @NotNull final CommonCodeStyleSettings.IndentOptions options) {
        super.reset(settings, options);
        myLabelIndent.setValue(options.LABEL_INDENT_SIZE);
        myLabelIndentAbsolute.setSelected(options.LABEL_INDENT_ABSOLUTE);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        myLabelIndent.setEnabled(enabled);
        myLabelIndentLabel.setEnabled(enabled);
        myLabelIndentAbsolute.setEnabled(enabled);
    }
}
