package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.*;
import org.raku.comma.psi.symbols.RakuExplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUnresolvedType;
import org.raku.comma.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.raku.comma.parsing.RakuTokenTypes.PARAMETER_QUANTIFIER;
import static org.raku.comma.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public class RakuParameterImpl extends RakuASTWrapperPsiElement implements RakuParameter {
    public RakuParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary(boolean includeName) {
        String maybeSignature = yieldSignature();
        if (maybeSignature != null)
            return maybeSignature;

        if (findChildByClass(RakuValueConstraint.class) != null)
            return "$";

        StringBuilder summary = new StringBuilder();
        RakuTypeNameImpl maybeType = findChildByClass(RakuTypeNameImpl.class);
        if (maybeType != null)
            summary.append(maybeType.getText()).append(" ");
        PsiElement maybeSlurpy = getFirstChild();
        if (maybeSlurpy.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeSlurpy.getText());

        RakuNamedParameterImpl maybeNamed = findChildByClass(RakuNamedParameterImpl.class);
        if (maybeNamed != null) summary.append(maybeNamed.summary());

        RakuParameterVariableImpl maybeNormal = findChildByClass(RakuParameterVariableImpl.class);
        if (maybeNormal != null) summary.append(maybeNormal.summary(includeName));

        RakuTermDefinitionImpl term = findChildByClass(RakuTermDefinitionImpl.class);
        if (term != null && term.getPrevSibling().getText().equals("\\"))
            summary.append(term.getText());

        PsiElement maybeQuant = getLastChild();
        while (maybeQuant != null && (
          maybeQuant instanceof RakuTrait ||
          maybeQuant instanceof PsiWhiteSpace || maybeQuant.getNode().getElementType() == UNV_WHITE_SPACE)) {
            maybeQuant = maybeQuant.getPrevSibling();
        }
        if (maybeQuant != null && maybeQuant.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeQuant.getText());

        return summary.toString();
    }

    @Nullable
    private String yieldSignature() {
        RakuSignatureImpl maybeSig = findChildByClass(RakuSignatureImpl.class);
        if (maybeSig != null) {
            if (getFirstChild() instanceof RakuNamedParameter)
                return ":$";
            if (maybeSig.getText().charAt(0) == '(')
                return "$";
            if (maybeSig.getText().charAt(0) == '[')
                return "@";
        }
        return null;
    }

    @Override
    public String getVariableName() {
        RakuParameterVariable var = PsiTreeUtil.findChildOfType(this, RakuParameterVariable.class);
        return var != null ? var.getText() : "";
    }

    @Override
    public RakuVariable[] getVariables() {
        RakuVariable var = PsiTreeUtil.findChildOfType(this, RakuVariable.class);
        return var == null ? new RakuVariable[0] : new RakuVariable[]{var};
    }

    @Override
    public String[] getVariableNames() {
        return new String[]{getVariableName()};
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        RakuParameterDefault parameterDefault = PsiTreeUtil.getChildOfType(this, RakuParameterDefault.class);
        return parameterDefault == null ? getMultideclarationInit() : parameterDefault.getLastChild();
    }

    private PsiElement getMultideclarationInit() {
        RakuVariable paramVariable = PsiTreeUtil.findChildOfType(this, RakuVariable.class);
        // We try to see if we are really in a `my ($foo, $bar) ...` case or
        // the signature we are in is just a part of some signature (in a pointy block or a sub)
        PsiElement decl = PsiTreeUtil.getParentOfType(this, RakuVariableDecl.class, RakuPsiScope.class);
        if (decl instanceof RakuVariableDecl)
            return ((RakuVariableDecl)decl).getInitializer(paramVariable);

        return null;
    }

    @Override
    public boolean isPositional() {
        // If it's declared as a named parameter, it certainly isn't positional.
        if (findChildByClass(RakuNamedParameterImpl.class) != null)
            return false;

        // If it's slurpy and has the % sigil, it's also named.
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        if (quant != null && quant.getText().equals("*") && getVariableName().startsWith("%"))
            return false;
        if (quant != null && quant.getText().equals("|"))
            return false;

        // Any other case is positional.
        return true;
    }

    @Override
    public boolean isNamed() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        if (quant != null && quant.getText().equals("*") && getVariableName().startsWith("%"))
            return true;
        return findChildByClass(RakuNamedParameterImpl.class) != null;
    }

    @Override
    public @Nullable RakuWhereConstraint getWhereConstraint() {
        return findChildByClass(RakuWhereConstraint.class);
    }

    @Override
    public RakuPsiElement getValueConstraint() {
        RakuValueConstraint constraint = findChildByClass(RakuValueConstraint.class);
        return constraint == null ? null : PsiTreeUtil.getChildOfType(constraint, RakuPsiElement.class);
    }

    @Override
    public boolean isSlurpy() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        return quant != null &&
               (quant.getText().equals("*") || quant.getText().equals("**")
                || quant.getText().equals("+"));
    }

    @Override
    public boolean isRequired() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        return quant != null && quant.getText().equals("!");
    }

    @Override
    public boolean isExplicitlyOptional() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        return quant != null && quant.getText().equals("?");
    }

    @Override
    public boolean isOptional() {
        // Certainly optional if it has a default.
        if (findChildByClass(RakuParameterDefault.class) != null)
            return true;

        // If there's a quantifier and it's ?, that is decisively optional;
        // any other quantifier implies non-optional (slurpy, required, etc.)
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        if (quant != null) {
            if (quant.getText().equals("?"))
                return true;
            return false;
        }

        // Otherwise, positional defaults to required, and named to not.
        return !isPositional();
    }

    @Override
    public boolean isCopy() {
        return hasTrait("copy");
    }

    @Override
    public boolean isRW() {
        // Trivially RW if we have the trait.
        if (hasTrait("rw"))
            return true;

        // However, we can also be in a <-> pointy block.
        RakuSignature signature = PsiTreeUtil.getParentOfType(this, RakuSignature.class);
        if (signature != null) {
            PsiElement signatured = signature.getParent();
            return signatured instanceof RakuPointyBlock &&
                   ((RakuPointyBlock)signatured).getLambda().equals("<->");
        }
        return false;
    }

    private boolean hasTrait(String traitName) {
        RakuTrait[] traits = PsiTreeUtil.getChildrenOfType(this, RakuTrait.class);
        if (traits != null)
            for (RakuTrait trait : traits) {
                if (trait.getTraitModifier().equals("is") && trait.getTraitName().equals(traitName))
                    return true;
            }
        return false;
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        RakuTermDefinition defterm = findChildByClass(RakuTermDefinition.class);
        if (defterm != null) {
            String name = defterm.getText();
            if (name.length() > 0) {
                collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, this));
            }
        }
    }

    @Override
    public @NotNull String getScope() {
        return "my";
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement defterm = getNameIdentifier();
        return defterm == null ? null : defterm.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(RakuTermDefinition.class);
    }

    @Override
    public @NotNull RakuType inferType() {
        @Nullable RakuTypeName type = findChildByClass(RakuTypeName.class);
        if (type != null) {
            return new RakuUnresolvedType(type.getTypeName());
        }
        else {
            return RakuUntyped.INSTANCE;
        }
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean equalsParameter(RakuParameter other) {
        // If sigils differ, not equal
        if (isPositional() != other.isPositional() ||
            isNamed() != other.isNamed() ||
            isSlurpy() != other.isSlurpy())
            return false;

        if (RakuVariable.getSigil(other.getVariableName()) != RakuVariable.getSigil(getVariableName()))
            return false;

        // if there is a where clause, check it by text
        RakuWhereConstraint selfWhere = getWhereConstraint();
        RakuWhereConstraint otherWhere = other.getWhereConstraint();
        if (otherWhere != null ^ selfWhere != null) {
            return false;
        }
        else if (otherWhere != null) {
            if (!selfWhere.getText().equals(otherWhere.getText()))
                return false;
        }

        RakuType selfType = inferType();
        RakuType otherType = other.inferType();
        if (selfType.equals(otherType))
            return true;
        return false; // Better to get more false negatives than false positives
    }
}
