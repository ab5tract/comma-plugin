package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.PARAMETER_QUANTIFIER;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6ParameterImpl extends ASTWrapperPsiElement implements Perl6Parameter {
    public Perl6ParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary() {
        String maybeSignature = yieldSignature();
        if (maybeSignature != null)
            return maybeSignature;

        if (findChildByClass(Perl6ValueConstraint.class) != null)
            return "$";

        StringBuilder summary = new StringBuilder();
        Perl6TypeNameImpl maybeType = findChildByClass(Perl6TypeNameImpl.class);
        if (maybeType != null)
            summary.append(maybeType.getText()).append(" ");
        PsiElement maybeSlurpy = getFirstChild();
        if (maybeSlurpy.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeSlurpy.getText());

        Perl6NamedParameterImpl maybeNamed = findChildByClass(Perl6NamedParameterImpl.class);
        if (maybeNamed != null) summary.append(maybeNamed.summary());

        Perl6ParameterVariableImpl maybeNormal = findChildByClass(Perl6ParameterVariableImpl.class);
        if (maybeNormal != null) summary.append(maybeNormal.summary());

        Perl6TermDefinitionImpl term = findChildByClass(Perl6TermDefinitionImpl.class);
        if (term != null && term.getPrevSibling().getText().equals("\\"))
            summary.append(term.getText());

        PsiElement maybeQuant = getLastChild();
        while (maybeQuant != null && (maybeQuant instanceof PsiWhiteSpace || maybeQuant.getNode().getElementType() == UNV_WHITE_SPACE)) {
            maybeQuant = maybeQuant.getPrevSibling();
        }
        if (maybeQuant != null && maybeQuant.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeQuant.getText());

        return summary.toString();
    }

    @Nullable
    private String yieldSignature() {
        Perl6SignatureImpl maybeSig = findChildByClass(Perl6SignatureImpl.class);
        if (maybeSig != null) {
            if (maybeSig.getText().charAt(0) == '(')
                return "$";
            if  (maybeSig.getText().charAt(0) == '[')
                return "@";
        }
        return null;
    }

    @Override
    public String getVariableName() {
        Perl6ParameterVariable var = findChildByClass(Perl6ParameterVariable.class);
        return var != null ? var.getText() : "";
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        Perl6ParameterDefault parameterDefault = PsiTreeUtil.getChildOfType(this, Perl6ParameterDefault.class);
        return parameterDefault == null ? null : parameterDefault.getLastChild();
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        Perl6TermDefinition defterm = findChildByClass(Perl6TermDefinition.class);
        if (defterm != null) {
            String name = defterm.getText();
            if (name.length() > 0) {
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, this));
            }
        }
    }

    @Override
    public String getScope() {
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
        return findChildByClass(Perl6TermDefinition.class);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
