package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6VariableImpl extends ASTWrapperPsiElement implements Perl6Variable {
    public Perl6VariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6VariableReference(this);
    }

    @Override
    public PsiElement getVariableToken() {
        PsiElement normalVar = findChildByType(Perl6TokenTypes.VARIABLE);
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
        PsiElement infix = findChildByType(Perl6ElementTypes.INFIX);
        if (infix != null)
            return "&infix:<" + infix.getText() + ">";
        PsiElement varToken = getVariableToken();
        if (varToken == null) {
            if (findChildByType(Perl6TokenTypes.REGEX_CAPTURE_NAME) != null) {
                return getText();
            } else {
                return null;
            }
        }
        StringBuilder name = new StringBuilder(varToken.getText());
        for (PsiElement colonPair : findChildrenByType(Perl6ElementTypes.COLON_PAIR)) {
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
        if (!(resolved instanceof Perl6VariableDecl)) return super.getUseScope();
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
        return replace(Perl6ElementFactory.createVariable(getProject(), fixedName));
    }

    @Override
    public @NotNull Perl6Type inferType() {
        String text = getText();
        // Special cases, regex
        if (text.substring(1).chars().allMatch(Character::isDigit)
                || text.startsWith("$<") && text.endsWith(">"))
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Match);

        // Check if known by definition
        Perl6SettingTypeId typeByDefinition = Perl6FileImpl.VARIABLE_SYMBOLS.get(text);
        if (typeByDefinition != null)
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), typeByDefinition);

        // Check if typed
        // Firstly get definition
        PsiReference ref = getReference();
        if (ref == null)
            return Perl6Untyped.INSTANCE;
        PsiElement resolved = ref.resolve();
        if (text.equals("$_") && resolved instanceof P6Topicalizer) {
            Perl6Type type = ((P6Topicalizer)resolved).calculateTopicType(this);
            if (type != null)
                return type;
        }
        else if (resolved instanceof Perl6VariableDecl) {
            Perl6Type type = ((Perl6VariableDecl) resolved).inferType();
            if (!(type instanceof Perl6Untyped))
                return type;
        }
        else if (resolved instanceof Perl6ParameterVariable) {
            Perl6Type type = ((Perl6ParameterVariable) resolved).inferType();
            if (!(type instanceof Perl6Untyped))
                return type;
        }
        // Handle $ case
        Perl6Type type = getTypeBySigil(text, resolved);
        return type == null ? Perl6Untyped.INSTANCE : type;
    }

    @Override
    public @Nullable Perl6Type getTypeBySigil(String text, PsiElement resolved) {
        if (resolved == null || resolved instanceof Perl6VariableDecl) {
            if (text.startsWith("@"))
                return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Array);
            else if (text.startsWith("%"))
                return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Hash);
            else if (text.startsWith("&"))
                return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Callable);
        } else if (resolved instanceof Perl6ParameterVariable) {
            if (text.startsWith("@"))
                return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.List);
            else if (text.startsWith("%"))
                return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Map);
            else if (text.startsWith("&"))
                return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Callable);
        }
        return null;
    }

    @Override
    public boolean isCaptureVariable() {
        return getNode().findChildByType(Perl6TokenTypes.REGEX_CAPTURE_NAME) != null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getVariableToken();
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        String varName = getVariableName();
        if (varName != null) {
            if (Perl6Variable.getTwigil(varName) == '^' ||
                Perl6Variable.getTwigil(varName) == ':') {
                collector.offerSymbol(
                    new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, Perl6Variable.getSigil(varName) + varName.substring(2)));
            }
        }
    }
}
