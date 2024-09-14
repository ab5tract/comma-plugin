package org.raku.intention;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler;
import org.raku.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConvertNonCapturingGroupIntoNamedIntention extends ConvertNonCapturingGroupIntention {
    @NotNull
    @Override
    PsiElement obtainReplacer(Project project, RakuRegexGroup group) {
        String regexContent = group.getText();
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(
          RakuElementFactory
                .createStatementFromText(project, String.format("/$<x>=(%s)/", regexContent.substring(1, regexContent.length() - 1))),
          RakuRegexVariable.class));
    }

    @Override
    protected void postProcess(Project project, Editor editor, PsiElement element) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            if (element instanceof RakuRegexVariable)
                ((RakuRegexVariable)element).setName("$<x>");
            return;
        }
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        VariableInplaceRenameHandler handler = new VariableInplaceRenameHandler();
        handler.doRename(element, editor, DataContext.EMPTY_CONTEXT);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert into named capture";
    }
}