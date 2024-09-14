package org.raku.comma.coverage;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public abstract class RakuCoverageDataManager {
    public static RakuCoverageDataManager getInstance(Project project) {
        return project.getService(RakuCoverageDataManager.class);
    }

    abstract void addSuiteFromSingleCoverageFile(File data, RakuCoverageCommandLineState state);

    public abstract void addSuiteFromIndexFile(File index, RakuCoverageTestRunningState state);

    abstract void changeToSuite(RakuCoverageSuite suite);

    abstract void hideCoverageData();

    abstract boolean hasCurrentCoverageSuite();

    abstract public void triggerPresentationUpdate();

    public abstract CoverageStatistics coverageForFile(VirtualFile file);

    public abstract CoverageStatistics coverageForDirectory(VirtualFile file);
}
