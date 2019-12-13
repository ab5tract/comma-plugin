package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Infix;
import edument.perl6idea.psi.Perl6InfixApplication;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6ElementTypes.NULL_TERM;

public class Perl6InfixApplicationImpl extends ASTWrapperPsiElement implements Perl6InfixApplication {
    public Perl6InfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement[] getOperands() {
        Perl6Infix[] infixes = PsiTreeUtil.getChildrenOfType(this, Perl6Infix.class);
        if (infixes == null)
            return PsiElement.EMPTY_ARRAY;
        // To get elements between infixes, we gather them all on the first level and
        // iterating over every instance, collecting a previous element,
        // thus for `1 infix' 2 infix'' 3` we collect `1` as left of `infix'`,
        // `2` as left of `infix''`. This way the rightest element is always left out,
        //  so we collect it using a condition for latest element
        List<PsiElement> operands = new ArrayList<>();
        for (int i = 0, infixesLength = infixes.length; i < infixesLength; i++) {
            Perl6Infix infix = infixes[i];
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
}
