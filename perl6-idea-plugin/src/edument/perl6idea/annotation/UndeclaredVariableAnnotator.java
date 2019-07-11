package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredVariableAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Make sure we've got a sensible looking variable to check.
        if (!(element instanceof Perl6Variable))
            return;
        final Perl6Variable ref = (Perl6Variable) element;
        String variableName = ref.getVariableName();
        if (variableName == null)
            return;

        // Check for $=finish section
        if (Perl6Variable.getTwigil(variableName) == '=' && variableName.equals("$=finish")) {
            PsiElement list = PsiTreeUtil.getChildOfType(
              PsiTreeUtil.getParentOfType(element, PsiFile.class),
              Perl6StatementList.class);
            if (list == null) return;
            PsiElement maybeFinish = PsiTreeUtil.findChildOfType(list, PodBlockFinish.class);
            if (maybeFinish == null)
                holder.createErrorAnnotation(
                  element,
                  "There is no =finish section in this file");
        }

        // We only check usual variables in this annotator
        // attributes are handled by another one
        if (Perl6Variable.getTwigil(variableName) != ' ')
            return;

        // Ignore anonymous variables
        // It also skips cases of contextualizer declarations
        if (variableName.equals("$") || variableName.equals("@") ||
            variableName.equals("%") || variableName.equals("&"))
            return;

        // Make sure it's not a long or late-bound name.
        if (variableName.contains("::") || variableName.contains(":["))
            return;

        // Otherwise, try to resolve it.
        Perl6Symbol resolved = ref.resolveLexicalSymbol(Perl6SymbolKind.Variable, variableName);
        if (resolved == null) {
            // Straight resolution failure
            holder.createErrorAnnotation(element,
                String.format("Variable %s is not declared", variableName));
        }
        else {
            // May not be available yet
            if (Perl6Variable.getSigil(variableName) != '&') {
                PsiElement psi = resolved.getPsi();
                if (psi != null && psi.getContainingFile() == ref.getContainingFile() &&
                        psi.getTextOffset() > ref.getTextOffset())
                    holder.createErrorAnnotation(element,
                        String.format("Variable %s is not declared in this scope yet", variableName));
            }
        }
    }
}
