package org.raku.comma.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.RakuParameter;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.RakuWhereConstraint;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUnresolvedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalRakuParameter extends RakuExternalPsiElement implements RakuParameter {
    private final String myName;
    private List<String> myNames = Collections.emptyList();
    private final String myType;
    static private final Pattern NAME_PATTERN = Pattern.compile("([|$@%&]\\w+)");

    public ExternalRakuParameter(Project project, PsiElement parent, String name, List<Object> names, String type) {
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
    public RakuVariable[] getVariables() {
        return new RakuVariable[0];
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
    public RakuWhereConstraint getWhereConstraint() {
        return null;
    }

    @Override
    public RakuPsiElement getValueConstraint() {
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
    public boolean isRaw() {
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
    public @NotNull RakuType inferType() {
        return new RakuUnresolvedType(myType);
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {}

    @Override
    public boolean equalsParameter(RakuParameter other) {
        return false;
    }
}
