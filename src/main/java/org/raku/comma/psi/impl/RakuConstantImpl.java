package org.raku.comma.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuConstant;
import org.raku.comma.psi.RakuMemberStubBasedPsi;
import org.raku.comma.psi.RakuTermDefinition;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.stub.RakuConstantStub;
import org.raku.comma.psi.stub.RakuConstantStubElementType;
import org.raku.comma.psi.symbols.RakuExplicitAliasedSymbol;
import org.raku.comma.psi.symbols.RakuExplicitSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuConstantImpl extends RakuMemberStubBasedPsi<RakuConstantStub> implements RakuConstant {
    public RakuConstantImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuConstantImpl(RakuConstantStub stub, RakuConstantStubElementType type) {
        super(stub, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        RakuVariable varNode = findChildByClass(RakuVariable.class);
        return varNode != null
               ? varNode.getVariableToken()
               : findChildByClass(RakuTermDefinition.class);
    }

    @Override
    public String getName() {
        RakuConstantStub stub = this.getStub();
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
        return getClass().getSimpleName() + "(Raku:CONSTANT)";
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        String name = getName();
        if (name != null && !name.isEmpty()) {
            char sigil = name.charAt(0);
            if (sigil == '$' || sigil == '@' || sigil == '%' || sigil == '&') {
                collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.Variable, this));
                if (sigil == '&')
                    collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Routine, this,
                                                                        name.substring(1)));
            }
            else {
                collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, this));
                if (!collector.isSatisfied()) {
                    String globalName = getGlobalName();
                    if (globalName != null)
                        collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.TypeOrConstant,
                                                                            this, globalName));
                }
            }
        }
    }
}
