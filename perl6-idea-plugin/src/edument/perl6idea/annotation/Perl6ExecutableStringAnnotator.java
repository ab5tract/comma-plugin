package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.UseExecutableDynamicVariableFix;
import edument.perl6idea.psi.Perl6StrLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6ExecutableStringAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6StrLiteral))
            return;

        if (((Perl6StrLiteral)element).getStringText().equals("perl6"))
            holder
                .createWarningAnnotation(element, "If Perl 6 executable is meant, consider using $*EXECUTABLE.absolute() call that supports many platforms (e.g. GNU/Linux, Windows etc)")
                .registerFix(new UseExecutableDynamicVariableFix(element));
    }
}
