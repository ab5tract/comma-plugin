package edument.perl6idea.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6TypeLike extends ASTWrapperPsiElement {
    public Perl6TypeLike(@NotNull ASTNode node) {
        super(node);
    }

    public String getTypeLikeName() {
        PsiElement name = findChildByType(Perl6TokenTypes.NAME);
        return name == null ? "<anon>" : name.getText();
    }

    @Override
    public ItemPresentation getPresentation() {
        System.out.println("Get presentation is called!");
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getTypeLikeName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
