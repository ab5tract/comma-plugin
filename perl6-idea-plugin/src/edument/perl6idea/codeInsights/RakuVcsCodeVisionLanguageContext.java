package edument.perl6idea.codeInsights;

import com.intellij.codeInsight.hints.VcsCodeVisionLanguageContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

public class RakuVcsCodeVisionLanguageContext implements VcsCodeVisionLanguageContext {
    @Override
    public boolean isAccepted(@NotNull PsiElement element) {
        return element instanceof Perl6RoutineDecl || element instanceof Perl6PackageDecl;
    }

    @Override
    public void handleClick(@NotNull MouseEvent mouseEvent, @NotNull Editor editor, @NotNull PsiElement element) {

    }
}
