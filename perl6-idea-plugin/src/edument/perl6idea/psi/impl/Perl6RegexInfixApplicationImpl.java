package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6RegexAtom;
import edument.perl6idea.psi.Perl6RegexInfixApplication;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6RegexInfixApplicationImpl extends ASTWrapperPsiElement implements Perl6RegexInfixApplication {
    public static final @NotNull TokenSet INFIX_TOKEN = TokenSet.create(Perl6TokenTypes.REGEX_INFIX);

    public Perl6RegexInfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getOperator() {
        ASTNode infixNode = getNode().findChildByType(INFIX_TOKEN);
        return infixNode != null ? infixNode.getText() : null;
    }

    @Override
    public Perl6RegexAtom[][] getOperandAtomSequences() {
        List<Perl6RegexAtom[]> result = new ArrayList<>();
        List<Perl6RegexAtom> currentAtoms = new ArrayList<>();
        for (ASTNode child : getNode().getChildren(TokenSet.ANY)) {
            if (child.getElementType() == Perl6TokenTypes.REGEX_INFIX) {
                result.add(currentAtoms.toArray(new Perl6RegexAtom[0]));
                currentAtoms.clear();
            }
            else if (child.getElementType() == Perl6ElementTypes.REGEX_ATOM) {
                currentAtoms.add((Perl6RegexAtom)child.getPsi());
            }
        }
        result.add(currentAtoms.toArray(new Perl6RegexAtom[0]));
        return result.toArray(new Perl6RegexAtom[0][]);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        String infix = getOperator();
        if (infix.equals("||") || infix.equals("|")) {
            // Might match zero width if any one branch matches zero width.
            for (Perl6RegexAtom[] sequence : getOperandAtomSequences()) {
                if (atomsMightMatchZeroWidth(sequence))
                    return true;
            }
        }
        return false;
    }
}
