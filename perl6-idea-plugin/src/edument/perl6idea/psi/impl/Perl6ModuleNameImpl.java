package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.psi.Perl6ModuleReference;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleNameImpl extends ASTWrapperPsiElement implements Perl6ModuleName {
    public Perl6ModuleNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6ModuleReference(this);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6LongName moduleName = Perl6ElementFactory
            .createModuleName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = moduleName.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
