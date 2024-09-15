package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class UndeclaredVariableAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Make sure we've got a sensible looking variable to check.
        if (!(element instanceof final RakuVariable variable))
            return;
        if (element.getParent() instanceof RakuRegexVariable)
            return;
        if (element.getParent() instanceof RakuVariableDecl)
            return;
        String variableName = variable.getVariableName();
        if (variableName == null)
            return;

        Pattern REGEX_VAR_PATTERN = Pattern.compile("\\$\\d+|\\$<[\\w\\d_-]+>");
        if (REGEX_VAR_PATTERN.matcher(variableName).matches()) {
            RakuSymbol symbol = variable.resolveLexicalSymbol(RakuSymbolKind.Variable, "$/");
            if (!symbol.isImplicitlyDeclared())
                return;
        }

        // Check for $=finish section
        if (RakuVariable.getTwigil(variableName) == '=' && variableName.equals("$=finish")) {
            PsiElement list = PsiTreeUtil.getChildOfType(
                PsiTreeUtil.getParentOfType(element, PsiFile.class),
                RakuStatementList.class);
            if (list == null) return;
            PsiElement maybeFinish = PsiTreeUtil.findChildOfType(list, PodBlockFinish.class);
            if (maybeFinish == null)
                holder.newAnnotation(HighlightSeverity.ERROR, "There is no =finish section in this file")
                    .range(element).create();
        }

        // We only check usual variables in this annotator
        // attributes are handled by another one
        if (RakuVariable.getTwigil(variableName) != ' ')
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
        RakuSymbol symbol = variable.resolveLexicalSymbol(RakuSymbolKind.Variable, variableName);
        if (symbol == null) {
            PsiReference reference = variable.getReference();
            assert reference != null;
            PsiElement resolved = reference.resolve();
            if (resolved == null) {
                // Straight resolution failure
                holder.newAnnotation(HighlightSeverity.ERROR,
                                     String.format("Variable %s is not declared", variableName))
                                           .range(element).create();
            }
        }
        else {
            if (RakuVariable.getSigil(variableName) != '&') {
                PsiElement psi = symbol.getPsi();
                if (psi != null && psi.getContainingFile() == variable.getContainingFile() && psi.getTextOffset() > variable.getTextOffset())
                    holder.newAnnotation(HighlightSeverity.ERROR,
                                         String.format("Variable %s is not declared in this scope yet", variableName))
                                               .range(element).create();
            }
        }
    }
}
