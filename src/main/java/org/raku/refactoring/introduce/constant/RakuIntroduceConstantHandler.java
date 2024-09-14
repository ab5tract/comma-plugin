package org.raku.refactoring.introduce.constant;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuElementFactory;
import org.raku.refactoring.introduce.IntroduceHandler;
import org.raku.refactoring.introduce.IntroduceOperation;
import org.raku.refactoring.introduce.IntroduceValidator;

public class RakuIntroduceConstantHandler extends IntroduceHandler {
    public RakuIntroduceConstantHandler(IntroduceValidator validator, String dialogTitle) {
        super(validator, dialogTitle);
    }

    @Override
    protected String getHelpId() {
        return  "refactoring.rakuidea.introduce.constant";
    }

    @Override
    protected String getRefactoringId() {
        return "refactoring.introduceConstant";
    }

    @Override
    protected PsiElement createDeclaration(IntroduceOperation operation) {
        Project project = operation.getProject();
        PsiElement initializer = operation.getInitializer();
        return RakuElementFactory.createConstantAssignment(project, operation.getName(), initializer.getText());
    }
}
