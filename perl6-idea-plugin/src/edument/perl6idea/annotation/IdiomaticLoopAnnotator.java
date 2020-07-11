package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import edument.perl6idea.annotation.fix.IdiomaticLoopFix;
import edument.perl6idea.psi.Perl6IntLiteral;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.Perl6WhileStatement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class IdiomaticLoopAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6WhileStatement))
            return;
        PsiElement keyword = element.getFirstChild();
        PsiElement condition = keyword.getNextSibling();
        while (condition != null && (condition instanceof PsiWhiteSpace ||
               condition.getNode().getElementType() == UNV_WHITE_SPACE)) {
            condition = condition.getNextSibling();
        }
        boolean suits = false;
        if (condition instanceof Perl6IntLiteral) {
            suits = condition.getText().equals("1");
        } else if (condition instanceof Perl6TypeName) {
            suits = Objects.equals("True", ((Perl6TypeName)condition).getTypeName());
        }
        if (suits) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Idiomatic 'loop' construction can be used instead")
                .range(keyword).withFix(new IdiomaticLoopFix((Perl6WhileStatement)element)).create();
        }
    }
}
