package edument.perl6idea.psi;

import java.util.List;

public interface Perl6StatementList extends Perl6PsiElement {
    List<Perl6Statement> getStatements();
}
