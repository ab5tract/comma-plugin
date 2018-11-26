package edument.perl6idea.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.usageView.UsageViewUtil;
import edument.perl6idea.psi.impl.Perl6FileImpl;

public class Perl6RenameDialog extends RenameDialog {
    public Perl6RenameDialog(Project project, PsiElement element, PsiElement context, Editor editor) {
        super(project, element, context, editor);
    }

    protected String getShortName() {
        return getPerl6Name(super.getPsiElement());
    }

    @Override
    protected String getFullName() {
        PsiElement myPsiElement = super.getPsiElement();
        String name = getPerl6Name(myPsiElement);
        String type = UsageViewUtil.getType(myPsiElement);
        return StringUtil.isEmpty(name) ? type : type + " '" + name + "'";
    }

    private static String getPerl6Name(PsiElement myPsiElement) {
        PsiUtilCore.ensureValid(myPsiElement);
        if (myPsiElement instanceof Perl6FileImpl)
            return ((Perl6FileImpl)myPsiElement).getEnclosingPerl6ModuleName();
        else if (myPsiElement instanceof PsiNamedElement)
            return ((PsiNamedElement)myPsiElement).getName();
        else
            return "";
    }

    @Override
    public String[] getSuggestedNames() {
        return new String[]{getShortName()};
    }
}
