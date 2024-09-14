package org.raku.comma.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuStrLiteral;
import org.jetbrains.annotations.NotNull;

public class SingleQuotesToDoubleIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuStrLiteral literal = PsiTreeUtil.getParentOfType(element, RakuStrLiteral.class);
        if (literal != null)
            literal.replace(RakuElementFactory.createStrLiteral(project, String.format("\"%s\"", literal.getStringText())));
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        RakuStrLiteral literal = PsiTreeUtil.getParentOfType(element, RakuStrLiteral.class);
        return literal != null && literal.getText().startsWith("'");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert to double quotes";
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }
}
