package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.METHOD_CALL_NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.METHOD_CALL_OPERATOR;

public class Perl6MethodCallImpl extends ASTWrapperPsiElement implements Perl6MethodCall {
    public Perl6MethodCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6MethodReference(this);
    }

    @NotNull
    @Override
    public String getCallName() {
        PsiElement name = findChildByType(LONG_NAME);
        if (name == null)
            name = findChildByType(METHOD_CALL_NAME);
        return name == null ? "" : getCallOperator() + name.getText();
    }

    @Override
    public PsiElement getWholeCallNode() {
        return PsiTreeUtil.getParentOfType(this, Perl6PostfixApplication.class);
    }

    @Override
    public String getCallOperator() {
        ASTNode op = findChildByType(METHOD_CALL_OPERATOR);
        return op == null ? "" : op.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        name = StringUtil.trimStart(name, "!");
        Perl6LongName call = Perl6ElementFactory.createMethodCallName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = call.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
