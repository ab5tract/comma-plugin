package edument.perl6idea.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.Perl6Icons;
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
                return getEnclosingPerl6ModuleName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
