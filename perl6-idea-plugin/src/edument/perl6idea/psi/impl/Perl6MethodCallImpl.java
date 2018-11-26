package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6MethodReference;
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

    @Override
    public String getCallName() {
        PsiElement name = findChildByType(LONG_NAME);
        if (name == null)
            name = findChildByType(METHOD_CALL_NAME);
        return name == null ? "" : getCallOperator() + name.getText();
    }

    @Override
    public String getCallOperator() {
        ASTNode op = findChildByType(METHOD_CALL_OPERATOR);
        return op == null ? "" : op.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        String choppedName = name;
        Perl6LongName call;
        if (name.startsWith("!")) {
            choppedName = choppedName.substring(1);
        }
        if (name.startsWith("!")) {
            call = Perl6ElementFactory.createPrivateMethodCall(getProject(), choppedName);
        } else {
            call = Perl6ElementFactory.createPublicMethodCall(getProject(), choppedName);
        }
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = call.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
