package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import edument.perl6idea.psi.symbols.Perl6SingleResolutionSymbolCollector;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredAttribute implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Filter out anything except attribute usages.
        if (!(element instanceof Perl6Variable))
            return;
        if (element.getParent() instanceof Perl6VariableDecl)
            return;
        final Perl6Variable variable = (Perl6Variable)element;
        String variableName = variable.getVariableName();
        if (variableName == null || variableName.length() <= 2 || Perl6Variable.getTwigil(variable.getText()) != '!')
            return;

        PsiReference reference = variable.getReference();
        if (reference == null)
            return;
        Perl6PackageDecl enclosingPackage = ((Perl6Variable)element).getSelfType();
        if (enclosingPackage == null) {
            holder.createErrorAnnotation(
                    element,
                    String.format("Attribute %s is used where no self is in scope", variableName));
            return;
        }
        Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(variableName, Perl6SymbolKind.Variable);
        enclosingPackage.contributeMOPSymbols(collector, true, true);
        if (collector.getResult() == null)
            holder.createErrorAnnotation(
                    element,
                    String.format("Attribute %s is used, but not declared", variableName));
    }
}
