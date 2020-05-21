package edument.perl6idea.project.structure;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.util.Comparing;
import edument.perl6idea.sdk.Perl6SdkType;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Perl6SdkConfigurable implements UnnamedConfigurable {
    private final Project myProject;
    private JComponent myJdkPanel;
    private JdkComboBox myCbProjectJdk;
    private final ProjectSdksModel myJdksModel;
    private final SdkModel.Listener myListener = new SdkModel.Listener() {
        @Override
        public void sdkAdded(@NotNull Sdk sdk) {
            try {
                myJdksModel.apply(null, true);
            }
            catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
            reloadModel();
        }

        @Override
        public void beforeSdkRemove(@NotNull Sdk sdk) {
            reloadModel();
        }

        @Override
        public void sdkChanged(@NotNull Sdk sdk, String previousName) {
            reloadModel();
        }

        @Override
        public void sdkHomeSelected(@NotNull Sdk sdk, @NotNull String newSdkHome) {
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
            myCbProjectJdk = new JdkComboBox(myJdksModel,
                                             (sdkType) -> sdkType instanceof Perl6SdkType);
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
        }
        return myJdkPanel;
    }

    private void reloadModel() {
        final Sdk projectJdk = myJdksModel.getProjectSdk();
        if (myCbProjectJdk != null)
            myCbProjectJdk.reloadModel();
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
