package edument.perl6idea.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.NAME;

public class Perl6PresentableStub<T extends StubElement> extends StubBasedPsiElementBase<T> {
    public Perl6PresentableStub(@NotNull T stub,
                                @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public Perl6PresentableStub(@NotNull ASTNode node) {
        super(node);
    }

    public String getSymbolName() {
        PsiElement name = findChildByType(NAME);
        PsiElement longName = findChildByType(LONG_NAME);
        return name == null ? longName == null ? "<anon>" : longName.getText() : name.getText();
    }

    @Nullable
    private String getOuterElementName() {
        StringBuilder name = new StringBuilder();
        PsiElement outer = getParent();
        while (outer != null && !(outer instanceof Perl6File)) {
            if (outer instanceof Perl6PackageDecl)
                name.insert(0, ((Perl6PackageDecl) outer).getPackageName());
            outer = outer.getParent();
        }
        return name.toString().isEmpty() ? null : name.toString();
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getSymbolName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiFile baseFile = getContainingFile();
                PsiDirectory dir = baseFile.getParent();
                String path = getContainingFile().getName().replace(".pm6", "");
                if (getOuterElementName() != null) path = String.format("%s::%s", path, getOuterElementName());
                while (dir != null && !dir.getName().equals("lib")) {
                    path = String.format("%s::%s", dir.getName(), path);
                    dir = dir.getParent();
                }
                return String.format("(in %s)", path);
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
