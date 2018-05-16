package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.stub.Perl6FileStub;

public class Perl6FileStubImpl extends PsiFileStubImpl<Perl6File> implements Perl6FileStub {
    private String compilationUnitName;

    public Perl6FileStubImpl(Perl6File file, String compilationUnitName) {
        super(file);
        this.compilationUnitName = compilationUnitName;
    }

    @Override
    public String getCompilationUnitName() {
        return compilationUnitName;
    }
}
