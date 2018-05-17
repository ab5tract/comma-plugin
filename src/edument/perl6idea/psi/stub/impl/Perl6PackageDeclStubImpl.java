package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6ScopedDecl;
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
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public String getGlobalName() {
        if (packageName == null)
            return null;
        String globalName = packageName;
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub)
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return null;
            if (current instanceof Perl6PackageDeclStub)
                globalName = ((Perl6PackageDeclStub)current).getPackageName() + "::" + globalName;
            current = current.getParentStub();
        }
        return globalName;
    }

    @Override
    public String getLexicalName() {
        if (packageName == null)
            return null;
        String lexicalName = packageName;
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub) {
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return lexicalName;
                return null;
            }
            if (current instanceof Perl6PackageDeclStub)
                lexicalName = ((Perl6PackageDeclStub)current).getPackageName() + "::" + lexicalName;
            current = current.getParentStub();
        }
        return null;
    }
}
