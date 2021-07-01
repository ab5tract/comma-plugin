package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6RegexModInternal;
import org.jetbrains.annotations.NotNull;

public class UnknownRegexModAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6RegexModInternal) {
            if (element.getNode().findChildByType(Perl6TokenTypes.REGEX_MOD_UNKNOWN) != null) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unrecognized regex modifier")
                    .create();
            }
        }
    }
}
