package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.*;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuPostfixApplicationImpl extends ASTWrapperPsiElement implements RakuPostfixApplication {
    public RakuPostfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        PsiElement first = getFirstChild();
        PsiElement last = getLastChild();

        if (first instanceof RakuTypeName typeName && last instanceof RakuMethodCall call) {
            return call.getCallName().equals(".new")
                   ? typeName.inferType()
                   : tryToCalculateMethodReturnType(call);
        } else if (last instanceof RakuMethodCall) {
            return tryToCalculateMethodReturnType((RakuMethodCall)last);
        }

        return RakuUntyped.INSTANCE;
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
        if (postfix instanceof RakuPostfix) {
            String operator = postfix.getText();
            return operator.equals("++") || operator.equals("--") ||
                operator.equals("⚛++") || operator.equals("⚛--");
        }
        else if (postfix instanceof RakuMethodCall) {
            return ((RakuMethodCall)postfix).getCallOperator().equals(".=");
        }
        return false;
    }

    @NotNull
    private static RakuType tryToCalculateMethodReturnType(RakuMethodCall last) {
        PsiReference ref = last.getReference();
        if (ref == null)
            return RakuUntyped.INSTANCE;
        PsiElement resolved = ref.resolve();
        if (resolved == null)
            return RakuUntyped.INSTANCE;
        if (resolved instanceof RakuRoutineDecl) {
            return ((RakuRoutineDecl)resolved).getReturnType();
        } else if (resolved instanceof RakuVariableDecl) {
            return ((RakuVariableDecl) resolved).inferType();
        }
        return RakuUntyped.INSTANCE;
    }
}
