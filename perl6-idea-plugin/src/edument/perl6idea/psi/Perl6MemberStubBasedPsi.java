package edument.perl6idea.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.stub.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class Perl6MemberStubBasedPsi<T extends StubElement<?>> extends StubBasedPsiElementBase<T>
        implements Perl6PsiDeclaration {
    public Perl6MemberStubBasedPsi(@NotNull T stub,
                                 @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public Perl6MemberStubBasedPsi(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String getScope() {
        T stub = getStub();
        if (stub instanceof Perl6DeclStub)
            return ((Perl6DeclStub<?>)stub).getScope();
        PsiElement parent = getParent();
        return parent instanceof Perl6ScopedDecl ? ((Perl6ScopedDecl)parent).getScope() : defaultScope();
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
                    Perl6PackageDecl pkg = getStubOrPsiParentOfType(Perl6PackageDecl.class);
                    if (pkg != null) {
                        Perl6PackageDeclStub stub = pkg.getStub();
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
                        String name = getEnclosingPerl6ModuleName();
                        return name == null ? "" : "global in " + name;
                    }
                    case "has": {
                        String encName = enclosingPackage();
                        return encName == null ? "" : "in " + encName;
                    }
                    default:
                        return getEnclosingPerl6ModuleName();
                }
            }

            private String enclosingPackage() {
                Perl6PackageDecl pkg = getStubOrPsiParentOfType(Perl6PackageDecl.class);
                if (pkg == null) {
                    String moduleName = getEnclosingPerl6ModuleName();
                    return moduleName == null ? getContainingFile().getVirtualFile().getName() : moduleName;
                }
                Perl6PackageDeclStub stub = pkg.getStub();
                String globalName = stub != null ? stub.getGlobalName() : pkg.getGlobalName();
                return globalName == null ? pkg.getName() : globalName;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                T stub = getStub();
                if (stub == null) return getOriginElementIcon();
                IStubElementType<?, ?> type = stub.getStubType();
                if (type instanceof Perl6RoutineDeclStubElementType) {
                    if (getScope().equals("has"))
                        return Perl6Icons.METHOD;
                    return Perl6Icons.SUB;
                }
                else if (type instanceof Perl6RegexDeclStubElementType)
                    return Perl6Icons.REGEX;
                else if (type instanceof Perl6ConstantStubElementType)
                    return Perl6Icons.CONSTANT;
                else if (type instanceof Perl6VariableDeclStubElementType)
                    return Perl6Icons.ATTRIBUTE;
                return Perl6Icons.CAMELIA;
            }

            private Icon getOriginElementIcon() {
                PsiElement origin = getOriginalElement();
                if (origin instanceof Perl6RoutineDecl &&
                    ((Perl6RoutineDecl)origin).getRoutineKind().equals("method"))
                    return Perl6Icons.METHOD;
                else if (origin instanceof Perl6RoutineDecl)
                    return Perl6Icons.SUB;
                else if (origin instanceof Perl6RegexDecl)
                    return Perl6Icons.REGEX;
                else if (origin instanceof Perl6Constant)
                    return Perl6Icons.CONSTANT;
                else if (origin instanceof Perl6VariableDecl)
                    return Perl6Icons.ATTRIBUTE;
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
