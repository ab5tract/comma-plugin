package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Statement;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_MOD_COND;
import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_MOD_LOOP;

public class ConvertToBlockFormIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        // Get an outer statement
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        assert statement != null;
        // for `say 42 if True` we need to cut `say 42`, so start of statement minus start of modificator
        int endIndex = element.getTextOffset() - statement.getTextOffset();
        // Create a replacer - all modificators are easy to fit, and the expression we cut before fits well with a semicolon added
        PsiElement replacer = Perl6ElementFactory.createStatementFromText(
            project, String.format("%s {\n%s;\n}", element.getParent().getText(),
                                   statement.getText().substring(0, endIndex)));
        // Reformat the resulting node
        replacer = CodeStyleManager.getInstance(project).reformat(replacer);
        statement.replace(replacer);
        // Move caret to end of the inner statement we re-wrote into
        PsiElement innerLine = PsiTreeUtil.findChildOfType(replacer, Perl6Statement.class);
        assert innerLine != null;
        editor.getCaretModel().moveToOffset(innerLine.getTextOffset() + innerLine.getTextLength());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return element.getNode().getElementType() == STATEMENT_MOD_COND || element.getNode().getElementType() == STATEMENT_MOD_LOOP;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert to block";
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }
}
