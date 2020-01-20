package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddEvalPragmaFix implements IntentionAction {
    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add MONKEY-SEE-NO-EVAL pragma";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        editor.getDocument().insertString(0, "use MONKEY-SEE-NO-EVAL;\n");
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
