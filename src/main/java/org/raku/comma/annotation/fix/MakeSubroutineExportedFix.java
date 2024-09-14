package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.RakuTrait;
import org.jetbrains.annotations.NotNull;


public class MakeSubroutineExportedFix implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Add export trait";
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
        RakuRoutineDecl decl = PsiTreeUtil.getParentOfType(caretEl, RakuRoutineDecl.class);
        if (decl == null)
            return;
        RakuTrait trait = RakuElementFactory.createTrait(project, "is", "export");
        decl.addBefore(trait, decl.getLastChild());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
