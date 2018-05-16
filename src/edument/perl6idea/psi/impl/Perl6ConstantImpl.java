package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6ConstantStub;
import edument.perl6idea.psi.stub.Perl6ConstantStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6ConstantImpl extends Perl6PresentableStub<Perl6ConstantStub> implements Perl6Constant {
    public Perl6ConstantImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6ConstantImpl(Perl6ConstantStub stub, Perl6ConstantStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getSymbolName() {
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

    @Override
    public String getConstantName() {
        Perl6ConstantStub stub = getStub();
        return stub != null ? stub.getConstantName() : getSymbolName();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:CONSTANT)";
    }
}
