package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.PodPreComment;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

public class RemoveUnusedRoutineFix implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Safe delete routine";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement el = file.findElementAt(editor.getCaretModel().getOffset());
        if (el == null)
            return;
        PsiElement decl = PsiTreeUtil.getParentOfType(el, Perl6RoutineDecl.class);
        if (decl == null)
            return;
        PsiElement preComment = Perl6PsiUtil.skipSpaces(decl.getParent().getPrevSibling(), false, false);
        if (preComment instanceof PodPreComment)
            preComment.delete();
        PsiElement maybeWS = decl.getParent().getNextSibling();
        if (maybeWS instanceof PsiWhiteSpace)
            maybeWS.delete();
        decl.getParent().delete();
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
