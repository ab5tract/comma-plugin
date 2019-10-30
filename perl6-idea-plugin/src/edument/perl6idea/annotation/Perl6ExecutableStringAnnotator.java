package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.hash.HashSet;
import edument.perl6idea.annotation.fix.UseExecutableDynamicVariableFix;
import edument.perl6idea.psi.Perl6StrLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Perl6ExecutableStringAnnotator implements Annotator {
    private static final Set<String> aliases = new HashSet<>();

    static {
        aliases.add("raku");
        aliases.add("perl6");
        aliases.add("rakudo");
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6StrLiteral))
            return;

        String text = ((Perl6StrLiteral)element).getStringText();
        if (aliases.contains(text))
            holder
                .createWarningAnnotation(element, "If the Perl 6 executable is meant, consider using the $*EXECUTABLE.absolute() call that supports many platforms (e.g. GNU/Linux, Windows, etc.)")
                .registerFix(new UseExecutableDynamicVariableFix(element));
    }
}
