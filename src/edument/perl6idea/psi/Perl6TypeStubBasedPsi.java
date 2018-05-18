package edument.perl6idea.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.stub.Perl6EnumStubElementType;
import edument.perl6idea.psi.stub.Perl6PackageDeclStubElementType;
import edument.perl6idea.psi.stub.Perl6SubsetStubElementType;
import edument.perl6idea.psi.stub.Perl6TypeStub;
import edument.perl6idea.psi.stub.impl.Perl6PackageDeclStubImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static edument.perl6idea.parsing.Perl6TokenTypes.NAME;

public abstract class Perl6TypeStubBasedPsi<T extends StubElement & Perl6TypeStub> extends StubBasedPsiElementBase<T>
        implements Perl6PsiDeclaration {
    public Perl6TypeStubBasedPsi(@NotNull T stub,
                                 @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public Perl6TypeStubBasedPsi(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(NAME);
    }

    @Override
    public String getName() {
        T stub = getStub();
        if (stub != null)
            return stub.getTypeName();
        return getNameIdentifier().getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public String getScope() {
        PsiElement parent = getNode().getPsi().getParent();
        return parent instanceof Perl6ScopedDecl ? ((Perl6ScopedDecl)parent).getScope() : "our";
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
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
                if (stub == null) return Perl6Icons.CAMELIA;
                IStubElementType type = stub.getStubType();
                if (type instanceof Perl6PackageDeclStubElementType) {
                    String kind = ((Perl6PackageDeclStubImpl)stub).getPackageKind();
                    switch (kind) {
                        case "module":
                            return Perl6Icons.MODULE;
                        case "class":
                            return Perl6Icons.CLASS;
                        case "role":
                            return Perl6Icons.ROLE;
                        case "grammar":
                            return Perl6Icons.GRAMMAR;
                        default:
                            return Perl6Icons.PACKAGE;
                    }
                }
                else if (type instanceof Perl6SubsetStubElementType)
                    return Perl6Icons.SUBSET;
                else if (type instanceof Perl6EnumStubElementType)
                    return Perl6Icons.ENUM;
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
