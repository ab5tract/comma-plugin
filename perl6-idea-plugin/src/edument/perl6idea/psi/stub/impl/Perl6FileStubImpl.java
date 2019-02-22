package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.tree.IStubFileElementType;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PsiDeclaration;
import edument.perl6idea.psi.stub.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Perl6FileStubImpl extends PsiFileStubImpl<Perl6File> implements Perl6FileStub {
    private String compilationUnitName;
    private Map<Integer, List<Integer>> statementLineMap;

    public Perl6FileStubImpl(Perl6File file, String compilationUnitName, Map<Integer, List<Integer>> statementLineMap) {
        super(file);
        this.compilationUnitName = compilationUnitName;
        this.statementLineMap = statementLineMap;
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

    @Override
    public Map<Integer, List<Integer>> getStatementLineMap() {
        return statementLineMap;
    }

    @Override
    public List<Perl6PsiDeclaration> getExports() {
        List<Perl6PsiDeclaration> exports = new ArrayList<>();
        List<Stub> toTry = new ArrayList<>();
        toTry.add(this);
        while (!toTry.isEmpty()) {
            Stub current = toTry.remove(0);
            for (Stub child : current.getChildrenStubs()) {
                if (child instanceof Perl6DeclStub) {
                    Perl6DeclStub declStub = (Perl6DeclStub)child;
                    if (declStub.isExported())
                        exports.add((Perl6PsiDeclaration)declStub.getPsi());
                }
                toTry.add(child);
            }
        }
        return exports;
    }
}
