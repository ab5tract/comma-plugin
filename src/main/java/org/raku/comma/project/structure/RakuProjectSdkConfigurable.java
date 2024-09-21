package org.raku.comma.project.structure;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import org.raku.comma.project.projectWizard.components.JdkComboBox;
import org.raku.comma.sdk.RakuSdkType;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raku.comma.services.RakuSDKService;

import javax.swing.*;
import java.util.Objects;

public class RakuProjectSdkConfigurable implements UnnamedConfigurable {
    private final Project myProject;
    private JComponent myJdkPanel;
    private JdkComboBox myCbProjectJdk;
    private final ProjectSdksModel myJdksModel;
    private final SdkModel.Listener myListener = new SdkModel.Listener() {
        @Override
        public void sdkAdded(@NotNull Sdk sdk) {
            try {
                myJdksModel.apply(null, true);
            } catch (ConfigurationException e) {
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

    public RakuProjectSdkConfigurable(Project project, ProjectSdksModel model) {
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
            myCbProjectJdk = new JdkComboBox(myProject, myJdksModel,
                    (sdkType) -> sdkType instanceof RakuSdkType,
                    JdkComboBox.getSdkFilter((sdkType) -> sdkType instanceof RakuSdkType),
                    (sdkType) -> sdkType instanceof RakuSdkType,
                    (foo) -> {
                    }
            );
            myCbProjectJdk.addActionListener(event -> {
                String sdkHome = Objects.requireNonNull(myCbProjectJdk.getSelectedJdk()).getHomePath();
                assert sdkHome != null;
                PropertiesComponent properties = PropertiesComponent.getInstance(myProject);
                if (RakuSdkType.getInstance().isValidSdkHome(sdkHome)) {
                    myProject.getService(RakuSDKService.class)
                             .setProjectSdkPath(sdkHome);
                    properties.setValue("raku.sdk.selected", sdkHome);
                } else {
                    throw new RuntimeException("Invalid SDK in location '%s'".formatted(sdkHome));
                }
            });
            final String text = "<html><b>Project SDK:</b><br>This SDK is default for all project modules.</html>";
            myJdkPanel.add(new JLabel(text), "wrap, span 3");
            myJdkPanel.add(myCbProjectJdk);
        }
        return myJdkPanel;
    }

    private void reloadModel() {
        var service = myProject.getService(RakuSDKService.class);
        final String rakuSdkHome = service.getProjectSdkPath();

        if (rakuSdkHome != null) {
            final String rakuSdkName = RakuSdkType.suggestSdkName(rakuSdkHome);
            if (rakuSdkName != null) {
                final Sdk rakuSdk = myJdksModel.findSdk(rakuSdkName);
                if (rakuSdk != null && rakuSdk.getHomePath() != null) {
                    service.setProjectSdkPath(rakuSdk.getHomePath());
                } else {
                    myCbProjectJdk.setInvalidJdk(rakuSdkName);
                }
            }
        } else {
            myCbProjectJdk.setSelectedJdk(null);
        }
    }

    @Override
    public boolean isModified() {
        final String rakuSdkHome = myProject.getService(RakuSDKService.class)
                                            .getProjectSdkPath();
        return ! (rakuSdkHome != null && rakuSdkHome.equals(Objects.requireNonNull(getSelectedProjectJdk()).getHomePath()));
    }

    @Override
    public void apply() {
        String sdkHome = Objects.requireNonNull(getSelectedProjectJdk()).getHomePath();
        if (sdkHome != null) {
            myProject.getService(RakuSDKService.class)
                     .setProjectSdkPath(sdkHome);
        }
    }

    @Override
    public void reset() {
        reloadModel();

        final String sdkName = myProject.getService(RakuSDKService.class).getProjectSdkName();
        if (sdkName != null) {
            final Sdk jdk = myJdksModel.findSdk(sdkName);
            if (jdk != null) {
                myCbProjectJdk.setSelectedJdk(jdk);
            } else {
                myCbProjectJdk.setInvalidJdk(sdkName);
            }
        } else {
            myCbProjectJdk.setSelectedJdk(null);
        }
    }

    @Override
    public void disposeUIResources() {
        myJdksModel.removeListener(myListener);
        myJdkPanel = null;
        myCbProjectJdk = null;
    }
}
