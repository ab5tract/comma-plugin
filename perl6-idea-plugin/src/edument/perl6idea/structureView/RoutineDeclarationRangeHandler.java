package edument.perl6idea.structureView;

import com.intellij.codeInsight.hint.DeclarationRangeHandler;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Blockoid;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

public class RoutineDeclarationRangeHandler implements DeclarationRangeHandler<Perl6RoutineDecl> {
    @NotNull
    @Override
    public TextRange getDeclarationRange(@NotNull Perl6RoutineDecl container) {
        Perl6Blockoid blockoid = PsiTreeUtil.getChildOfType(container, Perl6Blockoid.class);
        return blockoid != null
               ? new TextRange(container.getTextOffset(), blockoid.getTextOffset())
               : container.getTextRange();
    }
}
