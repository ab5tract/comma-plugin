package org.raku.comma.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import org.raku.comma.psi.RakuFile;
import org.jetbrains.annotations.NotNull;

public class RakuReplUsingThisModuleAction extends RakuLaunchReplAction {
    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean available = false;
        if (getSdkHome(event) != null) {
            PsiFile currentFile = event.getData(CommonDataKeys.PSI_FILE);
            available = currentFile instanceof RakuFile &&
                        ((RakuFile)currentFile).getEnclosingRakuModuleName() != null;
        }
        event.getPresentation().setEnabledAndVisible(available);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile currentFile = e.getData(CommonDataKeys.PSI_FILE);
        if (currentFile instanceof RakuFile) {
            String moduleName = ((RakuFile)currentFile).getEnclosingRakuModuleName();
            startRepl(e, moduleName);
        }
    }
}
