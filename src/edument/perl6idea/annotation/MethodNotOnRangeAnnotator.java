package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Prefix;
import edument.perl6idea.psi.Perl6PrefixApplication;
import org.jetbrains.annotations.NotNull;

/* Detects ^5.foo, which is probably a mistake. */
public class MethodNotOnRangeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Perl6PrefixApplication) {
            Perl6PrefixApplication prefixApp = (Perl6PrefixApplication)psiElement;
            PsiElement maybePrefix = prefixApp.getFirstChild();
            if (!(maybePrefix instanceof Perl6Prefix))
                return;
            if (!maybePrefix.textMatches("^"))
                return;
            String rest = psiElement.getText().substring(1);
            if (rest.matches("^\\d+\\..+"))
                annotationHolder.createWarningAnnotation(psiElement,
                        "Precedence of ^ is looser than method call; please parenthesize");
        }
    }
}
