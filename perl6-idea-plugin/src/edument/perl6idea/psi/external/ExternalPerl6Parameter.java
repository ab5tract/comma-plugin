package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalPerl6Parameter extends Perl6ExternalPsiElement implements Perl6Parameter {
    private final String myName;
    private final String myType;
    static private final Pattern NAME_PATTERN = Pattern.compile("([|$@%&]\\w+)");

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
        Matcher matcher = NAME_PATTERN.matcher(myName);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return myName;
        }
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
    public boolean isRequired() {
        return myName.endsWith("!");
    }

    @Override
    public boolean isExplicitlyOptional() {
        return myName.endsWith("?");
    }

    @Override
    public boolean isOptional() {
        return isExplicitlyOptional() || !isPositional();
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

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public @NotNull Perl6Type inferType() {
        return new Perl6UnresolvedType(myType);
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {}
}
