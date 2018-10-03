package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6ParameterDefault;
import edument.perl6idea.psi.Perl6ParameterVariable;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.TYPE_NAME;

public class Perl6ParameterVariableImpl extends ASTWrapperPsiElement implements Perl6ParameterVariable {
    public Perl6ParameterVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(Perl6TokenTypes.VARIABLE);
    }

    @NotNull
    @Override
    public String getName() {
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Override
    public String getScope() {
        return getName().contains("!") ? "has" : "my";
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        String variableName = getName();
        char sigil = Perl6Variable.getSigil(variableName);
        char twigil = Perl6Variable.getTwigil(variableName);
        String prefix = twigil != ' ' ?
                        String.valueOf(sigil) + String.valueOf(twigil) :
                        String.valueOf(sigil);
        Perl6Variable variable =
            Perl6ElementFactory.createVariable(getProject(), prefix + name);
        PsiElement keyNode = findChildByFilter(TokenSet.create(Perl6TokenTypes.VARIABLE));
        ASTNode newKeyNode = variable.getNode();
        getNode().replaceChild(keyNode.getNode(), newKeyNode);
        return this;
    }

    @Override
    public String summary() {
        String sigil = String.valueOf(this.getName().charAt(0));
        PsiElement defaultValue = PsiTreeUtil.getNextSiblingOfType(this, Perl6ParameterDefault.class);
        if (defaultValue != null) sigil += '?';
        return sigil;
    }

    @Override
    public String getVariableType() {
        PsiElement type = PsiTreeUtil.findSiblingBackward(this, TYPE_NAME, null);
        if (type == null) return " ";
        return getCuttedName(type.getText());
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        String name = getName();
        if (name.length() > 1) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, this));
            if (!collector.isSatisfied() && name.startsWith("&") && getScope().equals("my"))
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Routine,
                    this, name.substring(1)));
        }
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        String desigilname = getName();
        PsiElement decl = this;
        // Chop off sigil, if it's not sigil-only name
        if (desigilname.length() > 1)
            desigilname = desigilname.substring(1);
        // Chop off twigil if any
        if (desigilname.length() >= 2 && !Character.isLetter(desigilname.charAt(0)))
            desigilname = desigilname.substring(1);
        String finaldesigilname = desigilname;
        return new PsiMetaData() {
            @Override
            public PsiElement getDeclaration() {
                return decl;
            }

            @Override
            public String getName(PsiElement context) {
                return finaldesigilname;
            }

            @Override
            public String getName() {
                return finaldesigilname;
            }

            @Override
            public void init(PsiElement element) {
            }

            @NotNull
            @Override
            public Object[] getDependences() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }
}
