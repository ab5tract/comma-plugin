package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class PairSimplificationFix implements IntentionAction {
    private final PsiElement pair;
    private final Perl6Variable variable;

    public PairSimplificationFix(PsiElement pair, Perl6Variable variable) {
        this.pair = pair;
        this.variable = variable;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Convert to :" + variable.getText();
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert to simpler pair literal";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6ColonPair newPair = Perl6ElementFactory.createColonPair(project, variable.getText());
        pair.replace(newPair);
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
