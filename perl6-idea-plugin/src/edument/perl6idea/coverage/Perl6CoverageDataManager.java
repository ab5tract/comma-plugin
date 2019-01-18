package edument.perl6idea.coverage;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import java.io.File;

public abstract class Perl6CoverageDataManager implements ProjectComponent {
    public static Perl6CoverageDataManager getInstance(Project project) {
        return project.getComponent(Perl6CoverageDataManager.class);
    }

    abstract void addSuiteFromSingleCoverageFile(File data, Perl6CoverageCommandLineState state);

    abstract void changeToSuite(Perl6CoverageSuite suite);

    abstract public void triggerPresentationUpdate();
}
