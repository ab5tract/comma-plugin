package org.raku.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuPackageDecl;
import org.raku.psi.RakuRoutineDecl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MakeMethodPublicIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuRoutineDecl decl = PsiTreeUtil.getParentOfType(element, RakuRoutineDecl.class);
        if (decl == null) {
            return;
        }

        String publicRoutineName = decl.getRoutineName().substring(1);

        // Update all calls
        Collection<PsiReference> refs = ReferencesSearch.search(decl, GlobalSearchScope.fileScope(element.getContainingFile())).findAll();
        for (PsiReference ref : refs) {
            ref.handleElementRename(publicRoutineName);
        }
        // Update the declaration
        decl.setName(publicRoutineName);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element.getNode().getElementType() != org.raku.parsing.RakuTokenTypes.ROUTINE_DECLARATOR &&
            element.getNode().getElementType() != RakuTokenTypes.ROUTINE_NAME) {
            return false;
        }

        RakuRoutineDecl decl = PsiTreeUtil.getParentOfType(element, RakuRoutineDecl.class);
        RakuPackageDecl owner = PsiTreeUtil.getParentOfType(decl, RakuPackageDecl.class);
        return owner != null &&
               (decl.getRoutineKind().equals("method") || decl.getRoutineKind().equals("multi")) &&
               !decl.getRoutineName().equals("<anon>") &&
               decl.isPrivate();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Make method public";
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }
}
