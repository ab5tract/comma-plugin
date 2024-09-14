package org.raku.comma.codeInsights;

import com.intellij.codeInsight.hints.VcsCodeVisionLanguageContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuRoutineDecl;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

public class RakuVcsCodeVisionLanguageContext implements VcsCodeVisionLanguageContext {
    @Override
    public boolean isAccepted(@NotNull PsiElement element) {
        return element instanceof RakuRoutineDecl || element instanceof RakuPackageDecl;
    }

    @Override
    public void handleClick(@NotNull MouseEvent mouseEvent, @NotNull Editor editor, @NotNull PsiElement element) {

    }
}
