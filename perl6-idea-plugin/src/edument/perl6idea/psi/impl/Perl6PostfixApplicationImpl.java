package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6PostfixApplicationImpl extends ASTWrapperPsiElement implements Perl6PostfixApplication {
    public Perl6PostfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull Perl6Type inferType() {
        PsiElement first = getFirstChild();
        PsiElement last = getLastChild();

        if (first instanceof Perl6TypeName && last instanceof Perl6MethodCall) {
            Perl6MethodCall call = (Perl6MethodCall)last;
            Perl6TypeName typeName = (Perl6TypeName)first;
            return call.getCallName().equals(".new")
                   ? typeName.inferType()
                   : tryToCalculateMethodReturnType(call);
        } else if (last instanceof Perl6MethodCall) {
            return tryToCalculateMethodReturnType((Perl6MethodCall)last);
        }

        return Perl6Untyped.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getOperand() {
        return getFirstChild();
    }

    @Nullable
    @Override
    public PsiElement getPostfix() {
        return getLastChild();
    }

    @Override
    public boolean isAssignish() {
        PsiElement postfix = getPostfix();
        if (postfix instanceof Perl6Postfix) {
            String operator = postfix.getText();
            return operator.equals("++") || operator.equals("--") ||
                operator.equals("⚛++") || operator.equals("⚛--");
        }
        else if (postfix instanceof Perl6MethodCall) {
            return ((Perl6MethodCall)postfix).getCallOperator().equals(".=");
        }
        return false;
    }

    @NotNull
    private static Perl6Type tryToCalculateMethodReturnType(Perl6MethodCall last) {
        PsiReference ref = last.getReference();
        if (ref == null)
            return Perl6Untyped.INSTANCE;
        PsiElement resolved = ref.resolve();
        if (resolved == null)
            return Perl6Untyped.INSTANCE;
        if (resolved instanceof Perl6RoutineDecl) {
            return ((Perl6RoutineDecl)resolved).getReturnType();
        } else if (resolved instanceof Perl6VariableDecl) {
            return ((Perl6VariableDecl) resolved).inferType();
        }
        return Perl6Untyped.INSTANCE;
    }
}
