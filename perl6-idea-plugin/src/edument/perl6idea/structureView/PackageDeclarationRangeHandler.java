package edument.perl6idea.structureView;

import com.intellij.codeInsight.hint.DeclarationRangeHandler;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Blockoid;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class PackageDeclarationRangeHandler implements DeclarationRangeHandler<Perl6PackageDecl> {
    @NotNull
    @Override
    public TextRange getDeclarationRange(@NotNull Perl6PackageDecl container) {
        Perl6Blockoid blockoid = PsiTreeUtil.getChildOfType(container, Perl6Blockoid.class);
        return blockoid != null
                ? new TextRange(container.getTextOffset(), blockoid.getTextOffset())
                : container.getTextRange();
    }
}
