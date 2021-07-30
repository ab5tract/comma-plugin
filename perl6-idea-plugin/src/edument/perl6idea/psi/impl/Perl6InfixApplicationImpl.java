package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.effects.Effect;
import edument.perl6idea.psi.effects.EffectCollection;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static edument.perl6idea.parsing.Perl6ElementTypes.NULL_TERM;

public class Perl6InfixApplicationImpl extends ASTWrapperPsiElement implements Perl6InfixApplication {
    public Perl6InfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement[] getOperands() {
        List<Perl6PsiElement> infixes = PsiTreeUtil.getChildrenOfAnyType(this, Perl6Infix.class,
              Perl6AssignMetaOp.class, Perl6ZipMetaOp.class, Perl6ReverseMetaOp.class, Perl6CrossMetaOp.class);
        if (infixes.isEmpty())
            return PsiElement.EMPTY_ARRAY;
        // To get elements between infixes, we gather them all on the first level and
        // iterating over every instance, collecting a previous element,
        // thus for `1 infix' 2 infix'' 3` we collect `1` as left of `infix'`,
        // `2` as left of `infix''`. This way the rightest element is always left out,
        //  so we collect it using a condition for latest element
        List<PsiElement> operands = new ArrayList<>();
        for (int i = 0, infixesLength = infixes.size(); i < infixesLength; i++) {
            Perl6PsiElement infix = infixes.get(i);
            PsiElement left = infix.skipWhitespacesBackward();
            if (left != null && left.getNode().getElementType() != NULL_TERM)
                operands.add(left);
            if (i + 1 == infixesLength) {
                PsiElement right = infix.skipWhitespacesForward();
                if (right != null && right.getNode().getElementType() != NULL_TERM)
                    operands.add(right);
            }
        }
        return operands.toArray(PsiElement.EMPTY_ARRAY);
    }

    @Override
    public String getOperator() {
        Perl6Infix infixOp = PsiTreeUtil.getChildOfType(this, Perl6Infix.class);
        return infixOp == null ? "" : infixOp.getOperator().getText();
    }

    @Override
    public boolean isAssignish() {
        Perl6Infix standardInfix = findChildByClass(Perl6Infix.class);
        if (standardInfix != null) {
            String operator = standardInfix.getText();
            return operator.equals("=") || operator.equals(".=") || operator.equals("⚛=");
        }
        Perl6AssignMetaOp assignMetaOp = findChildByClass(Perl6AssignMetaOp.class);
        if (assignMetaOp != null)
            return true;
        return false;
    }


    @Override
    public @NotNull EffectCollection inferEffects() {
        return Arrays.stream(getOperands())
              .filter(c -> c instanceof Perl6PsiElement)
              .map(c -> ((Perl6PsiElement)c).inferEffects())
              .reduce(EffectCollection.EMPTY, EffectCollection::merge)
              .merge(inferOwnEffects());
    }

    // TODO generalized Perl6PsiElement method?
    @NotNull
    private EffectCollection inferOwnEffects() {
        return isPure() ? EffectCollection.EMPTY : EffectCollection.of(Effect.IMPURE);
    }

    private boolean isPure() {
        List<Perl6Symbol> symbols = resolveLexicalSymbolAllowingMulti(Perl6SymbolKind.Routine, "infix:<" + getOperator() + ">");
        if (symbols == null || symbols.isEmpty())
            return false; // If we can't resolve the operator, assume it's impure
        List<Perl6RoutineDecl> decls = symbols.stream()
          .map(Perl6Symbol::getPsi)
          .filter(s -> s instanceof Perl6RoutineDecl)
          .map(s -> ((Perl6RoutineDecl)s))
          .collect(Collectors.toList());

        // First see if there's a proto
        for (Perl6RoutineDecl decl : decls) {
            if ("proto".equals(decl.getMultiness()))
                return decl.isPure();
        }

        return ContainerUtil.all(decls, Perl6RoutineDecl::isPure);
    }
}
