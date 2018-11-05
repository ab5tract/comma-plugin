package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6RegexDeclStub;

public interface Perl6RegexDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                        StubBasedPsiElement<Perl6RegexDeclStub>,
                                        Perl6SignatureHolder {
    String getRegexKind();
    String getRegexName();
}
