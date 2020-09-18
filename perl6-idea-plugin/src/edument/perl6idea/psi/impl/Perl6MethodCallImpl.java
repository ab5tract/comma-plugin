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
import org.jetbrains.annotations.Nullable;

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
        PsiElement name = getSimpleName();
        return name == null ? "" : getCallOperator() + name.getText();
    }

    @Override
    public PsiElement getSimpleName() {
        PsiElement name = findChildByType(LONG_NAME);
        if (name != null)
            return name;
        return findChildByType(METHOD_CALL_NAME);
    }

    @Override
    public PsiElement getWholeCallNode() {
        return PsiTreeUtil.getParentOfType(this, Perl6PostfixApplication.class);
    }

    @NotNull
    @Override
    public String getCallOperator() {
        PsiElement op = getCallOperatorNode();
        return op == null ? "" : op.getText();
    }

    @Nullable
    @Override
    public PsiElement getCallOperatorNode() {
        return findChildByType(METHOD_CALL_OPERATOR);
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        Perl6LongName newLongName = Perl6ElementFactory.createMethodCallName(getProject(), StringUtil.trimStart(newName, "!"));
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null)
            longName.replace(newLongName);

        String oldOperator = getCallOperator();
        // If it is private and stays private OR if it is public and stays public
        if (oldOperator.equals("!") && newName.startsWith("!") || oldOperator.equals(".") && !newName.startsWith("!"))
            return this;

        PsiElement newOperator = Perl6ElementFactory.createMethodCallOperator(getProject(), newName.startsWith("!"));
        PsiElement operatorNode = getCallOperatorNode();
        if (operatorNode != null)
            operatorNode.replace(newOperator);
        return this;
    }
}
