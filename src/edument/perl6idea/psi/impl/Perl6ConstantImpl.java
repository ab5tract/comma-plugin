package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6ConstantStub;
import edument.perl6idea.psi.stub.Perl6ConstantStubElementType;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6ConstantImpl extends Perl6MemberStubBasedPsi<Perl6ConstantStub> implements Perl6Constant {
    public Perl6ConstantImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6ConstantImpl(Perl6ConstantStub stub, Perl6ConstantStubElementType type) {
        super(stub, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = findChildByClass(Perl6Variable.class);
        Perl6TermDefinition termNode = findChildByClass(Perl6TermDefinition.class);
        return varNode != null ? varNode.getVariableToken() : termNode;
    }

    @Override
    public String getName() {
        Perl6ConstantStub stub = this.getStub();
        if (stub != null)
            return stub.getConstantName();
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public String getConstantName() {
        return getName();
    }

    @Override
    public String defaultScope() {
        return "our";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:CONSTANT)";
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, this));
        if (!collector.isSatisfied()) {
            String globalName = getGlobalName();
            if (globalName != null)
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                    this, globalName));
        }
    }
}
