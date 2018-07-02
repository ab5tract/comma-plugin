package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredAttribute implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6Variable))
            return;
        final Perl6Variable variable = (Perl6Variable)element;
        String variableName = variable.getVariableName();
        if (variable.getTwigil() != '!' && variable.getTwigil() != '.') return;

        PsiReference reference = variable.getReference();
        if (reference == null) return;
        Perl6Symbol symbol = variable.resolveSymbol(Perl6SymbolKind.Variable, variable.getVariableName());
        if (symbol == null)
            holder.createErrorAnnotation(
                    element,
                    String.format("Attribute %s is used, but not declared", variableName));
    }
}
