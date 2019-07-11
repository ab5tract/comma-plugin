package edument.perl6idea.annotation;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.ConstKeywordFix;
import edument.perl6idea.psi.Perl6InfixApplication;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredRoutineAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6SubCallName))
            return;
        final Perl6SubCallName call = (Perl6SubCallName)element;
        String subName = call.getCallName();
        if (subName == null) return;

        Perl6Symbol resolved = call.resolveLexicalSymbol(Perl6SymbolKind.Routine, subName);
        if (resolved == null) {
            Annotation annotation = holder.createErrorAnnotation(
              element,
              String.format("Subroutine %s is not declared", subName));
            if (subName.equals("const") &&
                (PsiTreeUtil.skipWhitespacesForward(call) instanceof Perl6Variable ||
                 PsiTreeUtil.skipWhitespacesForward(call) instanceof Perl6InfixApplication)) {
                annotation.registerFix(new ConstKeywordFix(call));
            }
        }
    }
}
