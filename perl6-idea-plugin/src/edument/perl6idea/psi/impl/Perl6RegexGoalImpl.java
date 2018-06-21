package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexGoal;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexGoalImpl extends ASTWrapperPsiElement implements Perl6RegexGoal {
    public Perl6RegexGoalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
