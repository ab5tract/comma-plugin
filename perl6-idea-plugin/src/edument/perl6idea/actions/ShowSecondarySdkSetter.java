package edument.perl6idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Condition;
import edument.perl6idea.project.projectWizard.components.JdkComboBox;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.services.Perl6BackupSDKService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ShowSecondarySdkSetter extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        Perl6BackupSDKService service = project.getService(Perl6BackupSDKService.class);
        String currentSdk = service.getProjectSdkPath(project.getProjectFilePath());
        new SecondarySdkSelector(project, currentSdk).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }

    static class SecondarySdkSelector extends DialogWrapper {
        protected final ProjectSdksModel myModel;
        private final JdkComboBox mySdkCheckbox;
        private final Project myProject;

        protected SecondarySdkSelector(@Nullable Project project, String currentSdk) {
            super(project, true);
            myProject = project;
            myModel = new ProjectSdksModel();
            myModel.reset(project);
            Condition<SdkTypeId> isRakuSdk = (sdk) -> sdk instanceof Perl6SdkType;
            mySdkCheckbox = new JdkComboBox(project, myModel, isRakuSdk, null, isRakuSdk, null);
            for (Sdk sdk : myModel.getSdks()) {
                if (sdk.getSdkType() instanceof Perl6SdkType) {
                    if (Objects.equals(sdk.getHomePath(), currentSdk)) {
                        mySdkCheckbox.setSelectedJdk(sdk);
                        break;
                    }
                }
            }
            init();
            setTitle("Set Raku SDK");
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel();
            panel.add(mySdkCheckbox);
            return panel;
        }

        @Override
        public @Nullable JComponent getPreferredFocusedComponent() {
            return mySdkCheckbox;
        }

        @Override
        protected void doOKAction() {
            Perl6BackupSDKService service = myProject.getService(Perl6BackupSDKService.class);
            Sdk sdk = mySdkCheckbox.getSelectedJdk();
            if (sdk != null)
                service.setProjectSdkPath(myProject.getProjectFilePath(), sdk.getHomePath());
            super.doOKAction();
        }
    }
}
