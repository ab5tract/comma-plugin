package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import edument.perl6idea.psi.Perl6File;

public interface Perl6FileStub extends PsiFileStub<Perl6File> {
    /* The name, inferred from path, that a `use` statement would be followed by
     * to resolve to this module. */
    String getCompilationUnitName();
}
