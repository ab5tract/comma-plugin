package org.raku.comma.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.RakuIcons;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.stub.RakuEnumStubElementType;
import org.raku.comma.psi.stub.RakuPackageDeclStubElementType;
import org.raku.comma.psi.stub.RakuSubsetStubElementType;
import org.raku.comma.psi.stub.RakuTypeStub;
import org.raku.comma.psi.stub.impl.RakuPackageDeclStubImpl;
import org.raku.comma.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.raku.comma.parsing.RakuTokenTypes.NAME;

public abstract class RakuTypeStubBasedPsi<T extends StubElement<?> & RakuTypeStub<?>> extends StubBasedPsiElementBase<T>
  implements RakuPsiDeclaration, RakuLexicalSymbolContributor {
    public RakuTypeStubBasedPsi(@NotNull T stub,
                                @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public RakuTypeStubBasedPsi(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getNameIdentifier() {
        PsiElement normalNameId = findChildByType(NAME);
        RakuLongName name = PsiTreeUtil.getChildOfType(this, RakuLongName.class);
        return normalNameId != null ? normalNameId : name != null ? name.getFirstChild() : null;
    }

    @Override
    public int getTextOffset() {
        PsiElement name = getNameIdentifier();
        if (name != null)
            return name.getTextOffset();
        PsiElement declarator = getDeclarator();
        return declarator == null ? 0 : declarator.getTextOffset();
    }

    protected PsiElement getDeclarator() {
        return findChildByType(RakuTokenTypes.PACKAGE_DECLARATOR);
    }

    @Override
    public String getName() {
        T stub = getStub();
        if (stub != null)
            return stub.getTypeName();
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public @NotNull String getScope() {
        PsiElement parent = getNode().getPsi().getParent();
        return parent instanceof RakuScopedDecl ? ((RakuScopedDecl)parent).getScope() : "our";
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                T stub = getStub();
                String globalName = stub == null ? getGlobalName() : stub.getGlobalName();
                return globalName == null ? getName() : globalName;
            }

            @Override
            public @NotNull String getLocationString() {
                // Mangle file name into module name.
                String moduleName = getEnclosingPerl6ModuleName();
                if (moduleName == null)
                    return "";

                // See if it's global.
                T stub = getStub();
                String globalName = stub == null ? getGlobalName() : stub.getGlobalName();
                if (globalName != null)
                    return "global in " + moduleName;

                // Otherwise, presumed lexical.
                return "lexical in " + moduleName;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                T stub = getStub();
                if (stub == null) return getOriginElementIcon();
                IStubElementType<?, ?> type = stub.getStubType();
                if (type instanceof RakuPackageDeclStubElementType)
                    return RakuIcons.iconForPackageDeclarator(((RakuPackageDeclStubImpl)stub).getPackageKind());
                else if (type instanceof RakuSubsetStubElementType)
                    return RakuIcons.SUBSET;
                else if (type instanceof RakuEnumStubElementType)
                    return RakuIcons.ENUM;
                return RakuIcons.CAMELIA;
            }

            private Icon getOriginElementIcon() {
                PsiElement origin = getOriginalElement();
                if (origin instanceof RakuPackageDecl)
                    return RakuIcons.iconForPackageDeclarator(((RakuPackageDecl)origin).getPackageKind());
                else if (origin instanceof RakuSubset)
                    return RakuIcons.SUBSET;
                else if (origin instanceof RakuEnum)
                    return RakuIcons.ENUM;
                return RakuIcons.CAMELIA;
            }
        };
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        if (getName() == null) return;
        collector.offerSymbol(new RakuExplicitSymbol(RakuSymbolKind.TypeOrConstant, this));
        if (!collector.isSatisfied()) {
            T stub = getStub();
            String globalName = stub == null ? getGlobalName() : stub.getGlobalName();
            if (globalName != null)
                collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.TypeOrConstant,
                                                                    this, globalName));
        }
    }
}
