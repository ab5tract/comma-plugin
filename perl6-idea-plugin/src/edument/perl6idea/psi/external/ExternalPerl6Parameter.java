package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import org.jetbrains.annotations.Nullable;

public class ExternalPerl6Parameter extends Perl6ExternalPsiElement implements Perl6Parameter {
    private final String myName;
    private final String myType;

    public ExternalPerl6Parameter(Project project, PsiElement parent, String name, String type) {
        myProject = project;
        myParent = parent;
        myName = name;
        myType = type;
    }

    @Override
    public String summary() {
        return myType + " " + myName;
    }

    @Override
    public String getVariableName() {
        return myName;
    }

    @Override
    public String getText() {
        return myName;
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        return null;
    }

    @Override
    public boolean isPositional() {
        return !myName.startsWith("*%") && !myName.startsWith(":");
    }

    @Override
    public boolean isNamed() {
        return myName.startsWith("*%") || myName.startsWith(":");
    }

    @Override
    public Perl6PsiElement getValueConstraint() {
        return null;
    }

    @Override
    public boolean isSlurpy() {
        return myName.startsWith("*") || myName.startsWith("+");
    }

    @Override
    public boolean isOptional() {
        return myName.endsWith("?") || !isPositional();
    }

    @Override
    public boolean isCopy() {
        return false;
    }

    @Override
    public boolean isRW() {
        return false;
    }

    @Override
    public String getScope() {
        return "my";
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {}
}
