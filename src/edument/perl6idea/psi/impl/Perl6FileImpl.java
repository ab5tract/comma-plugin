package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.Processor;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PsiDeclaration;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.index.Perl6AllRoutinesStubIndex;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Perl6FileImpl extends PsiFileBase implements Perl6File {
    public Perl6FileImpl(FileViewProvider viewProvider) {
        super(viewProvider, Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Perl6ModuleFileType.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getContainingFile();
    }

    @Override
    public List<Perl6PsiDeclaration> getExports() {
        Stub stub = getStub();
        if (stub != null) {
            List<Perl6PsiDeclaration> result = new ArrayList<>();
            List<Stub> toTry = new ArrayList<>();
            toTry.add(stub);
            while (!toTry.isEmpty()) {
                Stub current = toTry.remove(0);
                List<? extends Stub> stubs = current.getChildrenStubs();
                for (Stub child : stubs) {
                    if (child instanceof Perl6RoutineDeclStub &&
                        ((Perl6RoutineDeclStub)child).isExported() &&
                        ((Perl6RoutineDeclStub)child).getRoutineKind().equals("method"))
                        result.add(((Perl6RoutineDeclStub)child).getPsi());
                    else if (child instanceof Perl6PackageDeclStub)
                        toTry.add(child);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }
}
