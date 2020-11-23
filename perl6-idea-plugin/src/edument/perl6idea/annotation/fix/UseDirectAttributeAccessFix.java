package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6PostfixApplication;
import org.jetbrains.annotations.NotNull;

public class UseDirectAttributeAccessFix implements IntentionAction {
    private final Perl6PostfixApplication replacee;
    private final String attributeName;

    public UseDirectAttributeAccessFix(Perl6PostfixApplication replacee, String attributeName) {
        this.replacee = replacee;
        this.attributeName = attributeName;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Replace with direct access to " + attributeName;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Use non-virtual attribute access";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException { ;
        replacee.replace(Perl6ElementFactory.createVariable(project, attributeName));
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
