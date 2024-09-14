package org.raku.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.raku.highlighter.RakuElementVisitor;
import org.raku.parsing.RakuElementTypes;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.*;
import org.raku.psi.stub.RakuRegexDeclStub;
import org.raku.psi.stub.RakuRegexDeclStubElementType;
import org.raku.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuRegexDeclImpl extends RakuMemberStubBasedPsi<RakuRegexDeclStub> implements RakuRegexDecl {
    private static final String[] REGEX_SYMBOLS = { "$/", "$!", "$_", "$¢", "%_" };

    public RakuRegexDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuRegexDeclImpl(RakuRegexDeclStub stub, RakuRegexDeclStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getRegexKind() {
        PsiElement declarator = findChildByType(RakuTokenTypes.REGEX_DECLARATOR);
        return declarator == null ? "rule" : declarator.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(RakuElementTypes.LONG_NAME);
    }

    @Override
    public int getTextOffset() {
        PsiElement name = getNameIdentifier();
        return name == null ? 0 : name.getTextOffset();
    }

    @Override
    public String getName() {
        RakuRegexDeclStub stub = getStub();
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
        return parent instanceof RakuMultiDecl ? ((RakuMultiDecl)parent).getMultiness() : "only";
    }

    @Override
    public String getSignature() {
        return getRegexName() + summarySignature();
    }

    @Override
    public RakuSignature getSignatureNode() {
        return findChildByClass(RakuSignatureImpl.class);
    }

    @Override
    public String getReturnsTrait() {
        return null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        RakuLongName newLongName = RakuElementFactory.createRegexLongName(getProject(), name);
        RakuLongName longName = findChildByClass(RakuLongName.class);
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
        return getClass().getSimpleName() + "(Raku:REGEX_DECLARATION)";
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        String name = getName();
        String scope = getScope();
        if (name != null && (scope.equals("my") || scope.equals("our"))) {
            collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Regex, this));
            collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Routine, this));
            collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Variable,
                                                                this, "&" + name));
        }
    }

    @Override
    public void contributeMOPSymbols(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        String name = getName();
        String scope = getScope();
        if (name != null && scope.equals("has")) {
            collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Regex, this));
            collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Method, this, "." + name));
        }
    }

    @Override
    public void contributeScopeSymbols(RakuSymbolCollector collector) {
        for (String sym : REGEX_SYMBOLS) {
            collector.offerSymbol(new RakuImplicitSymbol(RakuSymbolKind.Variable, sym, this));
            if (collector.isSatisfied())
                return;
        }
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RakuElementVisitor) {
            ((RakuElementVisitor)visitor).visitRakuElement(this);
        } else {
            super.accept(visitor);
        }
    }
}
