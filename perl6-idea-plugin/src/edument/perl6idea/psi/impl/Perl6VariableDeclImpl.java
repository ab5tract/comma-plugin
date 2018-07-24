package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6MemberStubBasedPsi;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import edument.perl6idea.psi.stub.Perl6VariableDeclStubElementType;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6VariableDeclImpl extends Perl6MemberStubBasedPsi<Perl6VariableDeclStub> implements Perl6VariableDecl {
    public Perl6VariableDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6VariableDeclImpl(Perl6VariableDeclStub stub, Perl6VariableDeclStubElementType type) {
        super(stub, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = getVariable();
        return varNode != null ? varNode.getVariableToken() : null;
    }

    @Override
    public String getName() {
        Perl6VariableDeclStub stub = getStub();
        if (stub != null)
            return stub.getVariableName();
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        // TODO See https://github.com/JetBrains/intellij-community/blob/db9200fcdb58eccfeb065524bd211b3aa6d6b83c/java/java-psi-impl/src/com/intellij/psi/impl/PsiImplUtil.java
        return null;
    }

    @Override
    public String getVariableName() {
        return getName();
    }

    @Override
    public String defaultScope() {
        return "my";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(Perl6:VARIABLE_DECLARATION)";
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        String name = getName();
        if (name != null && name.length() > 1) {
            offerVariableSymbols(collector, name, this);
        }
    }

    public static void offerVariableSymbols(Perl6SymbolCollector collector, String name, Perl6VariableDecl var) {
        boolean isInstanceScoped = var.getScope().equals("has");
        if (Perl6Variable.getTwigil(name) == '!' && collector.areInternalPartsCollected() || Perl6Variable.getTwigil(name) != '!')
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, var, isInstanceScoped));
        if (!collector.isSatisfied() && Perl6Variable.getTwigil(name) == '.' && collector.areInternalPartsCollected())
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                                                                 var, name.substring(0, 1) + "!" + name.substring(2)));
        if (!collector.isSatisfied() && name.startsWith("&") && var.getScope().equals("my"))
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Routine,
                     var, name.substring(1)));
        if (!collector.isSatisfied() && isInstanceScoped && Perl6Variable.getTwigil(name) == '.') {
            if (Perl6Variable.getTwigil(name) == '.') {
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer self!foo;
                    Perl6SymbolKind.Method, var, '!' + name.substring(2)));
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer self.foo;
                    Perl6SymbolKind.Method, var, '.' + name.substring(2)));
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer $.foo;
                    Perl6SymbolKind.Method, var,
                    Perl6Variable.getSigil(var.getVariableName()) + '.' + var.getVariableName().substring(2)
                ));
            }
        }
    }

    private Perl6Variable getVariable() {
        return findChildByClass(Perl6Variable.class);
    }
}
