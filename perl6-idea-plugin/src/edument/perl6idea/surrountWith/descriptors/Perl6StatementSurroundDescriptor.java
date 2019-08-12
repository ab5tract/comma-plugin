package edument.perl6idea.surrountWith.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Heredoc;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import edument.perl6idea.surrountWith.descriptors.surrounder.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Perl6StatementSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
        new Perl6IfSurrounder(true),
        new Perl6WithSurrounder(true),
        new Perl6UnlessSurrounder(true),
        new Perl6WithoutSurrounder(true),
        new Perl6GivenSurrounder(true),
        new Perl6ForSurrounder(true),
        new Perl6WheneverSurrounder(true),
        new Perl6WhenSurrounder(true),
        new Perl6TrySurrounder(true),
        new Perl6TryCatchWhenSurrounder(true),
        new Perl6TryCatchDefaultSurrounder(true),
        new Perl6StartSurrounder(true),
        new Perl6PointyBlockSurrounder(true),
        new Perl6HashSurrounder(true),
        new Perl6ArraySurrounder(true),
        new Perl6ArrayContextSurrounder(true),
        new Perl6HashContextSurrounder(true)
    };

    @NotNull
    @Override
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        // We need to find all statements between start and end offset (including)
        // The issues here might include:
        // * Start offset might start at whitespace, so we need to skip it
        // * End offset might include newline, so we need to get previous one

        // Prepare start element
        PsiElement start = getStatementListItemByOffset(file, startOffset);
        if (!(start instanceof Perl6Statement)) {
            PsiElement originalStart = start;
            start = PsiTreeUtil.getNextSiblingOfType(originalStart, Perl6Heredoc.class);
            if (start == null)
                start = PsiTreeUtil.getNextSiblingOfType(originalStart, Perl6Statement.class);
        }
        // Prepare end element
        PsiElement end = getStatementListItemByOffset(file, endOffset);
        if (!(end instanceof Perl6Statement) && !(end instanceof Perl6Heredoc)) {
            PsiElement originalEnd = end;
            end = PsiTreeUtil.getPrevSiblingOfType(originalEnd, Perl6Heredoc.class);
            if (end == null)
                end = PsiTreeUtil.getPrevSiblingOfType(originalEnd, Perl6Statement.class);
        }

        if (start == null || end == null)
            return PsiElement.EMPTY_ARRAY;

        if (start.equals(end)) {
            return new PsiElement[]{start};
        } else {
            List<PsiElement> statementList = PsiTreeUtil.getElementsOfRange(start, end);
            return statementList.toArray(PsiElement.EMPTY_ARRAY);
        }
    }

    @Nullable
    private static PsiElement getStatementListItemByOffset(PsiFile file, int offset) {
        PsiElement element = file.findElementAt(offset);
        if (file.getTextLength() == offset && offset > 0) {
            element = file.findElementAt(offset - 1);
        }
        while (element != null && element.getParent() != null && !(element.getParent() instanceof Perl6StatementList))
            element = element.getParent();
        return element;
    }

    @NotNull
    @Override
    public Surrounder[] getSurrounders() {
        return SURROUNDERS;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
