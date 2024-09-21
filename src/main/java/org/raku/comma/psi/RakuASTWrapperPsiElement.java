package org.raku.comma.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.utils.CommaProjectUtil;

/* Reduce the cost of this lookup so that it isn't happening more than once per element */

public class RakuASTWrapperPsiElement extends ASTWrapperPsiElement {
    public RakuASTWrapperPsiElement(ASTNode node) { super(node); }

    private Boolean withinRakudoCoreProject;
    public boolean isWithinRakudoCoreProject() {
        if (withinRakudoCoreProject == null) {
            withinRakudoCoreProject = CommaProjectUtil.isRakudoCoreProject(this.getProject());
        }
        return withinRakudoCoreProject;
    }
}
