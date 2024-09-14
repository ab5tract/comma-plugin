package org.raku.comma.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuRegex;
import org.raku.comma.psi.RakuRegexAtom;
import org.jetbrains.annotations.NotNull;

public class RakuSRegexAtomSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
        new RakuRegexGroupSurrounder(),
        new RakuRegexPositionalSurrounder(),
        new RakuRegexNamedSurrounder()
    };

    @Override
    public PsiElement @NotNull [] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        PsiElement start = file.findElementAt(startOffset);
        PsiElement end = file.findElementAt(endOffset == 0 ? 0 : endOffset - 1);
        if (start == null || end == null)
            return PsiElement.EMPTY_ARRAY;

        PsiElement common = PsiTreeUtil.findCommonParent(start, end);

        while (common != null && !(common instanceof RakuRegex))
            common = common.getParent();
        if (common == null)
            return PsiElement.EMPTY_ARRAY;

        RakuRegexAtom atom1 = PsiTreeUtil.getParentOfType(start, RakuRegexAtom.class);
        while (atom1 != null && !atom1.getParent().equals(common))
            atom1 = PsiTreeUtil.getParentOfType(atom1, RakuRegexAtom.class);
        RakuRegexAtom atom2 = PsiTreeUtil.getParentOfType(end, RakuRegexAtom.class);
        while (atom2 != null && !atom2.getParent().equals(common))
            atom2 = PsiTreeUtil.getParentOfType(atom2, RakuRegexAtom.class);

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
