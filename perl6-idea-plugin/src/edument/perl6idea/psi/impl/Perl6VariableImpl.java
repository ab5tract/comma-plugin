package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableReference;
import org.jetbrains.annotations.NotNull;

public class Perl6VariableImpl extends ASTWrapperPsiElement implements Perl6Variable {
    public Perl6VariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6VariableReference(this);
    }

    @Override
    public PsiElement getVariableToken() {
        return findChildByType(Perl6TokenTypes.VARIABLE);
    }

    @Override
    public String inferType() {
        String text = getText();
        if (text.equals("$!")) return "Exception";
        if (text.equals("$/")) return "Match";
        if (text.substring(1).chars().allMatch(Character::isDigit)) return "Match";
        return "Any";
    }
}
