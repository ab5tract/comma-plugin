package edument.perl6idea.pm.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.pm.RakuPackageManager;
import edument.perl6idea.pm.RakuPackageManagerKind;
import edument.perl6idea.pm.RakuPackageManagerManager;
import edument.perl6idea.pm.impl.RakuPakkuPM;
import edument.perl6idea.pm.impl.RakuZefPM;
import edument.perl6idea.psi.external.ExternalPerl6File;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Objects;

public class RakuPackageManagerConfigurable implements UnnamedConfigurable {
    public static final EmptyPMItem EMPTY_PM_ITEM = new EmptyPMItem();
    private final Project myProject;
    private final RakuPackageManagerManager myService;
    private JComponent myPanel;
    private ComboBox<RakuPackageManagerManager.PMInstanceData> myPMComboBox;
    private RakuPackageManager oldPM;
    private RakuPackageManager newPM;
    private List<VirtualFile> myPMsToAdd = new ArrayList<>();

    public RakuPackageManagerConfigurable(Project project) {
        myProject = project;
        myService = project.getService(RakuPackageManagerManager.class);
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (myPanel == null) {
            myPanel = new JPanel(new MigLayout("", "left", "top"));
            final String text = "<html><b>Project Package Manager:</b><br>This PM will be used to install dependencies from Comma.</html>";
            myPanel.add(new JLabel(text), "wrap, span 3");
            myPMComboBox = new ComboBox<>() {
            };
            myPMComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setIcon(Perl6Icons.CAMELIA);
                    if (value instanceof EmptyPMItem) {
                        setText("Add new Package Manager...");
                    }
                    else if (value instanceof RakuPackageManagerManager.PMInstanceData) {
                        setText(((RakuPackageManagerManager.PMInstanceData)value).kind.name().toLowerCase(Locale.ROOT) + " (" +
                                ((RakuPackageManagerManager.PMInstanceData)value).location + ")");
                    }
                    return comp;
                }
            });
            myPMComboBox.addItem(new EmptyPMItem());
            myPMComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object item = myPMComboBox.getSelectedItem();
                    if (item instanceof EmptyPMItem) {
                        VirtualFile file = FileChooser.chooseFile(
                            FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                                .withFileFilter(RakuPackageManagerManager::isManagerFile),
                            myProject, null);
                        if (file == null)
                            return;
                        myPMsToAdd.add(file);
                        RakuPackageManager pm = RakuPackageManagerManager.parsePM(file.getName(), file.getPath());
                        RakuPackageManagerManager.PMInstanceData newItem = new RakuPackageManagerManager.PMInstanceData(pm);
                        newPM = pm;
                        myPMComboBox.addItem(newItem);
                        myPMComboBox.setSelectedItem(newItem);
                    }
                    else if (item instanceof RakuPackageManagerManager.PMInstanceData) {
                        RakuPackageManager pmToSet = null;
                        switch (((RakuPackageManagerManager.PMInstanceData)item).kind) {
                            case EMPTY:
                                throw new IllegalArgumentException("Unknown kind of Package Manager");
                            case ZEF:
                                pmToSet = new RakuZefPM(((RakuPackageManagerManager.PMInstanceData)item).location);
                                break;
                            case PAKKU:
                                pmToSet = new RakuPakkuPM(((RakuPackageManagerManager.PMInstanceData)item).location);
                                break;
                        }
                        if (pmToSet != null)
                            newPM = pmToSet;
                    }
                }
            });
            myPanel.add(myPMComboBox);
        }
        return myPanel;
    }

    @Override
    public void reset() {
        for (RakuPackageManager manager : myService.getPMList()) {
            myPMComboBox.addItem(new RakuPackageManagerManager.PMInstanceData(manager));
        }
        oldPM = newPM = myService.getCurrentPM();
        if (oldPM == null)
            myPMComboBox.setSelectedItem(EMPTY_PM_ITEM);
        else {
            for (int i = 0; i < myPMComboBox.getItemCount(); i++) {
                if (myPMComboBox.getItemAt(i).kind == oldPM.getKind() &&
                    Objects.equals(myPMComboBox.getItemAt(i).location, oldPM.getLocation())) {
                    myPMComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    public boolean isModified() {
        return newPM != oldPM;
    }

    @Override
    public void apply() {
        oldPM = newPM;
        myService.setPM(oldPM);
        for (VirtualFile file : myPMsToAdd)
            myService.addNewPM(file, false);
        myPMsToAdd = new ArrayList<>();
    }

    private static class EmptyPMItem extends RakuPackageManagerManager.PMInstanceData {
        EmptyPMItem() {
            super(RakuPackageManagerKind.EMPTY, "");
        }
    }
}
