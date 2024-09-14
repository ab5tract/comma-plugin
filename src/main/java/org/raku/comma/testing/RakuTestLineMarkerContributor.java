package org.raku.comma.testing;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@InternalIgnoreDependencyViolation
public class RakuTestLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        if (element instanceof RakuFile && Arrays.asList("t", "t6", "rakutest").contains(((RakuFile)element).getVirtualFile().getExtension()))
            return withExecutorActions(AllIcons.RunConfigurations.TestState.Run);
        return null;
    }
}
