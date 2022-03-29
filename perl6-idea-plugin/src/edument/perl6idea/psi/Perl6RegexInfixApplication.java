package edument.perl6idea.psi;

public interface Perl6RegexInfixApplication extends Perl6RegexPsiElement {
    String getOperator();
    Perl6RegexAtom[][] getOperandAtomSequences();
}
