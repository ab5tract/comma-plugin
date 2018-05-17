package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.stub.Perl6FileElementType;
import edument.perl6idea.psi.stub.Perl6FileStub;
import org.jetbrains.annotations.NotNull;

public class Perl6FileStubImpl extends PsiFileStubImpl<Perl6File> implements Perl6FileStub {
    private String compilationUnitName;

    public Perl6FileStubImpl(Perl6File file, String compilationUnitName) {
        super(file);
        this.compilationUnitName = compilationUnitName;
    }

    @NotNull
    @Override
    public IStubFileElementType getType() {
        return Perl6ElementTypes.FILE;
    }

    @Override
    public String getCompilationUnitName() {
        return compilationUnitName;
    }
}
