package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6ParameterVariable;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.symbols.MOPSymbolsAllowed;
import edument.perl6idea.psi.symbols.Perl6SingleResolutionSymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredAttributeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Filter out anything except attribute usages.
        if (!(element instanceof Perl6Variable))
            return;
        if (element.getParent() instanceof Perl6VariableDecl || element.getParent() instanceof Perl6ParameterVariable)
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
            holder.newAnnotation(HighlightSeverity.ERROR,
                                 String.format("Attribute %s is used where no self is in scope", variableName))
                .range(element).create();
            return;
        }
        Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(variableName, Perl6SymbolKind.Variable);
        enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                true, true, true, enclosingPackage.getPackageKind().equals("role")));
        if (collector.getResult() == null)
            holder.newAnnotation(HighlightSeverity.ERROR, String.format("Attribute %s is used, but not declared", variableName))
                .range(element).create();
    }
}
