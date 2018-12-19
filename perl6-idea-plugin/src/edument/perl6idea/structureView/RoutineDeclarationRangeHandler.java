package edument.perl6idea.structureView;

import com.intellij.codeInsight.hint.DeclarationRangeHandler;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Blockoid;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

public class RoutineDeclarationRangeHandler implements DeclarationRangeHandler {
    @NotNull
    @Override
    public TextRange getDeclarationRange(@NotNull PsiElement container) {
        Perl6RoutineDecl routine = (Perl6RoutineDecl)container;
        Perl6Blockoid blockoid = PsiTreeUtil.getChildOfType(routine, Perl6Blockoid.class);
        return blockoid != null
               ? new TextRange(routine.getTextOffset(), blockoid.getTextOffset())
               : routine.getTextRange();
    }
}
