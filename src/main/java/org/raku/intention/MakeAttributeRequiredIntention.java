package org.raku.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.*;
import org.raku.psi.RakuScopedDecl;
import org.raku.psi.RakuTrait;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MakeAttributeRequiredIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuScopedDecl scopedDecl = PsiTreeUtil.getParentOfType(element, RakuScopedDecl.class);
        RakuVariableDecl variableDecl = PsiTreeUtil.getChildOfType(scopedDecl, RakuVariableDecl.class);
        if (scopedDecl == null || variableDecl == null)
            return;

        PsiDocumentManager instance = PsiDocumentManager.getInstance(project);

        RakuTrait newTrait = RakuElementFactory.createTrait(project, "is", "required");
        int offset = scopedDecl.getTextOffset() + scopedDecl.getTextLength();

        // We need to add a single space to avoid
        editor.getDocument().insertString(offset, " ");
        instance.commitDocument(editor.getDocument());

        variableDecl.add(newTrait);
        instance.doPostponedOperationsAndUnblockDocument(editor.getDocument());

        // Re-format to apply possible user spacing rules
        CodeStyleManager.getInstance(project).reformat(scopedDecl);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        RakuScopedDecl decl = PsiTreeUtil.getParentOfType(element, RakuScopedDecl.class);
        if (decl == null || !Objects.equals(decl.getScope(), "has"))
            return false;

        RakuVariableDecl variableDecl = PsiTreeUtil.getChildOfType(decl, RakuVariableDecl.class);
        if (variableDecl == null)
            return false;

        PsiElement init = variableDecl.getInitializer();
        if (init != null)
            return false;

        List<RakuTrait> traits = variableDecl.getTraits();

        for (RakuTrait trait : traits) {
            if (trait.getTraitModifier().equals("is") && trait.getTraitName().equals("required"))
                return false;
        }

        return true;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Make required";
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }
}
