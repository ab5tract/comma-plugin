package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import edument.perl6idea.psi.Perl6Variable;

public abstract  class Perl6InlineVariableSettings implements InlineHandler.Settings {
    private Perl6Variable myVariable;

    public Perl6InlineVariableSettings(Perl6Variable element) {
        myVariable = element;
    }

    public Perl6Variable getVariable() {
        return myVariable;
    }
}
