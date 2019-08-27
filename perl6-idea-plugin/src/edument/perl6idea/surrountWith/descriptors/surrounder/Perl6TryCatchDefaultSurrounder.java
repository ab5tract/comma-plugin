package edument.perl6idea.surrountWith.descriptors.surrounder;

import edument.perl6idea.psi.Perl6Try;

public class Perl6TryCatchDefaultSurrounder extends Perl6GenericTrySurrounder<Perl6Try> {
    public Perl6TryCatchDefaultSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected String createBranch() {
        return "default {}";
    }

    @Override
    public String getTemplateDescription() {
        return "try { CATCH { default } }";
    }
}
