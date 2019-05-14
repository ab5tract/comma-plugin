package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import edument.perl6idea.annotation.fix.UseWithSyntaxFix;
import edument.perl6idea.psi.Perl6IfStatement;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PostfixApplication;
import edument.perl6idea.psi.Perl6UnlessStatement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class WithConstructionAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (!(psiElement instanceof Perl6IfStatement || psiElement instanceof Perl6UnlessStatement))
            return;

        PsiElement condition = psiElement.getFirstChild();
        if (condition != null)
            condition = condition.getNextSibling();

        if (!(condition instanceof Perl6PostfixApplication))
            while (condition != null && (condition instanceof PsiWhiteSpace || condition.getNode().getElementType() == UNV_WHITE_SPACE))
                condition = condition.getNextSibling();

        if (!(condition instanceof Perl6PostfixApplication))
            return;

        PsiElement methodCall = condition.getLastChild();

        if (!(methodCall instanceof Perl6MethodCall))
            return;

        if (Objects.equals(((Perl6MethodCall) methodCall).getCallName(), ".defined")) {
            annotationHolder.createWeakWarningAnnotation(new TextRange(psiElement.getTextOffset(),
                            methodCall.getTextOffset() + methodCall.getTextLength()),
                    psiElement instanceof Perl6IfStatement ? "'with' construction can be used instead" : "'without' construction can be used instead")
                    .registerFix(new UseWithSyntaxFix(psiElement));
        }
    }
}
