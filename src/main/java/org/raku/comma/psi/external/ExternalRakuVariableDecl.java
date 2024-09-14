package org.raku.comma.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.psi.stub.RakuVariableDeclStub;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUnresolvedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExternalRakuVariableDecl extends RakuExternalPsiElement implements RakuVariableDecl {
    private final String myName;
    private final String myScope;
    private final String myType;

    public ExternalRakuVariableDecl(Project project, PsiElement parent, String name, String scope, String type) {
        myProject = project;
        myParent = parent;
        myName = name;
        myScope = scope;
        myType = type;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public String[] getVariableNames() {
        return new String[]{getName()};
    }

    @Override
    public RakuVariable[] getVariables() {
        return new RakuVariable[0];
    }

    @Override
    public boolean hasInitializer() {
        return false;
    }

    @Nullable
    @Override
    public PsiElement getInitializer(RakuVariable variable) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        return null;
    }

    @Override
    public void removeVariable(RakuVariable variable) {

    }

    @Override
    public IStubElementType<?, ?> getElementType() {
        return null;
    }

    @Override
    public RakuVariableDeclStub getStub() {
        return null;
    }

    @Override
    public @NotNull String getScope() {
        return myScope;
    }

    @Override
    public @NotNull RakuType inferType() {
        return new RakuUnresolvedType(myType);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        if (getScope().equals("has"))
            return;

        String name = getName();
        if (name.length() <= 1)
            return;

        // Our scoped term definitions are not yet implemented in rakudo
        collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Variable, this));
        if (collector.isSatisfied()) return;
        if (name.startsWith("&"))
            collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Routine,
                                                                this, name.substring(1)));
    }

    @Override
    public void contributeMOPSymbols(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        if (!getScope().equals("has"))
            return;

        String name = getName();
        if (name.length() < 3)
            return;

        if (RakuVariable.getTwigil(name) == '!' && symbolsAllowed.privateAttributesVisible) {
            collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Variable, this));
        } else if (RakuVariable.getTwigil(name) == '.') {
            collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Variable, this));
            if (collector.isSatisfied()) return;
            if (symbolsAllowed.privateAttributesVisible) {
                collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Variable,
                                                                    this, name.charAt(0) + "!" + name.substring(2)));
                if (collector.isSatisfied()) return;
            }
            // Offer self.foo;
            collector.offerMultiSymbol(new RakuExplicitAliasedSymbol(
              RakuSymbolKind.Method, this, '.' + name.substring(2)), false);
        }
    }
}
