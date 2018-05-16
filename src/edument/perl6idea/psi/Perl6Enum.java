package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6EnumStub;

public interface Perl6Enum extends Perl6PsiElement, Perl6PsiDeclaration,
                                   StubBasedPsiElement<Perl6EnumStub> {
    String getEnumName();
}
