package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.highlighter.RakuElementVisitor;
import edument.perl6idea.highlighter.RakuHighlightVisitor;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6RegexDeclStub;
import edument.perl6idea.psi.stub.Perl6RegexDeclStubElementType;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6RegexDeclImpl extends Perl6MemberStubBasedPsi<Perl6RegexDeclStub> implements Perl6RegexDecl {
    private static final String[] REGEX_SYMBOLS = { "$/", "$!", "$_", "$¢", "%_" };

    public Perl6RegexDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6RegexDeclImpl(Perl6RegexDeclStub stub, Perl6RegexDeclStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getRegexKind() {
        PsiElement declarator = findChildByType(Perl6TokenTypes.REGEX_DECLARATOR);
        return declarator == null ? "rule" : declarator.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(Perl6ElementTypes.LONG_NAME);
    }

    @Override
    public int getTextOffset() {
        PsiElement name = getNameIdentifier();
        return name == null ? 0 : name.getTextOffset();
    }

    @Override
    public String getName() {
        Perl6RegexDeclStub stub = getStub();
        if (stub != null)
            return stub.getRegexName();
        PsiElement name = getNameIdentifier();
        return name == null ? null : name.getText();
    }

    @Override
    public String getRegexName() {
        String name = getName();
        return name == null ? "<anon>" : name;
    }

    @Override
    public String getMultiness() {
        // TODO copy-paste stub-implementation?
        PsiElement parent = getParent();
        return parent instanceof Perl6MultiDecl ? ((Perl6MultiDecl)parent).getMultiness() : "only";
    }

    @Override
    public String getSignature() {
        return getRegexName() + summarySignature();
    }

    @Override
    public Perl6Signature getSignatureNode() {
        return findChildByClass(Perl6SignatureImpl.class);
    }

    @Override
    public String getReturnsTrait() {
        return null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6LongName newLongName = Perl6ElementFactory.createRegexLongName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = newLongName.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }

    @Override
    public String presentableName() {
        return getName() + summarySignature();
    }

    @Override
    public String defaultScope() {
        return "has";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:REGEX_DECLARATION)";
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        String name = getName();
        String scope = getScope();
        if (name != null && (scope.equals("my") || scope.equals("our"))) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Regex, this));
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, this));
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                this, "&" + name));
        }
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        String name = getName();
        String scope = getScope();
        if (name != null && scope.equals("has")) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Regex, this));
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Method, this, "." + name));
        }
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        for (String sym : REGEX_SYMBOLS) {
            collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, sym, this));
            if (collector.isSatisfied())
                return;
        }
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RakuElementVisitor) {
            ((RakuElementVisitor)visitor).visitRegex(this);
        } else {
            super.accept(visitor);
        }
    }
}
