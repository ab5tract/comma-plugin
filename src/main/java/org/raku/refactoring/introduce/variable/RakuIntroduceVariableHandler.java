package org.raku.refactoring.introduce.variable;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;
import org.raku.refactoring.introduce.IntroduceHandler;
import org.raku.refactoring.introduce.IntroduceOperation;
import org.raku.refactoring.introduce.IntroduceValidator;

public class RakuIntroduceVariableHandler extends IntroduceHandler {
    public RakuIntroduceVariableHandler(IntroduceValidator validator, String dialogTitle) {
        super(validator, dialogTitle);
    }

    @Override
    protected String getHelpId() {
        return "refactoring.introduceVariable";
    }

    @Override
    protected PsiElement createDeclaration(IntroduceOperation operation) {
        Project project = operation.getProject();
        PsiElement initializer = operation.getInitializer();
        boolean control = isInitializerControlStatement(initializer);
        return RakuElementFactory.createVariableAssignment(project, operation.getName(),
                                                           initializer.getText(), control);
    }

    private static boolean isInitializerControlStatement(PsiElement initializer) {
        // We might already have Statement or its child, so normalize our node first
        PsiElement statement = PsiTreeUtil.getParentOfType(initializer, RakuStatement.class, false);
        assert statement != null;
        PsiElement child = statement.getFirstChild();
        assert child != null;
        return child instanceof RakuIfStatement ||
               child instanceof RakuUnlessStatement ||
               child instanceof RakuWithoutStatement ||
               child instanceof RakuWheneverStatement ||
               child instanceof RakuForStatement ||
               child instanceof RakuGivenStatement ||
               child instanceof RakuWhenStatement ||
               child instanceof RakuLoopStatement ||
               child instanceof RakuWhileStatement ||
               child instanceof RakuUntilStatement ||
               child instanceof RakuRepeatStatement;
    }

    @Override
    protected String getRefactoringId() {
        return "refactoring.raku.introduce.variable";
    }
}
