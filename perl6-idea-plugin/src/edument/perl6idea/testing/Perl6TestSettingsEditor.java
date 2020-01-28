package edument.perl6idea.testing;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class Perl6TestSettingsEditor extends SettingsEditor<Perl6TestRunConfiguration> {
    private JTextField myDegree;
    protected EnvironmentVariablesComponent myEnvVariablesComponent = new EnvironmentVariablesComponent();

    @Override
    protected void resetEditorFrom(@NotNull Perl6TestRunConfiguration configuration) {
        myDegree.setText(String.valueOf(configuration.getParallelismDegree()));
        myEnvVariablesComponent.setEnvs(configuration.getEnvs());
        myEnvVariablesComponent.setPassParentEnvs(configuration.isPassParentEnvs());
    }

    @Override
    protected void applyEditorTo(@NotNull Perl6TestRunConfiguration configuration) throws ConfigurationException {
        String text = myDegree.getText();
        try {
            Integer degree = Integer.valueOf(text);
            configuration.setParallelismDegree(degree);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Number of tests must be a positive integer");
        }
        configuration.setEnvs(myEnvVariablesComponent.getEnvs());
        configuration.setPassParentEnvs(myEnvVariablesComponent.isPassParentEnvs());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel();
        panel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));
        myDegree = new JTextField(String.valueOf(1)) {
            @Override
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        if (str == null) return;
                        String oldString = getText(0, getLength());
                        String newString = oldString.substring(0, offs) + str + oldString.substring(offs);
                        try {
                            // Check if we can parse string as int
                            int degree = Integer.parseInt(newString);
                            if (degree > 0)
                                super.insertString(offs, str, a);
                        }
                        catch (NumberFormatException ignored) {
                        }
                    }
                };
            }
        };
        LabeledComponent<JTextField> degree = LabeledComponent.create(myDegree, "Tests run in parallel", BorderLayout.WEST);
        panel.add(degree);
        panel.add(myEnvVariablesComponent);
        return panel;
    }
}
