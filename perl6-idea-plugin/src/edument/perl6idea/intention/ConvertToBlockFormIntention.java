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
import edument.perl6idea.psi.Perl6StatementModCond;
import edument.perl6idea.psi.Perl6StatementModLoop;
import org.jetbrains.annotations.NotNull;

public class ConvertToBlockFormIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        // Get an outer statement
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        PsiElement modificator = PsiTreeUtil.getChildOfAnyType(statement, Perl6StatementModLoop.class, Perl6StatementModCond.class);
        assert statement != null && modificator != null;
        // for `say 42 if True` we need to cut `say 42`, so start of statement minus start of modificator
        int endIndex = modificator.getTextOffset() - statement.getTextOffset();
        // Create a replacer - all modificators are easy to fit, and the expression we cut before fits well with a semicolon added
        PsiElement replacer = Perl6ElementFactory.createStatementFromText(
            project, String.format("%s {\n%s;\n}", modificator.getText(),
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
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        return statement != null && PsiTreeUtil.getChildrenOfAnyType(statement, Perl6StatementModCond.class, Perl6StatementModLoop.class).size() == 1;
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
