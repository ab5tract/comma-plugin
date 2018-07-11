package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6NeedStatementStub;
import edument.perl6idea.psi.symbols.Perl6SymbolContributor;

import java.util.List;

public interface Perl6NeedStatement extends Perl6SymbolContributor, StubBasedPsiElement<Perl6NeedStatementStub> {
    List<String> getModuleNames();
}
