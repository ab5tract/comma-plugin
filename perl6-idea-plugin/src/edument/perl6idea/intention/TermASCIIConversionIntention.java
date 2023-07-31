package edument.perl6idea.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class TermASCIIConversionIntention extends TermConversionIntention {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Convert term to ASCII form";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        Operation operation = classifyOperation(project, editor, file);
        return operation == Operation.TO_ASCII_PI || operation == Operation.TO_ASCII_TAU ||
               operation == Operation.TO_ASCII_SET;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementUnderCaret = file.findElementAt(offset);
        assert elementUnderCaret != null;
        String replacement;
        switch (classifyOperation(project, editor, file)) {
            case TO_ASCII_SET: replacement = "set()"; break;
            case TO_ASCII_PI: replacement = "pi"; break;
            case TO_ASCII_TAU: replacement = "tau"; break;
            default: throw new AssertionError();
        }
        TextRange elementRange = elementUnderCaret.getTextRange();
        editor.getDocument().replaceString(elementRange.getStartOffset(), elementRange.getEndOffset(), replacement);
    }
}
