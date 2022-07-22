package edument.perl6idea.refactoring;

import com.intellij.history.LocalHistory;
import com.intellij.history.LocalHistoryAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RakuExtractPackageHandler implements RefactoringActionHandler {
    private final boolean isRole;
    private Perl6PackageDecl myPackage;

    public RakuExtractPackageHandler(boolean isRole) {
        this.isRole = isRole;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        int offset = editor.getCaretModel().getOffset();
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        PsiElement element = file.findElementAt(offset);
        invoke(project, new PsiElement[]{element}, dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, PsiElement @NotNull [] elements, DataContext dataContext) {
        if (elements.length != 1)
            return;

        myPackage = PsiTreeUtil.getParentOfType(elements[0], Perl6PackageDecl.class);
        assert myPackage != null;

        if (!CommonRefactoringUtil.checkReadOnlyStatus(project, myPackage))
            return;

        List<RakuAttributeInfo> members = RakuExtractPackageUtil.getAllMemberInfo(myPackage, isRole);
        RakuExtractPackageDialog dialog = new RakuExtractPackageDialog(
            project, myPackage, members, isRole,
            "Extract " + (isRole ? "role" : "parent class"));
        if (!dialog.showAndGet()) {
            return;
        }

        WriteCommandAction.writeCommandAction(project)
            .withName(getRefactoringName())
            .compute(() -> {
                String newName = dialog.getExtractedSuperName();
                Collection<RakuAttributeInfo> mySelectedAttributes = dialog.getSelectedMemberInfos();
                LocalHistoryAction a = LocalHistory.getInstance().startAction(getCommandName());
                try {
                    return RakuExtractPackageUtil.extractPackage(project, myPackage, newName, isRole, mySelectedAttributes);
                }
                finally {
                    a.finish();
                }
            });
    }

    @Nls
    private String getCommandName() {
        return String.format("Extracting %s from %s...", isRole ? "role" : "parent class", myPackage.getPackageName());
    }

    public @NlsContexts.DialogTitle String getRefactoringName() {
        return "Extract " + (isRole ? "Role" : "Parent Class");
    }
}
