package edument.perl6idea.testing;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6TestLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        if (element instanceof Perl6File)
            return withExecutorActions(AllIcons.RunConfigurations.TestState.Run);
        return null;
    }
}
