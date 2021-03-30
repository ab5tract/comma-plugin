package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public class Perl6BlockOrHashImpl extends ASTWrapperPsiElement implements Perl6BlockOrHash {
    public Perl6BlockOrHashImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getTopic() {
        return null;
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$_", this));
        if (!collector.isSatisfied())
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "&?BLOCK", this));
    }

    @Override
    public boolean isHash() {
        // If it doesn't even match the basis syntactic look of something that
        // could be a hash literal, then it's not one (empty or one statement,
        // starts with a pair or hash variable).
        if (!isHashish())
            return false;

        // Empty block is always a hash.
        Perl6Statement[] statements = getElements();
        if (statements.length == 0)
            return true;

        // Otherwise, we should check for:
        // 1. Declarations
        // 2. Use of $_
        // 3. Use of placeholder parameters
        // Without walking into inner blocks.
        Deque<Perl6PsiElement> toVisit = new ArrayDeque<>();
        toVisit.addLast(statements[0]);
        while (!toVisit.isEmpty()) {
            Perl6PsiElement check = toVisit.removeFirst();
            if (check instanceof Perl6VariableDecl)
                return false;
            if (check instanceof Perl6Variable) {
                String name = ((Perl6Variable)check).getVariableName();
                if ("$_".equals(name))
                    return false;
                char twigil = Perl6Variable.getTwigil(name);
                if (twigil == '^' || twigil == ':')
                    return false;
            }
            if (check instanceof Perl6MethodCall &&
                    !(check.getParent() instanceof Perl6PostfixApplication)) // .foo
                return false;
            for (PsiElement child : check.getChildren())
                if (child instanceof Perl6PsiElement && !(child instanceof Perl6PsiScope))
                    toVisit.add((Perl6PsiElement)child);
        }

        // No problems found, so it's a hash.
        return true;
    }

    @Override
    public boolean isHashish() {
        // Empty block is certainly a hash, multiple statement is certainly not.
        Perl6Statement[] statements = getElements();
        if (statements.length == 0)
            return true;
        if (statements.length != 1)
            return false;

        // Only one statement. Either it is a comma list and we should look at the
        // first element of that, or we just want to consider what it contains directly.
        Perl6Statement onlyStatement = statements[0];
        Perl6InfixApplication maybeComma = PsiTreeUtil.getChildOfType(onlyStatement, Perl6InfixApplication.class);
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
        if (element instanceof Perl6ColonPair || element instanceof Perl6FatArrow)
            return true;
        if (element instanceof Perl6Variable)
            return Perl6Variable.getSigil(((Perl6Variable)element).getVariableName()) == '%';
        return false;
    }
}
