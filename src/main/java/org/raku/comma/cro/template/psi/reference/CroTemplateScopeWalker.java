package org.raku.comma.cro.template.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.cro.template.psi.Scope;

public class CroTemplateScopeWalker {
    public static void walkWithCollector(CroTemplateSymbolCollector collector, PsiElement from) {
        Scope currentScope = PsiTreeUtil.getParentOfType(from, Scope.class);
        while (currentScope != null) {
            currentScope.offerAllTo(collector);
            if (collector.isSatisfied())
                return;
            currentScope = PsiTreeUtil.getParentOfType(currentScope, Scope.class);
        }
    }
}
