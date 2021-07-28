package edument.perl6idea.surrountWith.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Regex;
import edument.perl6idea.psi.Perl6RegexAtom;
import org.jetbrains.annotations.NotNull;

public class Perl6SRegexAtomSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
        new Perl6RegexGroupSurrounder(),
        new Perl6RegexPositionalSurrounder(),
        new Perl6RegexNamedSurrounder()
    };

    @Override
    public PsiElement @NotNull [] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        PsiElement start = file.findElementAt(startOffset);
        PsiElement end = file.findElementAt(endOffset == 0 ? 0 : endOffset - 1);
        if (start == null || end == null)
            return PsiElement.EMPTY_ARRAY;

        PsiElement common = PsiTreeUtil.findCommonParent(start, end);

        while (common != null && !(common instanceof Perl6Regex))
            common = common.getParent();
        if (common == null)
            return PsiElement.EMPTY_ARRAY;

        Perl6RegexAtom atom1 = PsiTreeUtil.getParentOfType(start, Perl6RegexAtom.class);
        while (atom1 != null && !atom1.getParent().equals(common))
            atom1 = PsiTreeUtil.getParentOfType(atom1, Perl6RegexAtom.class);
        Perl6RegexAtom atom2 = PsiTreeUtil.getParentOfType(end, Perl6RegexAtom.class);
        while (atom2 != null && !atom2.getParent().equals(common))
            atom2 = PsiTreeUtil.getParentOfType(atom2, Perl6RegexAtom.class);

        if (atom1 == null || atom2 == null)
            return PsiElement.EMPTY_ARRAY;

        return PsiTreeUtil.getElementsOfRange(atom1, atom2).toArray(PsiElement.EMPTY_ARRAY);
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
