package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StrLiteral;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.COLON_PAIR;

public class Perl6ColonPairImpl extends ASTWrapperPsiElement implements Perl6ColonPair {
    public Perl6ColonPairImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Pair";
    }

    @Override
    public String getKey() {
        PsiElement sibling = getFirstChild().getNextSibling();
        if (sibling.getNode().getElementType() == COLON_PAIR)
            return sibling.getText();
        else if (sibling instanceof Perl6Variable) {
            String nameWithSigils = ((Perl6Variable)sibling).getVariableName();
            if (nameWithSigils != null) {
                int sigilIndex = Perl6Variable.getTwigil(nameWithSigils) == ' ' ? 1 : 2;
                return nameWithSigils.substring(sigilIndex);
            }
        }
        return null;
    }

    @Override
    public PsiElement getStatement() {
        return PsiTreeUtil.findChildOfAnyType(this, Perl6Statement.class, Perl6StrLiteral.class);
    }
}
