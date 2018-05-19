package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;

import java.util.List;

public interface Perl6RoutineDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                          StubBasedPsiElement<Perl6RoutineDeclStub>,
                                          Perl6SignatureHolder {
    String getRoutineKind();
    String getRoutineName();
    List<String> getTraits();
    boolean isPrivateMethod();
}
