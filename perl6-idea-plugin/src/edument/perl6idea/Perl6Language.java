package edument.perl6idea;

import com.intellij.lang.Language;

public class Perl6Language extends Language {
    public static final Perl6Language INSTANCE = new Perl6Language();

    private Perl6Language() {
        super("Perl6");
    }
}
