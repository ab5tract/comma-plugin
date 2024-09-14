package org.raku.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import org.raku.psi.RakuFile;
import org.jetbrains.annotations.NotNull;

public class RakuReplUsingThisModuleAction extends RakuLaunchReplAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean available = false;
        if (getSdkHome(e) != null) {
            PsiFile currentFile = e.getData(CommonDataKeys.PSI_FILE);
            available = currentFile instanceof RakuFile &&
                        ((RakuFile)currentFile).getEnclosingPerl6ModuleName() != null;
        }
        e.getPresentation().setEnabledAndVisible(available);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile currentFile = e.getData(CommonDataKeys.PSI_FILE);
        if (currentFile instanceof RakuFile) {
            String moduleName = ((RakuFile)currentFile).getEnclosingPerl6ModuleName();
            startRepl(e, moduleName);
        }
    }
}
