package org.raku.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.*;
import org.raku.psi.stub.RakuTypeNameStub;
import org.raku.psi.stub.RakuTypeNameStubElementType;
import org.raku.psi.type.*;
import org.raku.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RakuTypeNameImpl extends StubBasedPsiElementBase<RakuTypeNameStub> implements RakuTypeName {
    public RakuTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuTypeNameImpl(RakuTypeNameStub stub, RakuTypeNameStubElementType type) {
        super(stub, type);
    }

    @Override
    public PsiReference getReference() {
        return new RakuTypeNameReference(this);
    }

    @Override
    public String getTypeName() {
        RakuTypeNameStub stub = getStub();
        if (stub != null)
            return stub.getTypeName();
        RakuLongName longName = findChildByClass(RakuLongName.class);
        if (longName == null) { // For cases like "::?CLASS" not parsed as a long name
            return getFirstChild().getText();
        } else {
            return longName.getNameWithoutColonPairs();
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "(Raku:TYPE_NAME)";
    }

    @Override
    public @NotNull RakuType inferType() {
        PsiElement resolution = getReference().resolve();
        return tweakType(resolution instanceof RakuPsiElement
               ? new RakuResolvedType(getTypeName(), (RakuPsiElement)resolution)
               : new RakuUnresolvedType(getTypeName()));
    }

    private RakuType tweakType(RakuType type) {
        // Handle definedness type
        RakuLongName longName = findChildByClass(RakuLongName.class);
        if (longName != null) {
            for (RakuColonPair pair : longName.getColonPairs()) {
                if (pair.getKey().equals("D")) {
                    type = new RakuDefinednessType(type, true);
                    break;
                }
                if (pair.getKey().equals("U")) {
                    type = new RakuDefinednessType(type, false);
                    break;
                }
            }
        }

        // Coercion or parametric type.
        if (getNode().findChildByType(RakuTokenTypes.TYPE_COERCION_PARENTHESES_CLOSE) != null) {
            // Coercion is another embedded type name.
            RakuTypeName from = findChildByClass(RakuTypeName.class);
            if (from != null)
                type = new RakuCoercionType(type, from.inferType());
        }
        else  {
            ASTNode curToken = getNode().findChildByType(org.raku.parsing.RakuTokenTypes.TYPE_PARAMETER_BRACKET);
            if (curToken != null) {
                List<RakuType> typeArgs = new ArrayList<>();
                PsiElement arg = RakuPsiUtil.skipSpaces(curToken.getPsi().getNextSibling(), true);
                if (arg instanceof RakuInfixApplication && ((RakuInfixApplication)arg).isCommaOperator()) {
                    // List of parameters (for now, we assume all are types).
                    PsiElement[] operands = ((RakuInfixApplication)arg).getOperands();
                    for (PsiElement operand : operands) {
                        if (operand instanceof RakuPsiElement)
                            typeArgs.add(((RakuPsiElement)operand).inferType());
                    }
                }
                else {
                    // One parameter (for now, we assume it's a type).
                    if (arg instanceof RakuPsiElement)
                        typeArgs.add(((RakuPsiElement)arg).inferType());
                }
                if (!typeArgs.isEmpty())
                    type = new RakuParametricType(type, typeArgs.toArray(new RakuType[0]));
            }
        }

        return type;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        RakuLongName type = RakuElementFactory
            .createTypeName(getProject(), name);
        RakuLongName longName = findChildByClass(RakuLongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = type.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
