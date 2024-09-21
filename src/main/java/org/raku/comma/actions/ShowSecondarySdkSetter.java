package org.raku.comma.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Condition;
import org.raku.comma.project.projectWizard.components.JdkComboBox;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.services.RakuSDKService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ShowSecondarySdkSetter extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        assert project != null;
        RakuSDKService service = project.getService(RakuSDKService.class);
        String currentSdkPath = service.getProjectSdkPath();
        new SecondarySdkSelector(project, currentSdkPath).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (e.getProject() == null) {
            e.getPresentation().setEnabledAndVisible(false);
        }
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
            Condition<SdkTypeId> isRakuSdk = (sdk) -> sdk instanceof RakuSdkType;
            mySdkCheckbox = new JdkComboBox(project, myModel, isRakuSdk, null, isRakuSdk, null);
            for (Sdk sdk : myModel.getSdks()) {
                if (sdk.getSdkType() instanceof RakuSdkType) {
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
            RakuSDKService service = myProject.getService(RakuSDKService.class);
            Sdk sdk = mySdkCheckbox.getSelectedJdk();
            if (sdk != null && sdk.getHomePath() != null) {
                service.setProjectSdkPath(sdk.getHomePath());
            }
            super.doOKAction();
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
