package edument.perl6idea.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ProjectJdkConfigurable;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;

import java.awt.event.ActionListener;

public class Perl6SdkConfigurable extends ProjectJdkConfigurable {
    public Perl6SdkConfigurable(Project project, ProjectSdksModel model) {
        super(project, model);
    }

    public void addChangeListener(ActionListener listener) {
        super.addChangeListener(listener);
    }


}
