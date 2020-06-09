package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
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
        String name = varToken.getText();
        for (PsiElement colonPair : findChildrenByType(Perl6ElementTypes.COLON_PAIR)) {
            // We should properly mangle these at some point.
            name += colonPair.getText();
        }
        return name;
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
        String fixedName = oldVarName.startsWith("&") ? "&" + name : name;
        // If this variable was public, but had `!` accessing before, preserve it
        fixedName = oldVarName.charAt(1) == '!' ? fixedName.replace('.', '!') : fixedName;
        return replace(Perl6ElementFactory.createVariable(getProject(), fixedName));
    }

    @Override
    public @NotNull String inferType() {
        String text = getText();
        // Special cases, regex
        if (text.substring(1).chars().allMatch(Character::isDigit)) return "Match";
        if (text.startsWith("$<") && text.endsWith(">")) return "Match";

        // Check if known by definition
        String typeByDefinition = Perl6FileImpl.VARIABLE_SYMBOLS.get(text);
        if (typeByDefinition != null)
            return typeByDefinition;

        // Check if typed
        // Firstly get definition
        PsiReference ref = getReference();
        if (ref == null) return "Any";
        PsiElement resolved = ref.resolve();
        if (resolved instanceof Perl6VariableDecl) {
            String type = ((Perl6VariableDecl) resolved).inferType();
            if (!type.equals("Any"))
                return type;
        } else if (resolved instanceof Perl6ParameterVariable) {
            String type = ((Perl6ParameterVariable) resolved).inferType();
            if (!type.equals("Any"))
                return type;
        }
        // Handle $ case
        String type = getTypeBySigil(text, resolved);
        return type == null ? "Any" : type;
    }

    @Override
    @Nullable
    public String getTypeBySigil(String text, PsiElement resolved) {
        if (resolved == null || resolved instanceof Perl6VariableDecl) {
            if (text.startsWith("@"))
                return "Array";
            else if (text.startsWith("%"))
                return "Hash";
            else if (text.startsWith("&"))
                return "Callable";
        } else if (resolved instanceof Perl6ParameterVariable) {
            if (text.startsWith("@"))
                return "List";
            else if (text.startsWith("%"))
                return "Map";
            else if (text.startsWith("&"))
                return "Callable";
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getVariableToken();
    }
}
