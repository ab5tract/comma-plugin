package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6Constant;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6ConstantImpl extends ASTWrapperPsiElement implements Perl6Constant {
    public Perl6ConstantImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = findChildByClass(Perl6Variable.class);
        return varNode != null ? varNode.getVariableToken() : null;
    }

    @Override
    public String getName() {
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
