package edument.perl6idea.refactoring.introduce.variable;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.introduce.IntroduceHandler;
import edument.perl6idea.refactoring.introduce.IntroduceOperation;
import edument.perl6idea.refactoring.introduce.IntroduceValidator;

public class Perl6IntroduceVariableHandler extends IntroduceHandler {
    public Perl6IntroduceVariableHandler(IntroduceValidator validator, String dialogTitle) {
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
        return Perl6ElementFactory.createVariableAssignment(project, operation.getName(),
                                                            initializer.getText(), control);
    }

    private static boolean isInitializerControlStatement(PsiElement initializer) {
        // We might already have Statement or it's child, so normalize our node first
        PsiElement statement = PsiTreeUtil.getParentOfType(initializer, Perl6Statement.class, false);
        assert statement != null;
        PsiElement child = statement.getFirstChild();
        assert child != null;
        return child instanceof Perl6IfStatement ||
               child instanceof Perl6UnlessStatement ||
               child instanceof Perl6WithoutStatement ||
               child instanceof Perl6WheneverStatement ||
               child instanceof Perl6ForStatement ||
               child instanceof Perl6GivenStatement ||
               child instanceof Perl6LoopStatement ||
               child instanceof Perl6WhileStatement ||
               child instanceof Perl6UntilStatement ||
               child instanceof Perl6RepeatStatement;
    }

    @Override
    protected String getRefactoringId() {
        return "refactoring.perl6.introduce.variable";
    }
}
