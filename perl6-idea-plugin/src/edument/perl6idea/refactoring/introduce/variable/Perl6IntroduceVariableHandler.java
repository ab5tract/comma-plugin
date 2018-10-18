package edument.perl6idea.refactoring.introduce.variable;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6ElementFactory;
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
        return Perl6ElementFactory.createVariableAssignment(project, operation.getName(), initializer.getText());
    }

    @Override
    protected String getRefactoringId() {
        return "refactoring.perl6.introduce.variable";
    }
}
