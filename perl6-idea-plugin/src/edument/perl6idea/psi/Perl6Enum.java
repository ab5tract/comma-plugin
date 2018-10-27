package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;

import java.util.List;

public interface Perl6Enum extends Perl6PsiDeclaration, StubBasedPsiElement<Perl6EnumStub>,
                                   Perl6IndexableType {
    String getEnumName();
    List<String> getEnumValues();
}
