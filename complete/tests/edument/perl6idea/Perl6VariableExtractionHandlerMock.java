package edument.perl6idea;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import edument.perl6idea.refactoring.introduce.IntroduceOperation;
import edument.perl6idea.refactoring.introduce.IntroduceValidator;
import edument.perl6idea.refactoring.introduce.variable.Perl6IntroduceVariableHandler;

public class Perl6VariableExtractionHandlerMock extends Perl6IntroduceVariableHandler {
    private final String myName;
    public boolean replaceAll = true;

    public Perl6VariableExtractionHandlerMock(IntroduceValidator validator, String name) {
        super(validator, "Extract Mock");
        myName = name;
    }

    @Override
    protected void performActionOnElementOccurrences(IntroduceOperation operation) {
        operation.setName(myName);
        operation.setReplaceAll(replaceAll);
        PsiElement declaration = performRefactoring(operation);
        removeLeftoverStatement(operation);
        Editor editor = operation.getEditor();
        editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
        editor.getSelectionModel().removeSelection();
    }
}
