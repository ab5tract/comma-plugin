package org.raku.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;
import org.raku.psi.RakuStatement;
import org.raku.psi.RakuStatementList;
import org.raku.descriptors.surrounder.*;
import org.raku.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;

public class RakuExpressionSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
        new RakuIfSurrounder(false),
        new RakuWithSurrounder(false),
        new RakuUnlessSurrounder(false),
        new RakuWithoutSurrounder(false),
        new RakuGivenSurrounder(false),
        new RakuForSurrounder(false),
        new RakuWheneverSurrounder(false),
        new RakuWhenSurrounder(false),
        new RakuTrySurrounder(false),
        new RakuTryCatchWhenSurrounder(false),
        new RakuTryCatchDefaultSurrounder(false),
        new RakuStartSurrounder(false),
        new RakuPointyBlockSurrounder(false),
        new RakuHashSurrounder(false),
        new RakuArraySurrounder(false),
        new RakuArrayContextSurrounder(false),
        new RakuHashContextSurrounder(false),
        new RakuCornerBracketsSurrounder(false)
    };

    @Override
    public PsiElement @NotNull [] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        PsiElement start = file.findElementAt(startOffset);
        PsiElement end = file.findElementAt(endOffset == 0 ? 0 : endOffset - 1);
        start = RakuPsiUtil.skipSpaces(start, true);
        end = RakuPsiUtil.skipSpaces(end, false);
        if (start == null || end == null)
            return PsiElement.EMPTY_ARRAY;

        PsiElement parent = PsiTreeUtil.findCommonParent(start, end);
        if (parent instanceof RakuRegex)
            return PsiElement.EMPTY_ARRAY;
        while (parent != null && !(parent instanceof RakuStatementList || parent instanceof RakuStatement || parent.getParent() instanceof RakuStatement)) {
            if (parent instanceof RakuExtractable)
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
