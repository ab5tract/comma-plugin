package edument.perl6idea.surrountWith.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.P6Extractable;
import edument.perl6idea.psi.Perl6Regex;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import edument.perl6idea.surrountWith.descriptors.surrounder.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

public class Perl6ExpressionSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
        new Perl6IfSurrounder(false),
        new Perl6WithSurrounder(false),
        new Perl6UnlessSurrounder(false),
        new Perl6WithoutSurrounder(false),
        new Perl6GivenSurrounder(false),
        new Perl6ForSurrounder(false),
        new Perl6WheneverSurrounder(false),
        new Perl6WhenSurrounder(false),
        new Perl6TrySurrounder(false),
        new Perl6TryCatchWhenSurrounder(false),
        new Perl6TryCatchDefaultSurrounder(false),
        new Perl6StartSurrounder(false),
        new Perl6PointyBlockSurrounder(false),
        new Perl6HashSurrounder(false),
        new Perl6ArraySurrounder(false),
        new Perl6ArrayContextSurrounder(false),
        new Perl6HashContextSurrounder(false)
    };

    @Override
    public PsiElement @NotNull [] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        PsiElement start = file.findElementAt(startOffset);
        PsiElement end = file.findElementAt(endOffset == 0 ? 0 : endOffset - 1);
        start = Perl6PsiUtil.skipSpaces(start, true);
        end = Perl6PsiUtil.skipSpaces(end, false);
        if (start == null || end == null)
            return PsiElement.EMPTY_ARRAY;

        PsiElement parent = PsiTreeUtil.findCommonParent(start, end);
        if (parent instanceof Perl6Regex)
            return PsiElement.EMPTY_ARRAY;
        while (parent != null && !(parent instanceof Perl6StatementList || parent instanceof Perl6Statement || parent.getParent() instanceof Perl6Statement)) {
            if (parent instanceof P6Extractable)
                return new PsiElement[]{parent};
            parent = parent.getParent();
        }
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public Surrounder @NotNull [] getSurrounders() {
        return SURROUNDERS;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
