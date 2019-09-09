package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6Constant;
import edument.perl6idea.psi.stub.Perl6ConstantStub;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExternalPerl6Constant extends Perl6ExternalPsiElement implements Perl6Constant {
    private final String myName;

    public ExternalPerl6Constant(Project project, ExternalPerl6File file, String name) {
        myProject = project;
        myParent = file;
        myName = name;
    }

    @Nullable
    @Override
    public String getConstantName() {
        return myName;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public IStubElementType getElementType() {
        return null;
    }

    @Override
    public Perl6ConstantStub getStub() {
        return null;
    }

    @Override
    public String getScope() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {}
}
