package org.raku.comma.coverage;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

public class RakuCoverageHideAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        RakuCoverageDataManager.getInstance(e.getProject()).hideCoverageData();
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        if (e.getProject() == null) {
            presentation.setEnabled(false);
        }
        else {
            presentation.setEnabled(RakuCoverageDataManager.getInstance(e.getProject()).hasCurrentCoverageSuite());
        }
    }
}
