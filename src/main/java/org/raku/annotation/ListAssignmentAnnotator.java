package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.annotation.fix.UseBindingToDestructureFix;
import org.raku.psi.RakuInfix;
import org.raku.psi.RakuVariable;
import org.raku.psi.RakuVariableDecl;
import org.jetbrains.annotations.NotNull;

public class ListAssignmentAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuVariableDecl decl))
            return;

        if (!decl.hasInitializer())
            return;

        RakuInfix infix = PsiTreeUtil.findChildOfType(decl, RakuInfix.class);
        if (infix == null || !infix.getOperator().getText().equals("="))
            return;

        RakuVariable[] variables = decl.getVariables();
        if (variables.length < 2)
            return;

        for (int i = 0, length = variables.length; i < length; i++) {
            String name = variables[i].getVariableName();
            if (i != length - 1 && name != null && (name.startsWith("@") || name.startsWith("%")))
                holder.newAnnotation(HighlightSeverity.WARNING, String.format("%s slurps everything from assignment", name.startsWith("@") ? "Array" : "Hash"))
                    .range(variables[i]).withFix(new UseBindingToDestructureFix(infix)).create();
        }
    }
}