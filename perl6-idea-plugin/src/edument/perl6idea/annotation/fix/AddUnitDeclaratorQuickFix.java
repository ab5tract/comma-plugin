package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.Nls;import org.jetbrains.annotations.NotNull;

public class AddUnitDeclaratorQuickFix implements IntentionAction {
    private final Perl6PackageDecl packageElement;

    public AddUnitDeclaratorQuickFix(Perl6PackageDecl ref) {
        this.packageElement = ref;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        String packageKind = packageElement.getPackageKind();
        int textOffset = packageElement.getTextOffset() - (packageKind == null ? 0 : packageKind.length() + 1);
        editor.getDocument().insertString(textOffset, "unit ");
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Perl 6";
    }

    @NotNull
    @Override
    public String getText() {
        return "Add missing 'unit' scope declaration";
    }
}
