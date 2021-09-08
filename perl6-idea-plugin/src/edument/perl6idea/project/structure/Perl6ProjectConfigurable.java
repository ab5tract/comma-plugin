package edument.perl6idea.project.structure;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectEx;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.DetailsComponent;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.project.ProjectKt;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.pm.ui.RakuPackageManagerConfigurable;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ProjectConfigurable extends NamedConfigurable<Project> implements DetailsComponent.Facade {
    private final Project myProject;
    private DetailsComponent myDetailsComponent;
    private JPanel myPanel;
    private JTextField myProjectName;
    private Perl6ProjectSdkConfigurable myProjectSdkConfigurable;
    private RakuPackageManagerConfigurable myPackageManagerConfigurable;

    public Perl6ProjectConfigurable(Project project, Perl6StructureConfigurableContext context,
                                    ProjectSdksModel model) {
        myProject = project;
        model.reset(myProject);
        initUI(model);
    }

    private void initUI(ProjectSdksModel model) {
        myPanel = new JPanel(new MigLayout());
        myPanel.setPreferredSize(JBUI.size(700, 500));
        myPanel.setBorder(JBUI.Borders.empty(0, 10));
        if (ProjectKt.isDirectoryBased(myProject)) {
            myPanel.add(new JLabel("<html><body><b>Project name:</b></body></html>"), "wrap");
            myProjectName = new JTextField();
            myProjectName.setColumns(40);
            myPanel.add(myProjectName, "wrap");
        }
        myProjectSdkConfigurable = new Perl6ProjectSdkConfigurable(myProject, model);
        myPanel.add(myProjectSdkConfigurable.createComponent(), "shrink 0, wrap");
        myPackageManagerConfigurable = new RakuPackageManagerConfigurable(myProject);
        myPanel.add(myPackageManagerConfigurable.createComponent(), "shrink 0");
    }

    @Override
    public void setDisplayName(String name) {}

    @Override
    public Project getEditableObject() {
        return myProject;
    }

    @Override
    public String getBannerSlogan() {
        return String.format("General Settings for Project '%s'", myProject.getName());
    }

    @Override
    public JComponent createOptionsPanel() {
        myDetailsComponent = new DetailsComponent(false, false);
        myDetailsComponent.setContent(myPanel);
        myDetailsComponent.setText(getBannerSlogan());
        myProjectSdkConfigurable.createComponent();
        return myDetailsComponent.getComponent();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Project";
    }

    @Nullable
    @Override
    public Icon getIcon(boolean expanded) {
        return AllIcons.Nodes.Project;
    }

    @Override
    public boolean isModified() {
        if (!getProjectName().equals(myProject.getName())) return true;
        return myProjectSdkConfigurable.isModified() || myPackageManagerConfigurable.isModified();
    }

    @Override
    public void reset() {
        if (myProjectName != null)
            myProjectName.setText(myProject.getName());
        myProjectSdkConfigurable.reset();
        myPackageManagerConfigurable.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (myProjectName != null && StringUtil.isEmptyOrSpaces(myProjectName.getText())) {
            throw new ConfigurationException("Please, specify project name!");
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            myProjectSdkConfigurable.apply();
            myPackageManagerConfigurable.apply();
            if (myProjectName != null) {
                ((ProjectEx)myProject).setProjectName(getProjectName());
            }
        });
    }

    @Override
    public DetailsComponent getDetailsComponent() {
        return myDetailsComponent;
    }

    public String getProjectName() {
        return myProjectName != null ? myProjectName.getText().trim() : myProject.getName();
    }

    @Override
    public void disposeUIResources() {
        if (myProjectSdkConfigurable != null)
            myProjectSdkConfigurable.disposeUIResources();
    }
}
