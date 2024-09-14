package org.raku.comma.refactoring.inline;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.inline.InlineOptionsDialog;

public abstract class RakuInlineDialog extends InlineOptionsDialog {
    protected final PsiElement myReference;
    protected final Editor myEditor;

    protected RakuInlineDialog(Project project, PsiElement element, PsiElement reference, Editor editor) {
        super(project, true, element);
        myReference = reference;
        myEditor = editor;
    }

    @Override
    protected String getBorderTitle() {
        return "Inline";
    }

    @Override
    protected String getInlineAllText() {
        return "Inline all and remove the declaration";
    }

    @Override
    protected String getInlineThisText() {
        return "Inline this only and keep the declaration";
    }

    @Override
    protected boolean isInlineThis() {
        return false;
    }

    @Override
    public boolean isInlineThisOnly() {
        if (ApplicationManager.getApplication().isUnitTestMode()) return myReference != null;
        return super.isInlineThisOnly();
    }

    @Override
    public boolean isKeepTheDeclaration() {
        if (ApplicationManager.getApplication().isUnitTestMode()) return myReference != null;
        return super.isInlineThisOnly();
    }
}
