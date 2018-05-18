package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import edument.perl6idea.psi.stub.Perl6VariableDeclStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6VariableDeclImpl extends Perl6MemberStubBasedPsi<Perl6VariableDeclStub> implements Perl6VariableDecl {
    public Perl6VariableDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6VariableDeclImpl(Perl6VariableDeclStub stub, Perl6VariableDeclStubElementType type) {
        super(stub, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = findChildByClass(Perl6Variable.class);
        return varNode != null ? varNode.getVariableToken() : null;
    }

    @Override
    public String getName() {
        Perl6VariableDeclStub stub = getStub();
        if (stub != null)
            return stub.getVariableName();
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        // TODO See https://github.com/JetBrains/intellij-community/blob/db9200fcdb58eccfeb065524bd211b3aa6d6b83c/java/java-psi-impl/src/com/intellij/psi/impl/PsiImplUtil.java
        return null;
    }

    @Override
    public String getVariableName() {
        return getName();
    }

    @Override
    public String getVariableScope() {
        PsiElement parent = getNode().getPsi().getParent();
        return parent instanceof Perl6ScopedDecl ? ((Perl6ScopedDecl)parent).getScope() : "";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(Perl6:VARIABLE_DECLARATION)";
    }
}
