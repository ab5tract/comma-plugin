package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.ReplaceHyphenWithRange;
import edument.perl6idea.psi.Perl6RegexCClassElem;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.REGEX_CCLASS_SYNTAX;

public class HyphenInCharacterClassAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6RegexCClassElem) {
            PsiElement child = element.getFirstChild();
            while (child != null) {
                if (child.getNode().getElementType() != REGEX_CCLASS_SYNTAX && child.getText().equals("-"))
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "A hyphen is used in a character class, maybe '..' was intended to denote a range? Otherwise a hyphen should be at the end of the character class.")
                        .range(child)
                        .withFix(new ReplaceHyphenWithRange(child))
                        .create();
                child = child.getNextSibling();
                PsiElement sibling = child == null ? null : child.getNextSibling();
                if (sibling != null && sibling.getText().equals("]"))
                    return;
            }
        }
    }
}