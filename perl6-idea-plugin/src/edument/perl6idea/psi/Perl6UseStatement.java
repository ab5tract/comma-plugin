package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface Perl6UseStatement extends Perl6LexicalSymbolContributor, StubBasedPsiElement<Perl6UseStatementStub> {
    @Nullable
    String getModuleName();
}
