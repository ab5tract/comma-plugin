package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.raku.comma.annotation.fix.IdiomaticLoopFix;
import org.raku.comma.psi.RakuIntLiteral;
import org.raku.comma.psi.RakuTypeName;
import org.raku.comma.psi.RakuWhileStatement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.raku.comma.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public class IdiomaticLoopAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuWhileStatement))
            return;
        PsiElement keyword = element.getFirstChild();
        PsiElement condition = keyword.getNextSibling();
        while (condition != null && (condition instanceof PsiWhiteSpace ||
               condition.getNode().getElementType() == UNV_WHITE_SPACE)) {
            condition = condition.getNextSibling();
        }
        boolean suits = false;
        if (condition instanceof RakuIntLiteral) {
            suits = condition.getText().equals("1");
        } else if (condition instanceof RakuTypeName) {
            suits = Objects.equals("True", ((RakuTypeName)condition).getTypeName());
        }
        if (suits) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Idiomatic 'loop' construction can be used instead")
                .range(keyword).withFix(new IdiomaticLoopFix((RakuWhileStatement)element)).create();
        }
    }
}
