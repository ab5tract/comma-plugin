package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class StubMissingMethodsFix implements IntentionAction {
    private final Perl6PackageDecl myDecl;
    private final Collection<String> myToImplement;

    public StubMissingMethodsFix(Perl6PackageDecl decl, Collection<String> implement) {
        myDecl = decl;
        myToImplement = implement;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Stub missing methods";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(myDecl, Perl6StatementList.class);
        if (list == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub methods without package body",
                                                "Stubbing role methods", null);
            return;
        }
        for (String methodDef : myToImplement) {
            Perl6Statement methodDecl = Perl6ElementFactory.createStatementFromText(project, methodDef);
            list.getNode().addChild(methodDecl.getNode());
            list.getNode().addChild(new PsiWhiteSpaceImpl("\n"));
        }
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformat(myDecl);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
