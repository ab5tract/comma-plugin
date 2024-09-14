package org.raku.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuPackageDecl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuTokenTypes.PACKAGE_DECLARATOR;

public class RemoveUnitDeclaratorQuickFix implements IntentionAction {
    private final int startOffset;
    private int endOffset = -1;
    private final RakuPackageDecl packageElement;

    public RemoveUnitDeclaratorQuickFix(int unitKeywordOffset, RakuPackageDecl element) {
        packageElement = element;
        startOffset = unitKeywordOffset;
        PsiElement child = packageElement.getFirstChild();
        while (child != null) {
            if (child.getNode().getElementType() == PACKAGE_DECLARATOR) {
                endOffset = child.getTextOffset();
                break;
            }
            child = child.getNextSibling();
        }
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return String.format("Remove 'unit' declarator from defined %s", packageElement.getPackageKind());
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return endOffset != -1;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        editor.getDocument().deleteString(startOffset, endOffset);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
