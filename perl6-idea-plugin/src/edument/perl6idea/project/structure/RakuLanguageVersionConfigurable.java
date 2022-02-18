package edument.perl6idea.project.structure;

import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.language.RakuLanguageVersion;
import edument.perl6idea.language.RakuLanguageVersionService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RakuLanguageVersionConfigurable implements UnnamedConfigurable {
    private final Project myProject;
    private final RakuLanguageVersionService myService;
    private ComboBox<RakuLanguageVersion> myLanguageVersionComboBox;
    private JComponent myPanel;
    private RakuLanguageVersion newVersion;
    private RakuLanguageVersion oldVersion;
    private boolean oldIsExplicit;
    private boolean newIsExplicit;
    private JBCheckBox myExplicitCheckbox;

    public RakuLanguageVersionConfigurable(Project project) {
        myProject = project;
        myService = project.getService(RakuLanguageVersionService.class);
        oldIsExplicit = newIsExplicit = myService.getIsExplicit();
        oldVersion = newVersion = myService.getVersion();
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (myPanel == null) {
            myPanel = new JPanel(new MigLayout("", "left", "top"));
            final String text = "<html><b>Raku language version:</b><br>Defines what features are enabled.</html>";
            myPanel.add(new JLabel(text), "wrap, span 3");
            myLanguageVersionComboBox = new ComboBox<>();
            myLanguageVersionComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setIcon(Perl6Icons.CAMELIA);
                    setText(value.toString());
                    return comp;
                }
            });
            for (RakuLanguageVersion value : RakuLanguageVersion.values())
                myLanguageVersionComboBox.addItem(value);
            if (oldVersion != null)
                myLanguageVersionComboBox.setSelectedItem(oldVersion);

            myLanguageVersionComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object item = myLanguageVersionComboBox.getSelectedItem();
                    if (item instanceof RakuLanguageVersion)
                        newVersion = (RakuLanguageVersion)item;
                }
            });

            myExplicitCheckbox = new JBCheckBox();
            myExplicitCheckbox.setSelected(oldIsExplicit);
            myExplicitCheckbox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    newIsExplicit = myExplicitCheckbox.isSelected();
                }
            });

            myPanel.add(myLanguageVersionComboBox, "wrap, span 2");
            myPanel.add(myExplicitCheckbox);
            myPanel.add(new JLabel("Include Language Version Declaration"));
        }
        return myPanel;
    }

    @Override
    public boolean isModified() {
        return newVersion != oldVersion || oldIsExplicit != newIsExplicit;
    }

    @Override
    public void apply() {
        myService.setVersion(newVersion);
        myService.setExplicit(newIsExplicit);
        oldVersion = newVersion;
        oldIsExplicit = newIsExplicit;
    }
}
