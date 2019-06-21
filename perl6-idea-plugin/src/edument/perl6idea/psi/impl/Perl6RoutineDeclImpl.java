package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.Perl6TraitStub;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

import static edument.perl6idea.parsing.Perl6ElementTypes.BLOCKOID;
import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;

public class Perl6RoutineDeclImpl extends Perl6MemberStubBasedPsi<Perl6RoutineDeclStub> implements Perl6RoutineDecl, Perl6SignatureHolder, PsiMetaOwner {
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
            for (StubElement s : stub.getChildrenStubs()) {
                if (!(s instanceof Perl6TraitStub)) continue;
                Perl6TraitStub traitStub = (Perl6TraitStub)s;
                String modifier = traitStub.getTraitModifier();
                if (modifier.equals("returns") ||
                    modifier.equals("of"))
                    return ((Perl6TraitStub)s).getTraitName();
            }
        Collection<Perl6Trait> traits = PsiTreeUtil.findChildrenOfType(this, Perl6Trait.class);
        for (Perl6Trait trait : traits) {
            String modifier = trait.getTraitModifier();
            if (modifier.equals("returns") || modifier.equals("of"))
                return trait.getTraitName();
        }
        return null;
    }

    @Override
    public boolean isStubbed() {
        Perl6Blockoid blockoid = (Perl6Blockoid)findChildByFilter(TokenSet.create(BLOCKOID));
        if (blockoid == null) return false;
        PsiElement statementList = PsiTreeUtil.getChildOfType(blockoid, Perl6StatementList.class);
        if (statementList == null || statementList.getChildren().length != 1) return false;
        PsiElement[] statement = statementList.getChildren()[0].getChildren();
        return statement.length != 0 && statement[0] instanceof Perl6StubCode;
    }

    @Nullable
    @Override
    public Perl6Signature getSignatureNode() {
        return findChildByClass(Perl6Signature.class);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(LONG_NAME);
    }

    @NotNull
    @Override
    public PsiElement[] getContent() {
        Perl6StatementList statementList = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (statementList == null)
            return PsiElement.EMPTY_ARRAY;
        return statementList.getChildren();
    }

    @Override
    public Perl6Parameter[] getParams() {
        Perl6Signature sig = findChildByClass(Perl6Signature.class);
        if (sig != null)
            return sig.getParameters();
        return new Perl6Parameter[]{};
    }

    @Override
    public String getName() {
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            return stub.getRoutineName();
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "<anon>" : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        if (!isPrivate() && getRoutineKind().equals("method"))
            throw new IncorrectOperationException("Rename for non-private methods is not yet supported");
        Perl6LongName newLongName = Perl6ElementFactory.createMethodCallName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = newLongName.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
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

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        PsiElement decl = this;
        String un_dashed_name = getName();
        if (un_dashed_name == null) return null;
        // Chop off everything before last `-` symbol
        un_dashed_name = un_dashed_name.substring(un_dashed_name.lastIndexOf('-') + 1);
        String final_un_dashed_name = un_dashed_name;
        return new PsiMetaData() {
            @Override
            public PsiElement getDeclaration() {
                return decl;
            }

            @Override
            public String getName(PsiElement context) {
                return final_un_dashed_name;
            }

            @Override
            public String getName() {
                return final_un_dashed_name;
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

    public static void offerRoutineSymbols(Perl6SymbolCollector collector, String name, Perl6RoutineDecl decl) {
        if (decl.getRoutineName().equals("<anon>")) return;
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
            if (decl.getScope().equals("my") || decl.getScope().equals("our")) {
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
        String routineKind = getRoutineKind();
        if (routineKind.equals("method") || routineKind.equals("submethod")) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "%_", this));
        }
    }
}
