package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6PackageDecl;

public interface Perl6PackageDeclStub extends StubElement<Perl6PackageDecl> {
    String getPackageKind();
    String getPackageName();
    String getScope();
    String getGlobalName();
    String getLexicalName();
}
