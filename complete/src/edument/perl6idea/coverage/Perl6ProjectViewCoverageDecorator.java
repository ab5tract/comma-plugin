package edument.perl6idea.coverage;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;

public class Perl6ProjectViewCoverageDecorator implements ProjectViewNodeDecorator {
    private final Perl6CoverageDataManager coverageDataManager;

    public Perl6ProjectViewCoverageDecorator(Perl6CoverageDataManager coverageDataManager) {
        this.coverageDataManager = coverageDataManager;
    }

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        VirtualFile file = node.getVirtualFile();
        if (file == null)
            return;
        if (file.isDirectory()) {
            data.clearText();
            data.addText(file.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            if (coverageDataManager != null && coverageDataManager.hasCurrentCoverageSuite()) {
                CoverageStatistics stats = coverageDataManager.coverageForDirectory(file);
                if (stats != null)
                    addCoverageStatistics(data, stats);
            }
        }
        else if (file.getPath().endsWith(".pm6")) {
            data.clearText();
            data.addText(file.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            if (coverageDataManager != null && coverageDataManager.hasCurrentCoverageSuite()) {
                CoverageStatistics stats = coverageDataManager.coverageForFile(file);
                if (stats != null)
                    addCoverageStatistics(data, stats);
            }
        }
    }

    private void addCoverageStatistics(PresentationData data, CoverageStatistics stats) {
        data.addText(" (" + stats.getCoveredLines() + " / " + stats.getCoverableLines() +
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

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {
    }
}
