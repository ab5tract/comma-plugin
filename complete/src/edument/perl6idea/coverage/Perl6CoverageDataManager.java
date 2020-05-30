package edument.perl6idea.coverage;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public abstract class Perl6CoverageDataManager {
    public static Perl6CoverageDataManager getInstance(Project project) {
        return project.getService(Perl6CoverageDataManager.class);
    }

    abstract void addSuiteFromSingleCoverageFile(File data, Perl6CoverageCommandLineState state);

    public abstract void addSuiteFromIndexFile(File index, Perl6CoverageTestRunningState state);

    abstract void changeToSuite(Perl6CoverageSuite suite);

    abstract void hideCoverageData();

    abstract boolean hasCurrentCoverageSuite();

    abstract public void triggerPresentationUpdate();

    public abstract CoverageStatistics coverageForFile(VirtualFile file);

    public abstract CoverageStatistics coverageForDirectory(VirtualFile file);
}
