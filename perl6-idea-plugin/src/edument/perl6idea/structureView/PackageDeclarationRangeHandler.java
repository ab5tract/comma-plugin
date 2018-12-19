package edument.perl6idea.structureView;

import com.intellij.codeInsight.hint.DeclarationRangeHandler;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Blockoid;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.NotNull;

public class PackageDeclarationRangeHandler implements DeclarationRangeHandler {
    @NotNull
    @Override
    public TextRange getDeclarationRange(@NotNull PsiElement container) {
        Perl6PackageDecl pkg = (Perl6PackageDecl)container;
        Perl6Blockoid blockoid = PsiTreeUtil.getChildOfType(pkg, Perl6Blockoid.class);
        return blockoid != null
                ? new TextRange(pkg.getTextOffset(), blockoid.getTextOffset())
                : pkg.getTextRange();
    }
}
