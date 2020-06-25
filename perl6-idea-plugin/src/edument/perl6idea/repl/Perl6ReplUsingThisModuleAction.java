package edument.perl6idea.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;

public class Perl6ReplUsingThisModuleAction extends Perl6LaunchReplAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean available = false;
        if (getSdkHome(e) != null) {
            PsiFile currentFile = e.getData(CommonDataKeys.PSI_FILE);
            available = currentFile instanceof Perl6File &&
                    ((Perl6File)currentFile).getEnclosingPerl6ModuleName() != null;
        }
        e.getPresentation().setEnabledAndVisible(available);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile currentFile = e.getData(CommonDataKeys.PSI_FILE);
        if (currentFile instanceof Perl6File) {
            String moduleName = ((Perl6File)currentFile).getEnclosingPerl6ModuleName();
            startRepl(e, moduleName);
        }
    }
}
