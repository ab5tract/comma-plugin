package edument.perl6idea.coverage;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import edument.perl6idea.filetypes.Perl6ModuleFileType;

public class Perl6ProjectViewCoverageDecorator implements ProjectViewNodeDecorator {
    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        VirtualFile file = node.getVirtualFile();
        if (file == null || node.getProject() == null)
            return;
        Perl6CoverageDataManager coverageDataManager = node.getProject().getService(Perl6CoverageDataManager.class);
        if (coverageDataManager == null)
            return;
        if (file.isDirectory()) {
            data.clearText();
            data.addText(file.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            if (coverageDataManager.hasCurrentCoverageSuite()) {
                CoverageStatistics stats = coverageDataManager.coverageForDirectory(file);
                if (stats != null)
                    addCoverageStatistics(data, stats);
            }
        }
        else if (file.getFileType() instanceof Perl6ModuleFileType) {
            data.clearText();
            data.addText(file.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            if (coverageDataManager.hasCurrentCoverageSuite()) {
                CoverageStatistics stats = coverageDataManager.coverageForFile(file);
                if (stats != null)
                    addCoverageStatistics(data, stats);
            }
        }
    }

    private static void addCoverageStatistics(PresentationData data, CoverageStatistics stats) {
        data.addText(" (" + stats.coveredLines() + " / " + stats.coverableLines() +
                     " statements; ", SimpleTextAttributes.GRAY_ATTRIBUTES);
        data.addText(stats.percent() + "%)", percentAttributes(stats));
        data.addText(")", SimpleTextAttributes.GRAY_ATTRIBUTES);
    }

    private static SimpleTextAttributes percentAttributes(CoverageStatistics stats) {
        if (stats.isGood())
            return new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN);
        if (stats.isPoor())
            return new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED);
        return new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.YELLOW);
    }
}
