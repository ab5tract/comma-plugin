package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.PARENTHESES_OPEN;

public class Perl6SubCallImpl extends ASTWrapperPsiElement implements Perl6SubCall {
    public Perl6SubCallImpl(@NotNull ASTNode node) {
        super(node);
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
    public String getSubCallName() {
        Perl6SubCallName name = getSubCallNameNode();
        return name == null ? "" : name.getCallName();
    }

    @Override
    public PsiElement[] getSubCallArguments() {
        Perl6SubCallName name = getSubCallNameNode();
        if (name == null)
            return new PsiElement[0];
        PsiElement argument = name.skipWhitespacesForward();
        if (argument != null && argument.getNode().getElementType() == PARENTHESES_OPEN) {
            argument = argument.getNextSibling();
        }

        if (argument == null)
            return new PsiElement[0];

        if (argument instanceof Perl6InfixApplication) {
            return ((Perl6InfixApplication) argument).getOperands();
        } else {
            return new PsiElement[]{argument};
        }
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
}
