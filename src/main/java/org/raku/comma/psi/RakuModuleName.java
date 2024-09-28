package org.raku.comma.psi;

import com.intellij.psi.PsiNamedElement;

public interface RakuModuleName extends RakuPsiElement, PsiNamedElement {

    default boolean belongsToUse() {
        return this.getParent() instanceof RakuUseStatement;
    }

    default boolean belongsToDeclaration() {
        return this.getParent() instanceof RakuPackageDecl;
    }

    default boolean isUnit() {
        if (belongsToDeclaration()) {
            var maybeUnit = this.getParent().getParent();
            return maybeUnit instanceof RakuScopedDecl && maybeUnit.getText().equals("unit");
        }
        return false;
    }
}
