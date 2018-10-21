package edument.perl6idea;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import edument.perl6idea.refactoring.introduce.IntroduceOperation;
import edument.perl6idea.refactoring.introduce.IntroduceValidator;
import edument.perl6idea.refactoring.introduce.constant.Perl6IntroduceConstantHandler;

public class Perl6ConstantExtractionHandlerMock extends Perl6IntroduceConstantHandler {
    private final String myName;

    public Perl6ConstantExtractionHandlerMock(IntroduceValidator validator, String name) {
        super(validator, "Extract mock");
        myName = name;
    }

    @Override
    protected void performActionOnElementOccurrences(IntroduceOperation operation) {
        operation.setName(myName);
        operation.setReplaceAll(true);
        PsiElement declaration = performRefactoring(operation);
        removeLeftoverStatement(operation);
        Editor editor = operation.getEditor();
        editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
        editor.getSelectionModel().removeSelection();
    }
}
