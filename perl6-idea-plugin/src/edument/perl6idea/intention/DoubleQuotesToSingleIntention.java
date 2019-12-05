package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6StrLiteral;
import org.jetbrains.annotations.NotNull;

public class DoubleQuotesToSingleIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6StrLiteral literal = PsiTreeUtil.getParentOfType(element, Perl6StrLiteral.class);
        if (literal != null)
            literal.replace(Perl6ElementFactory.createStrLiteral(project, String.format("'%s'", literal.getStringText())));
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        Perl6StrLiteral literal = PsiTreeUtil.getParentOfType(element, Perl6StrLiteral.class);
        return literal != null && literal.getText().startsWith("\"");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert to single quotes";
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }
}
