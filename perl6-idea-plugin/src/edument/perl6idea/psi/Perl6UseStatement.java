package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.symbols.Perl6SymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface Perl6UseStatement extends Perl6SymbolContributor, StubBasedPsiElement<Perl6UseStatementStub> {
    @Nullable
    String getModuleName();
}
