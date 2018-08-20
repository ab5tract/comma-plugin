package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.Perl6TraitStub;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;

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
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            return stub.getRoutineKind();

        PsiElement declarator = findChildByType(Perl6TokenTypes.ROUTINE_DECLARATOR);
        return declarator == null ? "sub" : declarator.getText();
    }

    @Override
    public int getTextOffset() {
        PsiElement name = getNameIdentifier();
        return name == null ? 0 : name.getTextOffset();
    }


    @Override
    public String getSignature() {
        return getRoutineName() + summarySignature();
    }

    @Override
    public boolean isPrivate() {
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            return stub.isPrivate();

        return getRoutineName().startsWith("!");
    }

    @Override
    @Nullable
    public String getReturnsTrait() {
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            for (StubElement s : stub.getChildrenStubs())
                if (s instanceof Perl6TraitStub &&
                    ((Perl6TraitStub)s).getTraitModifier().equals("returns"))
                    return ((Perl6TraitStub)s).getTraitName();

        Collection<Perl6Trait> traits = PsiTreeUtil.findChildrenOfType(this, Perl6Trait.class);
        for (Perl6Trait trait : traits)
            if (trait.getTraitModifier().equals("returns"))
                return trait.getTraitName();
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
        offerRoutineSymbols(collector, getRoutineName(), this);
    }

    public static void offerRoutineSymbols(Perl6SymbolCollector collector, String name, Perl6RoutineDecl decl) {
        if (decl.getRoutineKind().equals("method") || decl.getRoutineKind().equals("submethod")) {
            // Do not contribute submethods if restricted
            if (decl.getRoutineKind().equals("submethod") && !collector.areInternalPartsCollected()) return;
            // Do not contribute private methods if restricted
            if (name.startsWith("!") && !collector.areInternalPartsCollected()) return;
            // Contribute name with prefix, so `.foo` can be matched, not `foo`,
            // private methods have `!` as name part already
            if (!name.startsWith("!")) name = "." + name;
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Method, decl, name));
        } else {
            if (name != null && (decl.getScope().equals("my") || decl.getScope().equals("our"))) {
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, decl));
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                                                                     decl, "&" + name));
            }
        }
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        for (String sym : ROUTINE_SYMBOLS)
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, sym, this));
    }
}
