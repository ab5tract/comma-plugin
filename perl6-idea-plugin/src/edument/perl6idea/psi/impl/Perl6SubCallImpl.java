package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6SubCallName;
import org.jetbrains.annotations.NotNull;

public class Perl6SubCallImpl extends ASTWrapperPsiElement implements Perl6SubCall {
    public Perl6SubCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6SubCallName call =
            Perl6ElementFactory.createSubCallName(getProject(), name);
        ASTNode keyNode = findChildByClass(Perl6SubCallName.class).getNode();
        ASTNode newKeyNode = call.getNode();
        getNode().replaceChild(keyNode, newKeyNode);
        return null;
    }
}
