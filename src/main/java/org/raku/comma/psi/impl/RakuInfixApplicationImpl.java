package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.effects.Effect;
import org.raku.comma.psi.effects.EffectCollection;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.raku.comma.parsing.RakuElementTypes.NULL_TERM;

public class RakuInfixApplicationImpl extends ASTWrapperPsiElement implements RakuInfixApplication {
    public RakuInfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement[] getOperands() {
        List<RakuPsiElement> infixes = PsiTreeUtil.getChildrenOfAnyType(this, RakuInfix.class,
                                                                        RakuAssignMetaOp.class, RakuZipMetaOp.class, RakuReverseMetaOp.class, RakuCrossMetaOp.class);
        if (infixes.isEmpty())
            return PsiElement.EMPTY_ARRAY;
        // To get elements between infixes, we gather them all on the first level and
        // iterating over every instance, collecting a previous element,
        // thus for `1 infix' 2 infix'' 3` we collect `1` as left of `infix'`,
        // `2` as left of `infix''`. This way the rightest element is always left out,
        //  so we collect it using a condition for latest element
        List<PsiElement> operands = new ArrayList<>();
        for (int i = 0, infixesLength = infixes.size(); i < infixesLength; i++) {
            RakuPsiElement infix = infixes.get(i);
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
        RakuInfix infixOp = PsiTreeUtil.getChildOfType(this, RakuInfix.class);
        return infixOp == null ? "" : infixOp.getOperator().getText();
    }

    @Override
    public boolean isAssignish() {
        RakuInfix standardInfix = findChildByClass(RakuInfix.class);
        if (standardInfix != null) {
            String operator = standardInfix.getText();
            return operator.equals("=") || operator.equals(".=") || operator.equals("⚛=");
        }
        RakuAssignMetaOp assignMetaOp = findChildByClass(RakuAssignMetaOp.class);
        if (assignMetaOp != null)
            return true;
        return false;
    }


    @Override
    public @NotNull EffectCollection inferEffects() {
        return Arrays.stream(getOperands())
              .filter(c -> c instanceof RakuPsiElement)
              .map(c -> ((RakuPsiElement)c).inferEffects())
              .reduce(EffectCollection.EMPTY, EffectCollection::merge)
              .merge(inferOwnEffects());
    }

    // TODO generalized RakuPsiElement method?
    @NotNull
    private EffectCollection inferOwnEffects() {
        return isPure() ? EffectCollection.EMPTY : EffectCollection.of(Effect.IMPURE);
    }

    private boolean isPure() {
        List<RakuSymbol> symbols = resolveLexicalSymbolAllowingMulti(RakuSymbolKind.Routine, "infix:<" + getOperator() + ">");
        if (symbols == null || symbols.isEmpty())
            return false; // If we can't resolve the operator, assume it's impure
        List<RakuRoutineDecl> decls = symbols.stream()
          .map(RakuSymbol::getPsi)
          .filter(s -> s instanceof RakuRoutineDecl)
          .map(s -> ((RakuRoutineDecl)s))
          .collect(Collectors.toList());

        // First see if there's a proto
        for (RakuRoutineDecl decl : decls) {
            if ("proto".equals(decl.getMultiness()))
                return decl.isPure();
        }

        return ContainerUtil.all(decls, RakuRoutineDecl::isPure);
    }
}
