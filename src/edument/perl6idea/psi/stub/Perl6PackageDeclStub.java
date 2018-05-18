package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6PackageDecl;

public interface Perl6PackageDeclStub extends Perl6TypeStub<Perl6PackageDecl> {
    String getTypeName();
    String getPackageKind();
    String getScope();
}
