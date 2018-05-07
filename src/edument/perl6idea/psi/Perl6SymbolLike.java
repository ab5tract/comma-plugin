package edument.perl6idea.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6SymbolLike extends ASTWrapperPsiElement {
    public Perl6SymbolLike(@NotNull ASTNode node) {
        super(node);
    }

    public String getTypeLikeName() {
        PsiElement name = findChildByType(Perl6TokenTypes.NAME);
        PsiElement longName = findChildByType(Perl6ElementTypes.LONG_NAME);
        return name == null ? longName == null ? "<anon>" : longName.getText() : name.getText();
    }

    @Override
    public String getName() { return getTypeLikeName(); }

    @Nullable
    private String getOuterElementName() {
        StringBuilder name = new StringBuilder();
        PsiElement outer = getParent();
        while (outer != null && !(outer instanceof Perl6File)) {
            if (outer instanceof Perl6PackageDecl)
                name.insert(0, ((Perl6PackageDecl) outer).getPackageName());
            outer = outer.getParent();
        }
        return name.toString().equals("") ? null : name.toString();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getTypeLikeName();
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
