package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChangeDoesToIsFix implements IntentionAction {
    private final int offset;

    public ChangeDoesToIsFix(int offset) {
        this.offset = offset;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Replace \"does\" with \"is\"";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Perl 6";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        List<CaretState> carets = editor.getCaretModel().getCaretsAndSelections();
        editor.getDocument().replaceString(offset, offset + 4, "is");
        editor.getCaretModel().setCaretsAndSelections(carets);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
