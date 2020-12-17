package edument.perl6idea.psi;

import org.jetbrains.annotations.Nullable;

public interface Perl6Deprecatable {
    boolean isDeprecated();

    @Nullable
    String getDeprecationMessage();
}
