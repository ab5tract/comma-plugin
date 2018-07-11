package edument.perl6idea.project;

import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.util.Comparing;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Perl6SdkConfigurable implements UnnamedConfigurable {
    private Project myProject;
    private JComponent myJdkPanel;
    private Perl6JdkComboBox myCbProjectJdk;
    private ProjectSdksModel myJdksModel;
    private final SdkModel.Listener myListener = new SdkModel.Listener() {
        @Override
        public void sdkAdded(Sdk sdk) {
            reloadModel();
        }

        @Override
        public void beforeSdkRemove(Sdk sdk) {
            reloadModel();
        }

        @Override
        public void sdkChanged(Sdk sdk, String previousName) {
            reloadModel();
        }

        @Override
        public void sdkHomeSelected(Sdk sdk, String newSdkHome) {
            reloadModel();
        }
    };

    public Perl6SdkConfigurable(Project project, ProjectSdksModel model) {
        myProject = project;
        myJdksModel = model;
        myJdksModel.addListener(myListener);
    }

    @Nullable
    public Sdk getSelectedProjectJdk() {
        return myJdksModel.findSdk(myCbProjectJdk.getSelectedJdk());
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        if (myJdkPanel == null) {
            myJdkPanel = new JPanel(new MigLayout("", "left", "top"));
            myCbProjectJdk = new Perl6JdkComboBox(myJdksModel);
            myCbProjectJdk.insertItemAt(new Perl6JdkComboBox.NoneJdkComboBoxItem(), 0);
            myCbProjectJdk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myJdksModel.setProjectSdk(myCbProjectJdk.getSelectedJdk());
                }
            });
            final String text = "<html><b>Project SDK:</b><br>This SDK is default for all project modules.<br>" +
                                "A module-specific SDK can be configured for each of the modules as required.</html>";
            myJdkPanel.add(new JLabel(text), "wrap, span 3");
            myJdkPanel.add(myCbProjectJdk);
            final JButton setUpButton = new JButton(ApplicationBundle.message("button.new"));
            myCbProjectJdk.setSetupButton(setUpButton, myProject, myJdksModel, new JdkComboBox.NoneJdkComboBoxItem(), null, false);
            myJdkPanel.add(setUpButton);
            final JButton editButton = new JButton(ApplicationBundle.message("button.edit"));
            myCbProjectJdk.setEditButton(editButton, myProject, () -> myJdksModel.getProjectSdk());
            myJdkPanel.add(editButton);
           }
        return myJdkPanel;
    }

    private void reloadModel() {
        final Sdk projectJdk = myJdksModel.getProjectSdk();
        if (myCbProjectJdk != null)
            myCbProjectJdk.reloadModel(new Perl6JdkComboBox.NoneJdkComboBoxItem(), myProject);
        final String sdkName = projectJdk == null ? ProjectRootManager.getInstance(myProject).getProjectSdkName() : projectJdk.getName();
        if (sdkName != null) {
            final Sdk jdk = myJdksModel.findSdk(sdkName);
            if (jdk != null)
                myCbProjectJdk.setSelectedJdk(jdk);
            else
                myCbProjectJdk.setInvalidJdk(sdkName);
        } else
            myCbProjectJdk.setSelectedJdk(null);
    }

    @Override
    public boolean isModified() {
        final Sdk projectSdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
        return !Comparing.equal(projectSdk, getSelectedProjectJdk());
    }

    @Override
    public void apply() {
        ProjectRootManager.getInstance(myProject).setProjectSdk(getSelectedProjectJdk());
    }

    @Override
    public void reset() {
        reloadModel();

        final String sdkName = ProjectRootManager.getInstance(myProject).getProjectSdkName();
        if (sdkName != null) {
            final Sdk jdk = myJdksModel.findSdk(sdkName);
            if (jdk != null)
                myCbProjectJdk.setSelectedJdk(jdk);
            else
                myCbProjectJdk.setInvalidJdk(sdkName);
        } else
            myCbProjectJdk.setSelectedJdk(null);
    }

    @Override
    public void disposeUIResources() {
        myJdksModel.removeListener(myListener);
        myJdkPanel = null;
        myCbProjectJdk = null;
    }
}
