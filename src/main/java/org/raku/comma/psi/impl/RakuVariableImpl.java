package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.*;
import org.raku.comma.psi.symbols.RakuExplicitAliasedSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuVariableImpl extends RakuASTWrapperPsiElement implements RakuVariable {
    public RakuVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuVariableReference(this);
    }

    @Override
    public PsiElement getVariableToken() {
        PsiElement normalVar = findChildByType(RakuTokenTypes.VARIABLE);
        return normalVar != null ? normalVar : this;
    }

    @Override
    public String getName() {
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Nullable
    @Override
    public String getVariableName() {
        PsiElement infix = findChildByType(RakuElementTypes.INFIX);
        if (infix != null)
            return "&infix:<" + infix.getText() + ">";
        PsiElement varToken = getVariableToken();
        if (varToken == null) {
            if (findChildByType(RakuTokenTypes.REGEX_CAPTURE_NAME) != null) {
                return getText();
            } else {
                return null;
            }
        }
        StringBuilder name = new StringBuilder(varToken.getText());
        for (PsiElement colonPair : findChildrenByType(RakuElementTypes.COLON_PAIR)) {
            // We should properly mangle these at some point.
            name.append(colonPair.getText());
        }
        return name.toString();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        PsiReference ref = getReference();
        if (ref == null) return super.getUseScope();
        PsiElement resolved = ref.resolve();
        if (!(resolved instanceof RakuVariableDecl)) return super.getUseScope();
        return resolved.getUseScope();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        String oldVarName = getVariableName();
        if (oldVarName == null) return this;
        // If this variable derives from sub, it must have `&` prefix
        String fixedName = oldVarName.startsWith("&") && !name.startsWith("&") ? "&" + name : name;
        // If this variable was public, but had `!` accessing before, preserve it
        fixedName = oldVarName.charAt(1) == '!' ? fixedName.replace('.', '!') : fixedName;
        return replace(RakuElementFactory.createVariable(getProject(), fixedName));
    }

    @Override
    public @NotNull RakuType inferType() {
        String text = getText();
        // Special cases, regex
        if (text.substring(1).chars().allMatch(Character::isDigit)
                || text.startsWith("$<") && text.endsWith(">"))
            return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Match);

        // Check if known by definition
        RakuSettingTypeId typeByDefinition = RakuFileImpl.VARIABLE_SYMBOLS.get(text);
        if (typeByDefinition != null)
            return RakuSdkType.getInstance().getCoreSettingType(getProject(), typeByDefinition);

        // Check if typed
        // Firstly get definition
        PsiReference ref = getReference();
        if (ref == null)
            return RakuUntyped.INSTANCE;
        PsiElement resolved = ref.resolve();
        if (text.equals("$_") && resolved instanceof RakuTopicalizer) {
            RakuType type = ((RakuTopicalizer)resolved).calculateTopicType(this);
            if (type != null)
                return type;
        }
        else if (resolved instanceof RakuVariableDecl) {
            RakuType type = ((RakuVariableDecl) resolved).inferType();
            if (!(type instanceof RakuUntyped))
                return type;
        }
        else if (resolved instanceof RakuParameterVariable) {
            RakuType type = ((RakuParameterVariable) resolved).inferType();
            if (!(type instanceof RakuUntyped))
                return type;
        }
        // Handle $ case
        RakuType type = getTypeBySigil(text, resolved);
        return type == null ? RakuUntyped.INSTANCE : type;
    }

    @Override
    public @Nullable RakuType getTypeBySigil(String text, PsiElement resolved) {
        if (resolved == null || resolved instanceof RakuVariableDecl) {
            if (text.startsWith("@"))
                return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Array);
            else if (text.startsWith("%"))
                return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Hash);
            else if (text.startsWith("&"))
                return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Callable);
        } else if (resolved instanceof RakuParameterVariable) {
            if (text.startsWith("@"))
                return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.List);
            else if (text.startsWith("%"))
                return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Map);
            else if (text.startsWith("&"))
                return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Callable);
        }
        return null;
    }

    @Override
    public boolean isCaptureVariable() {
        return getNode().findChildByType(RakuTokenTypes.REGEX_CAPTURE_NAME) != null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getVariableToken();
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        String varName = getVariableName();
        if (varName != null) {
            if (RakuVariable.getTwigil(varName) == '^' ||
                RakuVariable.getTwigil(varName) == ':') {
                collector.offerSymbol(
                    new RakuExplicitAliasedSymbol(RakuSymbolKind.Variable, this, RakuVariable.getSigil(varName) + varName.substring(2)));
            }
        }
    }
}
