package edument.perl6idea.project;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.GeneralProjectSettingsElement;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectStructureElementConfigurable;
import com.intellij.openapi.roots.ui.configuration.projectRoot.StructureConfigurableContext;
import com.intellij.openapi.roots.ui.configuration.projectRoot.daemon.ProjectStructureElement;
import com.intellij.openapi.ui.DetailsComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ProjectConfigurable extends ProjectStructureElementConfigurable<Project> implements DetailsComponent.Facade {
    private final Project myProject;
    private final GeneralProjectSettingsElement mySettingsElement;
    private DetailsComponent myDetailsComponent;

    public Perl6ProjectConfigurable(Project project, StructureConfigurableContext context) {
        myProject = project;
        mySettingsElement = new GeneralProjectSettingsElement(context);
        //initUI();
    }

    //private void initUI() {
    //    myPanel
    //}

    @Nullable
    @Override
    public ProjectStructureElement getProjectStructureElement() {
        return mySettingsElement;
    }

    @Override
    public void setDisplayName(String name) {
    }

    @Override
    public Project getEditableObject() {
        return null;
    }

    @Override
    public String getBannerSlogan() {
        return String.format("General Settings for Project '%s'", myProject.getName());
    }

    @Override
    public JComponent createOptionsPanel() {
        myDetailsComponent = new DetailsComponent(false, false);
        myDetailsComponent.setContent(new JLabel("Various project settings here"));
        myDetailsComponent.setText(getBannerSlogan());
        return myDetailsComponent.getComponent();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Project";
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public DetailsComponent getDetailsComponent() {
        return myDetailsComponent;
    }
}
