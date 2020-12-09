package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6Trait;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MakeSubroutineExportedFix implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Make exported";
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
        PsiElement caretEl = file.findElementAt(editor.getCaretModel().getOffset());
        if (caretEl == null)
            return;
        Perl6RoutineDecl decl = PsiTreeUtil.getParentOfType(caretEl, Perl6RoutineDecl.class);
        if (decl == null)
            return;
        Perl6Trait trait = Perl6ElementFactory.createTrait(project, "is", "export");
        decl.addBefore(trait, decl.getLastChild());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
