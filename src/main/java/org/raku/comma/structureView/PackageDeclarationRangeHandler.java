package org.raku.comma.structureView;

import com.intellij.codeInsight.hint.DeclarationRangeHandler;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuBlockoid;
import org.raku.comma.psi.RakuPackageDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class PackageDeclarationRangeHandler implements DeclarationRangeHandler<RakuPackageDecl> {
    @NotNull
    @Override
    public TextRange getDeclarationRange(@NotNull RakuPackageDecl container) {
        RakuBlockoid blockoid = PsiTreeUtil.getChildOfType(container, RakuBlockoid.class);
        return blockoid != null
                ? new TextRange(container.getTextOffset(), blockoid.getTextOffset())
                : container.getTextRange();
    }
}
