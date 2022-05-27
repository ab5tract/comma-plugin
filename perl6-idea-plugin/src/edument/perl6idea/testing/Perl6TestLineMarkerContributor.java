package edument.perl6idea.testing;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@InternalIgnoreDependencyViolation
public class Perl6TestLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        if (element instanceof Perl6File && Arrays.asList("t", "t6", "rakutest").contains(((Perl6File)element).getVirtualFile().getExtension()))
            return withExecutorActions(AllIcons.RunConfigurations.TestState.Run);
        return null;
    }
}
