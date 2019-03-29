package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6SubCallStub;
import edument.perl6idea.psi.stub.Perl6SubCallStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6SubCallImpl extends StubBasedPsiElementBase<Perl6SubCallStub> implements Perl6SubCall {
    public Perl6SubCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6SubCallImpl(Perl6SubCallStub stub, Perl6SubCallStubElementType type) {
        super(stub, type);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6SubCallName call =
            Perl6ElementFactory.createSubCallName(getProject(), name);
        Perl6SubCallName callName = getSubCallNameNode();
        if (callName != null) {
            ASTNode keyNode = callName.getNode();
            ASTNode newKeyNode = call.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }

    @Nullable
    private Perl6SubCallName getSubCallNameNode() {
        return findChildByClass(Perl6SubCallName.class);
    }

    @Override
    public String getCallName() {
        Perl6SubCallName name = getSubCallNameNode();
        return name == null ? "" : name.getCallName();
    }

    @Override
    public PsiElement getWholeCallNode() {
        return this;
    }

    @Override
    public String inferType() {
        PsiElement name = getFirstChild();
        if (!(name instanceof Perl6SubCallName)) return "Mu";
        PsiReference ref = name.getReference();
        if (ref == null) return "Mu";
        PsiElement resolved = ref.resolve();
        if (resolved == null) return "Mu";
        Perl6RoutineDecl decl = (Perl6RoutineDecl)resolved;
        return decl.getReturnType();
    }

    @Override
    public String getCalleeName() {
        Perl6SubCallName callName = findChildByClass(Perl6SubCallName.class);
        return callName == null ? "" : callName.getText();
    }
}
