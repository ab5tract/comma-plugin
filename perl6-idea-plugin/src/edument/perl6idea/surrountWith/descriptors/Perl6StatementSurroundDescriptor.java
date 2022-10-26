package edument.perl6idea.surrountWith.descriptors;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.surrountWith.descriptors.surrounder.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_TERMINATOR;

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
        new Perl6HashContextSurrounder(true)//,
        //new Perl6CustomBracketsSurrounder()
    };

    @Override
    public PsiElement @NotNull [] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        // We need to find all statements between start and end offset (including)
        // The issues here might include:
        // * Start offset might start at whitespace, so we need to skip it
        // * End offset might include newline, so we need to get previous one

        // Prepare start element
        PsiElement start = getStatementLevelElementAtOffset(file, startOffset, true);
        // Prepare end element
        PsiElement end = getStatementLevelElementAtOffset(file, endOffset, false);

        if (start == null || end == null)
            return PsiElement.EMPTY_ARRAY;

        // If we ended up with numerous statements, surround them
        if (!PsiEquivalenceUtil.areElementsEquivalent(start, end)) {
            try {
                return PsiTreeUtil.getElementsOfRange(start, end).toArray(PsiElement.EMPTY_ARRAY);
            } catch (IllegalArgumentException e) {
                return PsiElement.EMPTY_ARRAY;
            }
        }

        // If we ended up with a single statement appearing to be selected,
        // we need to differentiate if that is an expression or a single statement
        // To do this, we find elements at offsets and if their common parent
        // belongs to something that can be extracted as an expression,
        // then this surrounder can't help
        PsiElement exprStart = file.findElementAt(startOffset);
        if (exprStart == null)
            return PsiElement.EMPTY_ARRAY;

        PsiElement exprEnd = Perl6PsiUtil.skipSpaces(file.findElementAt(endOffset), false);

        // because potentially we are surrounding the last statement
        if (file.getTextLength() == endOffset || exprEnd != null && exprEnd.getNode().getElementType() == STATEMENT_TERMINATOR)
            exprEnd = file.findElementAt(endOffset == 0 ? 0 : endOffset - 1);
        if (exprEnd == null)
            return PsiElement.EMPTY_ARRAY;

        PsiElement expr = PsiTreeUtil.findCommonParent(exprStart, exprEnd);
        if (expr == null || expr instanceof Perl6Regex)
            return PsiElement.EMPTY_ARRAY;

        while (expr != null && !(expr.getParent() instanceof Perl6Statement || expr instanceof Perl6Statement || expr instanceof Perl6StatementList)) {
            if (expr instanceof P6Extractable)
                return PsiElement.EMPTY_ARRAY;
            expr = expr.getParent();
        }
        return new PsiElement[]{start};
    }

    @Nullable
    private static PsiElement getStatementLevelElementAtOffset(PsiFile file, int offset, boolean toRight) {
        PsiElement element = file.findElementAt(offset);
        if (file.getTextLength() == offset && offset > 0) {
            element = file.findElementAt(offset - 1);
        }
        while (element != null && !(element.getParent() instanceof Perl6StatementList))
            element = element.getParent();

        while (element != null && !(element instanceof Perl6Statement) && !(element instanceof Perl6Heredoc))
            element = toRight ? element.getNextSibling() : element.getPrevSibling();
        return element;
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
