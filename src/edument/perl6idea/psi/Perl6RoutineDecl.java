package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;

import java.util.List;

public interface Perl6RoutineDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                          StubBasedPsiElement<Perl6RoutineDeclStub> {
    String getRoutineKind();
    String getRoutineName();
    String getSignature();
    List<String> getTraits();
    boolean isPrivateMethod();
}
