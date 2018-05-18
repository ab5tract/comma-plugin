package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6MethodReference;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.METHOD_CALL_OPERATOR;

public class Perl6MethodCallImpl extends ASTWrapperPsiElement implements Perl6MethodCall {
    private Perl6LongName name = null;

    public Perl6MethodCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6MethodReference(this);
    }

    @Override
    public String getCallName() {
        if (name == null)
            name = findChildByType(LONG_NAME);
        return name == null ? "" : getCallOperator() + name.getText();
    }

    @Override
    public String getCallOperator() {
        ASTNode op = findChildByType(METHOD_CALL_OPERATOR);
        return op == null ? "" : op.getText();
    }
}
