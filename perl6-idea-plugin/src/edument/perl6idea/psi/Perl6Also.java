package edument.perl6idea.psi;

import edument.perl6idea.psi.symbols.Perl6MOPSymbolContributor;

public interface Perl6Also extends Perl6PsiElement, Perl6MOPSymbolContributor {
    Perl6Trait getTrait();
}
