package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PsiDeclaration;

import java.util.List;
import java.util.Map;

public interface Perl6FileStub extends PsiFileStub<Perl6File> {
    /* The name, inferred from path, that a `use` statement would be followed by
     * to resolve to this module. */
    String getCompilationUnitName();

    /* Locates everything that is exported and returns the matching PSI
     * elements. */
    List<Perl6PsiDeclaration> getExports();

    /* Gets the statement line map, used for coverage. */
    Map<Integer, List<Integer>> getStatementLineMap();
}
