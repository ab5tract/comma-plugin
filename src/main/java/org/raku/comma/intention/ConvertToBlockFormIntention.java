package org.raku.comma.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuStatementModLoop;
import org.raku.comma.psi.RakuStatement;
import org.raku.comma.psi.RakuStatementModCond;
import org.jetbrains.annotations.NotNull;

public class ConvertToBlockFormIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        // Get an outer statement
        RakuStatement statement = PsiTreeUtil.getParentOfType(element, RakuStatement.class);
        PsiElement modificator = PsiTreeUtil.getChildOfAnyType(statement, RakuStatementModLoop.class, RakuStatementModCond.class);
        assert statement != null && modificator != null;
        // for `say 42 if True` we need to cut `say 42`, so start of statement minus start of modificator
        int endIndex = modificator.getTextOffset() - statement.getTextOffset();
        // Create a replacer - all modificators are easy to fit, and the expression we cut before fits well with a semicolon added
        PsiElement replacer = RakuElementFactory.createStatementFromText(
            project, String.format("%s {\n%s;\n}", modificator.getText(),
                                   statement.getText().substring(0, endIndex)));
        replacer = statement.replace(replacer);
        // Move caret to end of the inner statement we re-wrote into
        PsiElement innerLine = PsiTreeUtil.findChildOfType(replacer, RakuStatement.class);
        assert innerLine != null;
        editor.getCaretModel().moveToOffset(innerLine.getTextOffset() + innerLine.getTextLength());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        RakuStatement statement = PsiTreeUtil.getParentOfType(element, RakuStatement.class);
        return statement != null && PsiTreeUtil.getChildrenOfAnyType(statement, RakuStatementModCond.class, RakuStatementModLoop.class).size() == 1;
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
