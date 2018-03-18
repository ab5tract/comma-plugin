package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6SubCallReference;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.SUB_CALL_NAME;

public class Perl6SubCallImpl extends ASTWrapperPsiElement implements Perl6SubCall {
    public Perl6SubCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6SubCallReference(this);
    }

    @Override
    public String getCallName() {
        PsiElement name = findChildByType(SUB_CALL_NAME);
        return name == null ? "" : name.getText();
    }
}
