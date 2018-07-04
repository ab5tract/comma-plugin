package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredPrivateMethod implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6MethodCall))
            return;
        final Perl6MethodCall call = (Perl6MethodCall)element;
        String methodName = call.getCallName();
        // Annotate only private methods for now
        if (!methodName.startsWith("!")) return;

        PsiReference reference = call.getReference();
        if (reference == null) return;
        Perl6Symbol symbol = call.resolveSymbol(Perl6SymbolKind.Routine, call.getCallName());
        if (symbol != null) return;
        PsiElement prev = call.getPrevSibling();
        if (prev instanceof Perl6RoutineDecl) {
            holder.createErrorAnnotation(
                    element,
                    "Subroutine cannot start with '!'");
        } else {
            holder.createErrorAnnotation(
                    element,
                    String.format("Private method %s is used, but not declared", methodName));
        }
    }
}
