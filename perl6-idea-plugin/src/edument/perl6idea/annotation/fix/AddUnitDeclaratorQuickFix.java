// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.Nls;import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddUnitDeclaratorQuickFix extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        PsiElement ourParent = psiElement.getParent();
        int offset = ourParent.getTextOffset();

        new WriteCommandAction.Simple(project) {
            @Override
            protected void run() throws Throwable {
                try {
                    FileEditorManager.getInstance(project).openFile(ourParent.getContainingFile().getVirtualFile(), true);
                    final Editor editor1 = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    final Document document = editor1.getDocument();
                    document.insertString(offset, "unit ");
                }
                catch (Exception e) {
                    int bar = 1;
                }
            }
        }.execute();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        if (psiElement.getNode().getElementType() != Perl6TokenTypes.STATEMENT_TERMINATOR) {
            return false;
        }
        if (!(psiElement.getParent() instanceof Perl6PackageDecl)) {
            return false;
        }
        return true;
    }

    protected boolean checkFile(@Nullable PsiFile file) {
        if (file == null) {
            return false;
        } else {
            PsiManager manager = file.getManager();
            if (manager == null) {
                return false;
            }
            if (manager.isInProject(file)) {
                return true;
            }
            if (ScratchFileService.isInScratchRoot(file.getVirtualFile())) {
                return true;
            }
            return false;
        }
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Perl 6";
    }

    @Override
    public String getText() {
        return "Add missing 'unit' scope declaration";
    }
}
