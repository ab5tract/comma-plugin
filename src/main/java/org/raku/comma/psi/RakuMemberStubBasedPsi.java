package org.raku.comma.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.RakuIcons;
import org.raku.comma.psi.stub.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class RakuMemberStubBasedPsi<T extends StubElement<?>> extends StubBasedPsiElementBase<T>
  implements RakuPsiDeclaration {
    public RakuMemberStubBasedPsi(@NotNull T stub,
                                  @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public RakuMemberStubBasedPsi(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String getScope() {
        T stub = getStub();
        if (stub instanceof RakuDeclStub)
            return ((RakuDeclStub<?>)stub).getScope();
        PsiElement parent = getParent();
        return parent instanceof RakuScopedDecl ? ((RakuScopedDecl)parent).getScope() : defaultScope();
    }

    public abstract String defaultScope();

    public String presentableName() {
        return getName();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                String displayName = presentableName();
                if (displayName == null)
                    return "<anon>";
                if (getScope().equals("our")) {
                    RakuPackageDecl pkg = getStubOrPsiParentOfType(RakuPackageDecl.class);
                    if (pkg != null) {
                        RakuPackageDeclStub stub = pkg.getStub();
                        String globalName = stub != null ? stub.getGlobalName() : pkg.getGlobalName();
                        if (globalName != null)
                            displayName = globalName + "::" + displayName;
                    }
                }
                return displayName;
            }

            @Nullable
            @Override
            public String getLocationString() {
                switch (getScope()) {
                    case "my": {
                        String encName = enclosingPackage();
                        return encName == null ? "" : "lexical in " + encName;
                    }
                    case "our": {
                        String name = getEnclosingRakuModuleName();
                        return name == null ? "" : "global in " + name;
                    }
                    case "has": {
                        String encName = enclosingPackage();
                        return encName == null ? "" : "in " + encName;
                    }
                    default:
                        return getEnclosingRakuModuleName();
                }
            }

            private String enclosingPackage() {
                RakuPackageDecl pkg = getStubOrPsiParentOfType(RakuPackageDecl.class);
                if (pkg == null) {
                    String moduleName = getEnclosingRakuModuleName();
                    return moduleName == null ? getContainingFile().getVirtualFile().getName() : moduleName;
                }
                RakuPackageDeclStub stub = pkg.getStub();
                String globalName = stub != null ? stub.getGlobalName() : pkg.getGlobalName();
                return globalName == null ? pkg.getName() : globalName;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                T stub = getStub();
                if (stub == null) return getOriginElementIcon();
                IStubElementType<?, ?> type = stub.getStubType();
                if (type instanceof RakuRoutineDeclStubElementType) {
                    if (getScope().equals("has"))
                        return RakuIcons.METHOD;
                    return RakuIcons.SUB;
                }
                else if (type instanceof RakuRegexDeclStubElementType)
                    return RakuIcons.REGEX;
                else if (type instanceof RakuConstantStubElementType)
                    return RakuIcons.CONSTANT;
                else if (type instanceof RakuVariableDeclStubElementType)
                    return RakuIcons.ATTRIBUTE;
                return RakuIcons.CAMELIA;
            }

            private Icon getOriginElementIcon() {
                PsiElement origin = getOriginalElement();
                if (origin instanceof RakuRoutineDecl &&
                    ((RakuRoutineDecl)origin).getRoutineKind().equals("method"))
                    return RakuIcons.METHOD;
                else if (origin instanceof RakuRoutineDecl)
                    return RakuIcons.SUB;
                else if (origin instanceof RakuRegexDecl)
                    return RakuIcons.REGEX;
                else if (origin instanceof RakuConstant)
                    return RakuIcons.CONSTANT;
                else if (origin instanceof RakuVariableDecl)
                    return RakuIcons.ATTRIBUTE;
                return RakuIcons.CAMELIA;
            }
        };
    }
}
