package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;

public class Perl6RoutineDeclImpl extends Perl6MemberStubBasedPsi<Perl6RoutineDeclStub> implements Perl6RoutineDecl, Perl6SignatureHolder {
    private static final String[] ROUTINE_SYMBOLS = { "$/", "$!", "$_", "&?ROUTINE", "&?BLOCK" };

    public Perl6RoutineDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6RoutineDeclImpl(final Perl6RoutineDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getRoutineKind() {
        PsiElement declarator = findChildByType(Perl6TokenTypes.ROUTINE_DECLARATOR);
        return declarator == null ? "sub" : declarator.getText();
    }

    @Override
    public String getSignature() {
        return getRoutineName() + summarySignature();
    }

    @Override
    public boolean isPrivateMethod() {
        return getRoutineName().startsWith("!");
    }

    @Override
    @Nullable
    public String getReturnsTrait() {
        ASTNode trait = getNode().findChildByType(TRAIT);
        if (trait != null && trait.getFirstChildNode().getText().equals("returns")) {
            ASTNode type = trait.findChildByType(TYPE_NAME);
            if (type != null) return type.getText();
        }
        return null;
    }

    @Override
    public Perl6Signature getSignatureNode() {
        return findChildByClass(Perl6SignatureImpl.class);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(LONG_NAME);
    }

    @Override
    public String getName() {
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            return stub.getRoutineName();
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public String getRoutineName() {
        String name = getName();
        return name == null ? "<anon>" : name;
    }

    @Override
    public String presentableName() {
        return getName() + summarySignature();
    }

    @Override
    public String defaultScope() {
        return getRoutineKind().equals("sub") ? "my" : "has";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:ROUTINE_DECLARATION)";
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        String name = getName();
        if (name != null && getScope().equals("my")) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, this));
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                    this, "&" + name));
        }
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        for (String sym : ROUTINE_SYMBOLS)
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, sym, this));
    }
}
