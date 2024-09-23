package org.raku.comma.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.usageView.UsageViewUtil;
import org.raku.comma.psi.impl.RakuFileImpl;

public class RakuRenameDialog extends RenameDialog {
    public RakuRenameDialog(Project project, PsiElement element, PsiElement context, Editor editor) {
        super(project, element, context, editor);
    }

    protected String getShortName() {
        return getRakuName(super.getPsiElement());
    }

    @Override
    protected String getFullName() {
        PsiElement myPsiElement = super.getPsiElement();
        String name = getRakuName(myPsiElement);
        String type = UsageViewUtil.getType(myPsiElement);
        return StringUtil.isEmpty(name)
                    ? type
                    : "%s '%s'".formatted(type, name);
    }

    private static String getRakuName(PsiElement myPsiElement) {
        PsiUtilCore.ensureValid(myPsiElement);
        return switch(myPsiElement) {
            case RakuFileImpl rakuFile        -> rakuFile.getEnclosingRakuModuleName();
            case PsiNamedElement namedElement -> namedElement.getName();
            default -> "";
        };
    }

    // TODO name suggester, somehow from context
    @Override
    public String[] getSuggestedNames() {
        return new String[]{getShortName()};
    }
}
