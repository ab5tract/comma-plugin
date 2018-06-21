package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.symbols.Perl6SymbolContributor;

public interface Perl6UseStatement extends Perl6SymbolContributor, StubBasedPsiElement<Perl6UseStatementStub> {
    String getModuleName();
}
