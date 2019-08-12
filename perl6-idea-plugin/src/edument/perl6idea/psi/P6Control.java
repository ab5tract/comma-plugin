package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * A common interface for language constructions that usually have a topic
 * and a block, e.g. `for $ {}`, `given $ {}`, `whenever $ {}` and so on.
 */
public interface P6Control extends Perl6PsiElement {
    @Nullable
    default PsiElement getTopic() {
        PsiElement block = getLastChild();
        if (!(block instanceof Perl6Block))
            return null;
        return ((Perl6Block)block).skipWhitespacesBackward();
    }

    @Nullable
    default Perl6Blockoid getBlock() {
        PsiElement block = getLastChild();
        return block instanceof Perl6Block ? ((Perl6Block)block).getBlockoid()
                                           : block instanceof Perl6Blockoid
                                             ? (Perl6Blockoid)block
                                             : null;
    }

    default void addStatements(PsiElement[] statements) {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(getBlock(), Perl6StatementList.class);
        if (list == null)
            return;
        for (PsiElement statement : statements) {
            list.add(statement);
        }
    }
}
