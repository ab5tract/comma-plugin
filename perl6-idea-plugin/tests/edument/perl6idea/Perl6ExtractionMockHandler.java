package edument.perl6idea;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import edument.perl6idea.refactoring.introduce.IntroduceOperation;
import edument.perl6idea.refactoring.introduce.IntroduceValidator;
import edument.perl6idea.refactoring.introduce.variable.Perl6IntroduceVariableHandler;

public class Perl6ExtractionMockHandler extends Perl6IntroduceVariableHandler {
    private final String myName;

    public Perl6ExtractionMockHandler(IntroduceValidator validator, String dialogTitle, String name) {
        super(validator, dialogTitle);
        myName = name;
    }

    @Override
    protected void performActionOnElementOccurrences(IntroduceOperation operation) {
        operation.setName(myName);
        operation.setReplaceAll(true);
        PsiElement declaration = performRefactoring(operation);
        Editor editor = operation.getEditor();
        editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
        editor.getSelectionModel().removeSelection();
    }
}
