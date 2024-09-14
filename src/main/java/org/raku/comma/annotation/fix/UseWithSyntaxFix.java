package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuConditionalBranch;
import org.raku.comma.psi.RakuPostfixApplication;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UseWithSyntaxFix implements IntentionAction {
    private final RakuConditionalBranch conditional;

    public UseWithSyntaxFix(RakuConditionalBranch condBranch) {
        this.conditional = condBranch;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        String keyword = conditional.term.getText().equals("unless") ? "without" : "with";
        return String.format("Use '%s' syntax construction", keyword);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Use with/without construction";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        // Cannot happen with correct annotator beign a caller
        if (!(conditional.condition instanceof RakuPostfixApplication inner))
            return;

        // Firstly, delete the `.defined` call
        inner.getLastChild().delete();

        // Replace term
        PsiElement term = conditional.term;
        editor.getDocument().replaceString(
                term.getTextOffset(),
                term.getTextOffset() + term.getTextLength(),
                getReplacer(term.getText()));
    }

    private static String getReplacer(@NotNull String text) {
        switch(text) {
            case "if": return "with";
            case "elsif": return "orwith";
            case "unless":
            default: return "without";
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
