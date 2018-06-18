package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6StatementList;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.PodBlockFinish;
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
        if (ref.getTwigil() == '=' && variableName.equals("$=finish")) {
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

        // We only check twigilless variables for now (can't yet do attributes
        // because they may come from a role).
        if (ref.getTwigil() != ' ')
            return;

        // Make sure it's not a long or late-bound name.
        if (ref.getTwigil() == '!' || variableName.contains("::") || variableName.contains(":["))
            return;

        // Otherwise, try to resolve it.
        Perl6Symbol resolved = ref.resolveSymbol(Perl6SymbolKind.Variable, variableName);
        if (resolved == null) {
            // Straight resolution failure.
            holder.createErrorAnnotation(element,
                String.format("Variable %s is not declared", variableName));
        }
        else {
            // May not be available yet.
            if (ref.getTwigil() == ' ' && ref.getSigil() != '&') {
                PsiElement psi = resolved.getPsi();
                if (psi != null && psi.getContainingFile() == ref.getContainingFile() &&
                        psi.getTextOffset() > ref.getTextOffset())
                    holder.createErrorAnnotation(element,
                        String.format("Variable %s is not declared in this scope yet", variableName));
            }
        }
    }
}
