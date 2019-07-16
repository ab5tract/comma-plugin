package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static edument.perl6idea.parsing.Perl6TokenTypes.SCOPE_DECLARATOR;

public class MakeAttributeRequiredIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6ScopedDecl scopedDecl = PsiTreeUtil.getParentOfType(element, Perl6ScopedDecl.class);
        Perl6VariableDecl variableDecl = PsiTreeUtil.getChildOfType(scopedDecl, Perl6VariableDecl.class);
        if (scopedDecl == null || variableDecl == null)
            return;

        PsiDocumentManager instance = PsiDocumentManager.getInstance(project);

        Perl6Trait newTrait = Perl6ElementFactory.createTrait(project, "is", "required");
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
        if (element.getNode().getElementType() != SCOPE_DECLARATOR)
            return false;

        Perl6ScopedDecl decl = PsiTreeUtil.getParentOfType(element, Perl6ScopedDecl.class);
        if (decl == null || !Objects.equals(decl.getScope(), "has"))
            return false;

        Perl6VariableDecl variableDecl = PsiTreeUtil.getChildOfType(decl, Perl6VariableDecl.class);
        if (variableDecl == null)
            return false;

        List<Perl6Trait> traits = variableDecl.getTraits();

        for (Perl6Trait trait : traits) {
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
