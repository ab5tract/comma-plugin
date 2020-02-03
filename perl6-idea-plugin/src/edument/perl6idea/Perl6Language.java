package edument.perl6idea;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class Perl6Language extends Language {
    public static final Perl6Language INSTANCE = new Perl6Language();

    private Perl6Language() {
        super("Perl6");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Raku";
    }
}
