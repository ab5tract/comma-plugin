package edument.perl6idea.refactoring.introduce.constant;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.refactoring.introduce.IntroduceHandler;
import edument.perl6idea.refactoring.introduce.IntroduceOperation;
import edument.perl6idea.refactoring.introduce.IntroduceValidator;

public class Perl6IntroduceConstantHandler extends IntroduceHandler {
    public Perl6IntroduceConstantHandler(IntroduceValidator validator, String dialogTitle) {
        super(validator, dialogTitle);
    }

    @Override
    protected String getHelpId() {
        return  "refactoring.perl6.introduce.constant";
    }

    @Override
    protected String getRefactoringId() {
        return "refactoring.introduceConstant";
    }

    @Override
    protected PsiElement createDeclaration(IntroduceOperation operation) {
        Project project = operation.getProject();
        PsiElement initializer = operation.getInitializer();
        return Perl6ElementFactory.createConstantAssignment(project, operation.getName(), initializer.getText());
    }
}
