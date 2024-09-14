package edument.rakuidea;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import edument.rakuidea.refactoring.introduce.IntroduceOperation;
import edument.rakuidea.refactoring.introduce.IntroduceValidator;
import edument.rakuidea.refactoring.introduce.constant.RakuIntroduceConstantHandler;

import java.util.List;

public class RakuConstantExtractionHandlerMock extends RakuIntroduceConstantHandler {
    private final String myName;

    public RakuConstantExtractionHandlerMock(IntroduceValidator validator, String name) {
        super(validator, "Extract mock");
        myName = name;
    }

    @Override
    protected void performActionOnElementOccurrences(IntroduceOperation operation) {
        operation.setName(myName);
        operation.setReplaceAll(true);
        List<PsiElement> anchors = calculateAnchors(operation, operation.getElement(), operation.getOccurrences());
        operation.setAnchor(anchors.get(0));
        PsiElement declaration = performRefactoring(operation);
        removeLeftoverStatement(operation);
        Editor editor = operation.getEditor();
        editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
        editor.getSelectionModel().removeSelection();
    }
}
