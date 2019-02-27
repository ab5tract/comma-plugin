package edument.perl6idea.coverage;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

public class Perl6CoverageHideAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Perl6CoverageDataManager.getInstance(e.getProject()).hideCoverageData();
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        if (e.getProject() == null) {
            presentation.setEnabled(false);
        }
        else {
            presentation.setEnabled(Perl6CoverageDataManager.getInstance(e.getProject()).hasCurrentCoverageSuite());
        }
    }
}
