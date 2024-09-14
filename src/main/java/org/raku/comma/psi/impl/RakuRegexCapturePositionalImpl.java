package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class RakuRegexCapturePositionalImpl extends ASTWrapperPsiElement implements RakuRegexCapturePositional {
    public RakuRegexCapturePositionalImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        // This is not very efficient, since we want to use alias in caller in RakuQuoteRegexImpl,
        // but we sacrifice some performance for code consistency
        RakuRegexDriver driver = PsiTreeUtil.getParentOfType(this, RakuRegexDriver.class);
        if (driver == null) return "";
        RakuRegex regex = PsiTreeUtil.findChildOfType(driver, RakuRegex.class, false);
        if (regex == null) return "";

        int captureCounter = 0;
        Deque<PsiElement> toWalk = new ArrayDeque<>(Arrays.asList(regex.getChildren()));
        while (!toWalk.isEmpty()) {
            PsiElement atom = toWalk.removeFirst();
            PsiElement firstChild = atom.getFirstChild();
            if (firstChild instanceof RakuRegexCapturePositional) {
                if (firstChild.equals(this))
                    return "$" + captureCounter;
                else
                    captureCounter++;
            } else if (firstChild instanceof RakuRegexGroup) {
                toWalk.addAll(Arrays.asList(firstChild.getChildren()));
            }
        }
        return "";
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean mightMatchZeroWidth() {
        return atomsMightMatchZeroWidth(PsiTreeUtil.getChildrenOfType(this, RakuRegexAtom.class));
    }
}
