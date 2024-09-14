package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.raku.parsing.RakuElementTypes;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuRegexAtom;
import org.raku.psi.RakuRegexInfixApplication;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RakuRegexInfixApplicationImpl extends ASTWrapperPsiElement implements RakuRegexInfixApplication {
    public static final @NotNull TokenSet INFIX_TOKEN = TokenSet.create(RakuTokenTypes.REGEX_INFIX);

    public RakuRegexInfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getOperator() {
        ASTNode infixNode = getNode().findChildByType(INFIX_TOKEN);
        return infixNode != null ? infixNode.getText() : null;
    }

    @Override
    public RakuRegexAtom[][] getOperandAtomSequences() {
        List<RakuRegexAtom[]> result = new ArrayList<>();
        List<RakuRegexAtom> currentAtoms = new ArrayList<>();
        for (ASTNode child : getNode().getChildren(TokenSet.ANY)) {
            if (child.getElementType() == org.raku.parsing.RakuTokenTypes.REGEX_INFIX) {
                result.add(currentAtoms.toArray(new RakuRegexAtom[0]));
                currentAtoms.clear();
            }
            else if (child.getElementType() == RakuElementTypes.REGEX_ATOM) {
                currentAtoms.add((RakuRegexAtom)child.getPsi());
            }
        }
        result.add(currentAtoms.toArray(new RakuRegexAtom[0]));
        return result.toArray(new RakuRegexAtom[0][]);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        String infix = getOperator();
        if (infix.equals("||") || infix.equals("|")) {
            // Might match zero width if any one branch matches zero width.
            for (RakuRegexAtom[] sequence : getOperandAtomSequences()) {
                if (atomsMightMatchZeroWidth(sequence))
                    return true;
            }
        }
        return false;
    }
}
