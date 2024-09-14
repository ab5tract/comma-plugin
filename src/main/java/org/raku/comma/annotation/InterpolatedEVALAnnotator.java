package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.annotation.fix.AddEvalPragmaFix;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

public class InterpolatedEVALAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuSubCall)) return;
        if (!((RakuSubCall)element).getCallName().equals("EVAL")) return;

        PsiElement[] args = ((RakuSubCall)element).getCallArguments();
        if (args == null) return;

        RakuPsiScope scope = PsiTreeUtil.getParentOfType(element, RakuPsiScope.class);
        while (scope != null) {
            RakuStatementList list = PsiTreeUtil.findChildOfType(scope, RakuStatementList.class);
            if (list == null) break;
            RakuStatement[] stats = PsiTreeUtil.getChildrenOfType(list, RakuStatement.class);
            if (stats == null) stats = new RakuStatement[0];
            for (RakuStatement statement : stats) {
                if (statement.getTextOffset() > element.getTextOffset()) break;
                for (PsiElement child : statement.getChildren()) {
                    if (!(child instanceof RakuUseStatement)) continue;
                    String moduleName = ((RakuUseStatement)child).getModuleName();
                    if (moduleName == null || moduleName.equals("MONKEY") ||
                        moduleName.equals("MONKEY-SEE-NO-EVAL"))
                        return;
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, RakuPsiScope.class);
        }

        for (PsiElement arg : args) {
            if (arg instanceof RakuStrLiteral) {
                String t = arg.getText(); // Literal text
                if (t.startsWith("Q")) return;
                if (t.startsWith("q") && !t.startsWith("qq")) return;
                // Check is variable used
                if (PsiTreeUtil.findChildOfType(arg, RakuVariable.class) == null) return;
            }
        }

        holder.newAnnotation(HighlightSeverity.ERROR, "Cannot EVAL interpolated expression without MONKEY-SEE-NO-EVAL pragma")
            .range(element).withFix(new AddEvalPragmaFix()).create();
    }
}
