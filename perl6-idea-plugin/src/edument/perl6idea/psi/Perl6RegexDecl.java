package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6RegexDeclStub;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6MOPSymbolContributor;

public interface Perl6RegexDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                        StubBasedPsiElement<Perl6RegexDeclStub>,
                                        Perl6SignatureHolder, Perl6LexicalSymbolContributor,
                                        Perl6MOPSymbolContributor {
    String getRegexKind();
    String getRegexName();
    @Override
    String getMultiness();
}
