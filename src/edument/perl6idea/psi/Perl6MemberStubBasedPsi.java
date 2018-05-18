package edument.perl6idea.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class Perl6MemberStubBasedPsi<T extends StubElement> extends StubBasedPsiElementBase<T>
        implements Perl6PsiDeclaration {
    public Perl6MemberStubBasedPsi(@NotNull T stub,
                                 @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public Perl6MemberStubBasedPsi(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getScope() {
        PsiElement parent = getParent();
        return parent instanceof Perl6ScopedDecl ? ((Perl6ScopedDecl)parent).getScope() : defaultScope();
    }

    public abstract String defaultScope();

    public String presentableName() {
        return getName();
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return presentableName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                switch (getScope()) {
                    case "my":
                        return "lexical in " + enclosingPackage();
                    case "our":
                        return "global in " + enclosingPackage();
                    case "has":
                        return "in " + enclosingPackage();
                    default:
                        return getEnclosingPerl6ModuleName();
                }
            }

            private String enclosingPackage() {
                Perl6PackageDecl pkg = getStubOrPsiParentOfType(Perl6PackageDecl.class);
                if (pkg == null)
                    return getEnclosingPerl6ModuleName();
                Perl6PackageDeclStub stub = pkg.getStub();
                String globalName = stub != null ? stub.getGlobalName() : pkg.getGlobalName();
                return globalName == null ? pkg.getName() : globalName;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
