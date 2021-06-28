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
import edument.perl6idea.pod.*;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.Perl6TraitStub;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

import static edument.perl6idea.parsing.Perl6ElementTypes.BLOCKOID;
import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;

public class Perl6RoutineDeclImpl extends Perl6MemberStubBasedPsi<Perl6RoutineDeclStub>
        implements Perl6RoutineDecl, Perl6SignatureHolder, PsiMetaOwner {
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

        PsiElement declarator = getDeclaratorNode();
        return declarator == null ? "sub" : declarator.getText();
    }

    @Override
    public PsiElement getDeclaratorNode() {
        return findChildByType(Perl6TokenTypes.ROUTINE_DECLARATOR);
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
    public @NotNull Perl6Type getReturnType() {
        return Perl6RoutineDecl.super.getReturnType();
    }

    @Override
    public boolean isMethod() {
        String kind = getRoutineKind();
        return kind.equals("method") || kind.equals("submethod");
    }

    @Override
    public boolean isSub() {
        return getRoutineKind().equals("sub");
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
    public boolean isDeprecated() {
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            for (StubElement s : stub.getChildrenStubs()) {
                if (!(s instanceof Perl6TraitStub)) continue;
                Perl6TraitStub traitStub = (Perl6TraitStub)s;
                String modifier = traitStub.getTraitModifier();
                if (modifier.equals("is") && "DEPRECATED".equals(((Perl6TraitStub)s).getTraitName()))
                    return true;
            }
        return findTrait("is", "DEPRECATED") != null;
    }

    @Override
    public @Nullable String getDeprecationMessage() {
        Perl6Trait trait = findTrait("is", "DEPRECATED");
        if (trait != null) {
            Perl6StrLiteral reason = PsiTreeUtil.findChildOfType(trait, Perl6StrLiteral.class);
            if (reason != null)
                return reason.getStringText();
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
        Perl6LongName newLongName = Perl6ElementFactory.createRoutineName(getProject(), name);
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

    @Override
    public String getMultiness() {
        Perl6RoutineDeclStub stub = getStub();
        if (stub != null)
            return stub.getMultiness();
        PsiElement parent = getParent();
        return parent instanceof Perl6MultiDecl ? ((Perl6MultiDecl)parent).getMultiness() : "only";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:ROUTINE_DECLARATION)";
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        if (getScope().equals("unit"))
            contributeParametersOfUnit(collector);
        String name = getRoutineName();
        String scope = getScope();
        if (!name.equals("<anon>") && (scope.equals("my") || scope.equals("our"))) {
            String multiness = getMultiness();
            boolean isProto = multiness.equals("proto");
            if (isProto || multiness.equals("multi"))
                collector.offerMultiSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, this), isProto);
            else
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, this));
            if (!collector.isSatisfied())
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                                                                     this, "&" + name));
        }
    }

    private void contributeParametersOfUnit(Perl6SymbolCollector collector) {
        Perl6Parameter[] params = getParams();
        for (Perl6Parameter param : params) {
            Perl6ParameterVariable parameterVariable = PsiTreeUtil.findChildOfType(param, Perl6ParameterVariable.class);
            if (parameterVariable != null)
                parameterVariable.contributeLexicalSymbols(collector);
        }
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        String name = getRoutineName();
        String scope = getScope();
        if (!name.equals("<anon>") && scope.equals("has")) {
            String routineKind = getRoutineKind();
            boolean visible;
            boolean isPrivate = name.startsWith("!");
            if (isPrivate) {
                // Private. Checked first as in theory a private submethod could exist.
                visible = symbolsAllowed.privateMethodsVisible;
            }
            else if (routineKind.equals("submethod")) {
                // It's a submethod; only contribute if they are visible here.
                visible = symbolsAllowed.submethodsVisible;
            }
            else {
                // Normal method.
                visible = true;
            }
            if (visible) {
                String multiness = getMultiness();
                boolean isProto = multiness.equals("proto");
                Perl6ExplicitAliasedSymbol sym = new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Method, this,
                        isPrivate ? name : "." + name);
                if (isProto || multiness.equals("multi"))
                    collector.offerMultiSymbol(sym, isProto);
                else
                    collector.offerSymbol(sym);
            }
        }
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
            public Object[] getDependencies() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        for (String sym : ROUTINE_SYMBOLS) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, sym, this));
            if (collector.isSatisfied()) return;
        }
        String routineName = getRoutineName();
        String routineKind = getRoutineKind();
        if (routineKind.equals("method") || routineKind.equals("submethod")) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "%_", this));
        }
        if (Objects.equals(routineName, "MAIN")) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$*USAGE"));
        } else if (Objects.equals(routineName, "GENERATE-USAGE")) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "&*GENERATE-USAGE"));
        } else if (Objects.equals(routineName, "ARGS-TO-CAPTURE")) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "&*ARGS-TO-CAPTURE"));
        }
    }

    @Override
    public void collectPodAndDocumentables(PodDomBuildingContext context) {
        // Only include things with a name.
        String name = getName();
        if (name == null || name.isEmpty())
            return;

        // Method or sub?
        if (isMethod()) {
            // Don't include private, BUILD, TWEAK or DESTROY, as these aren't for
            // external callers.
            if (isPrivate() || name.equals("BUILD") || name.equals("TWEAK") || name.equals("DESTROY"))
                return;

            // Method. Ensure there's an enclosing class.
            PodDomClassyDeclarator enclosingClass = context.currentClassyDeclarator();
            if (enclosingClass == null)
                return;

            // Build the doc node and add it to the class.
            Perl6Type returnType = getReturnType();
            String returnTypeName = returnType instanceof Perl6Untyped ? null : returnType.getName();
            PodDomRoutineDeclarator routine = new PodDomRoutineDeclarator(getTextOffset(), name, null,
                    getDocBlocks(), getRoutineKind(), getDocParameters(), returnTypeName);
            enclosingClass.addMethod(routine);
        }
    }

    private PodDomParameterDeclarator[] getDocParameters() {
        Perl6Signature signature = getSignatureNode();
        if (signature == null)
            return new PodDomParameterDeclarator[0];
        Perl6Parameter[] parameters = signature.getParameters();
        PodDomParameterDeclarator[] result = new PodDomParameterDeclarator[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Perl6Parameter parameter = parameters[i];
            result[i] = new PodDomParameterDeclarator(parameter.getTextOffset(), parameter.getVariableName(),
                    null, parameter.getDocBlocks(), parameter.getText());
        }
        return result;
    }
}
