package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.highlighter.RakuHighlightVisitor;
import org.raku.comma.psi.*;
import org.raku.comma.psi.symbols.RakuImplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public class RakuBlockOrHashImpl extends RakuASTWrapperPsiElement implements RakuBlockOrHash {
    public RakuBlockOrHashImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getTopic() {
        return null;
    }

    @Override
    public void contributeScopeSymbols(RakuSymbolCollector collector) {
        collector.offerSymbol(new RakuImplicitSymbol(RakuSymbolKind.Variable, "$_", this));
        if (!collector.isSatisfied())
            collector.offerSymbol(new RakuImplicitSymbol(RakuSymbolKind.Variable, "&?BLOCK", this));
    }

    @Override
    public boolean isHash() {
        // If it doesn't even match the basis syntactic look of something that
        // could be a hash literal, then it's not one (empty or one statement,
        // starts with a pair or hash variable).
        if (!isHashish())
            return false;

        // Empty block is always a hash.
        RakuStatement[] statements = getElements();
        if (statements.length == 0)
            return true;

        // Otherwise, we should check for:
        // 1. Declarations
        // 2. Use of $_
        // 3. Use of placeholder parameters
        // Without walking into inner blocks.
        Deque<RakuPsiElement> toVisit = new ArrayDeque<>();
        toVisit.addLast(statements[0]);
        while (!toVisit.isEmpty()) {
            RakuPsiElement check = toVisit.removeFirst();
            if (check instanceof RakuVariableDecl)
                return false;
            if (check instanceof RakuVariable) {
                String name = ((RakuVariable)check).getVariableName();
                if ("$_".equals(name))
                    return false;
                char twigil = RakuVariable.getTwigil(name);
                if (twigil == '^' || twigil == ':')
                    return false;
            }
            if (check instanceof RakuMethodCall &&
                !(check.getParent() instanceof RakuPostfixApplication)) // .foo
                return false;
            for (PsiElement child : check.getChildren())
                if (child instanceof RakuPsiElement && !(child instanceof RakuPsiScope))
                    toVisit.add((RakuPsiElement)child);
        }

        // No problems found, so it's a hash.
        return true;
    }

    @Override
    public boolean isHashish() {
        // Empty block is certainly a hash, multiple statement is certainly not.
        RakuStatement[] statements = getElements();
        if (statements.length == 0)
            return true;
        if (statements.length != 1)
            return false;

        // Only one statement. Either it is a comma list and we should look at the
        // first element of that, or we just want to consider what it contains directly.
        RakuStatement onlyStatement = statements[0];
        RakuInfixApplication maybeComma = PsiTreeUtil.getChildOfType(onlyStatement, RakuInfixApplication.class);
        if (maybeComma != null && maybeComma.isCommaOperator()) {
            PsiElement firstOperand = maybeComma.getOperands()[0];
            return isHashStarter(firstOperand);
        }
        else {
            for (PsiElement child : onlyStatement.getChildren())
                if (isHashStarter(child))
                    return true;
            return false;
        }
    }

    private static boolean isHashStarter(PsiElement element) {
        if (element instanceof RakuColonPair || element instanceof RakuFatArrow)
            return true;
        if (element instanceof RakuVariable)
            return RakuVariable.getSigil(((RakuVariable)element).getVariableName()) == '%';
        return false;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RakuHighlightVisitor) {
            ((RakuHighlightVisitor)visitor).visitRakuElement(this);
        } else {
            super.accept(visitor);
        }
    }
}
