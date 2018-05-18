package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

public class Perl6PackageDeclStubImpl extends StubBase<Perl6PackageDecl> implements Perl6PackageDeclStub {
    private String packageKind;
    private String packageName;

    public Perl6PackageDeclStubImpl(StubElement parent, String packageKind, String packageName) {
        super(parent, Perl6ElementTypes.PACKAGE_DECLARATION);
        this.packageKind = packageKind;
        this.packageName = packageName;
    }

    @Override
    public String getPackageKind() {
        return packageKind;
    }

    @Override
    public String getTypeName() {
        return packageName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "our";
    }
}
