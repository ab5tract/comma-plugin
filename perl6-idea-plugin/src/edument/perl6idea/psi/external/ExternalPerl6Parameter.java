package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6WhereConstraint;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalPerl6Parameter extends Perl6ExternalPsiElement implements Perl6Parameter {
    private final String myName;
    private List<String> myNames = Collections.emptyList();
    private final String myType;
    static private final Pattern NAME_PATTERN = Pattern.compile("([|$@%&]\\w+)");

    public ExternalPerl6Parameter(Project project, PsiElement parent, String name, List<Object> names, String type) {
        myProject = project;
        myParent = parent;
        myName = name;
        if (names != null) {
            myNames = new ArrayList<>();
            for (Object obj : names) {
                if (obj instanceof String)
                    myNames.add((String)obj);
            }
        }
        myType = type;
    }

    @Override
    public String summary(boolean includeName) {
        return myType + " " + myName;
    }

    @Override
    public String getVariableName() {
        Matcher matcher = NAME_PATTERN.matcher(myName);
        if (matcher.find()) {
            return matcher.group(0);
        }
        else {
            return myName;
        }
    }

    @Override
    public Perl6Variable[] getVariables() {
        return new Perl6Variable[0];
    }

    @Override
    public String[] getVariableNames() {
        return myNames.size() == 0
               ? new String[]{getVariableName()}
               : ArrayUtil.toStringArray(ContainerUtil.map(myNames, s -> "$" + s));
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
    public Perl6WhereConstraint getWhereConstraint() {
        return null;
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
    public @NotNull String getScope() {
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

    @Override
    public boolean equalsParameter(Perl6Parameter other) {
        return false;
    }
}
