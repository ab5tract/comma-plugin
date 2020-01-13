package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ChangeMyScopeToOurScopeFix implements IntentionAction {
    private final int myOffset;

    public ChangeMyScopeToOurScopeFix(int variableDeclTextOffset) {
        myOffset = variableDeclTextOffset;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Change scope to `our`";
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
        editor.getDocument().replaceString(myOffset, myOffset + 2, "our");
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
