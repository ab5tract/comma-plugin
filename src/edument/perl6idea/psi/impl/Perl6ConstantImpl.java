package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6Constant;
import edument.perl6idea.psi.Perl6SymbolLike;
import edument.perl6idea.psi.Perl6TermDefinition;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6ConstantImpl extends Perl6SymbolLike implements Perl6Constant {
    public Perl6ConstantImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getTypeLikeName() {
        String name = getName();
        return name == null ? "<anon>" : name;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = findChildByClass(Perl6Variable.class);
        Perl6TermDefinition termNode = findChildByClass(Perl6TermDefinition.class);
        return varNode != null ? varNode.getVariableToken() : termNode;
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
