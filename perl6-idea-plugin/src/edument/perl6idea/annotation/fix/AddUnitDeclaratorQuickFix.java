package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.Nls;import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
