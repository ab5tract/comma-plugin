package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.psi.type.Perl6ParametricType;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static edument.perl6idea.parsing.Perl6ElementTypes.TYPE_NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.PARAMETER_SEPARATOR;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6ParameterVariableImpl extends ASTWrapperPsiElement implements Perl6ParameterVariable {
    public Perl6ParameterVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.getChildOfType(this, Perl6Variable.class);
    }

    @NotNull
    @Override
    public String getName() {
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Nullable
    @Override
    public String getDocsString() {
        StringBuilder builder = new StringBuilder();
        Perl6Parameter parameter = PsiTreeUtil.getParentOfType(this, Perl6Parameter.class);
        if (parameter == null) return null;
        PsiElement temp = parameter.getPrevSibling();
        Perl6Documented.gatherInlineComments(temp, false, builder);
        builder.append("<br>");
        temp = parameter.getNextSibling();
        while (temp != null && (temp.getNode().getElementType() == PARAMETER_SEPARATOR ||
                                temp.getNode().getElementType() == UNV_WHITE_SPACE ||
                                temp instanceof PsiWhiteSpace))
            temp = temp.getNextSibling();
        Perl6Documented.gatherInlineComments(temp, true, builder);
        return builder.toString().trim();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        Perl6RoutineDecl parent = PsiTreeUtil.getParentOfType(this, Perl6RoutineDecl.class);
        if (parent == null)
            return super.getUseScope();
        if (Objects.equals(parent.getScope(), "unit"))
            return new LocalSearchScope(parent.getContainingFile(), parent.getContainingFile().getName());
        return new LocalSearchScope(parent, getName());
    }

    @Override
    public String getScope() {
        return getName().contains("!") ? "has" : "my";
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        String variableName = getName();
        char sigil = Perl6Variable.getSigil(variableName);
        char twigil = Perl6Variable.getTwigil(variableName);
        String prefix = twigil != ' ' ?
                        String.valueOf(sigil) + String.valueOf(twigil) :
                        String.valueOf(sigil);
        Perl6Variable variable =
            Perl6ElementFactory.createVariable(getProject(), prefix + name);
        PsiElement keyNode = findChildByFilter(TokenSet.create(Perl6TokenTypes.VARIABLE));
        if (keyNode != null) {
            ASTNode newKeyNode = variable.getNode();
            getNode().replaceChild(keyNode.getNode(), newKeyNode);
        }
        return this;
    }

    @Override
    public String summary() {
        String sigil = getSigil();
        PsiElement defaultValue = PsiTreeUtil.getNextSiblingOfType(this, Perl6ParameterDefault.class);
        if (defaultValue != null)
            sigil += '?';
        return sigil;
    }

    @NotNull
    public String getSigil() {
        String name = getName();
        return name.length() == 0 ? "$" : String.valueOf(name.charAt(0));
    }

    @Override
    public @NotNull Perl6Type inferType() {
        Perl6Type sigilType = inferBySigil();
        PsiElement type = PsiTreeUtil .findSiblingBackward(this, TYPE_NAME, null);
        if (type instanceof Perl6TypeName) {
            return sigilType == null
                   ? ((Perl6TypeName) type).inferType()
                   : new Perl6ParametricType(sigilType, new Perl6Type[] { ((Perl6TypeName) type).inferType() });
        }
        return sigilType != null ? sigilType : Perl6Untyped.INSTANCE;
    }

    private @Nullable Perl6Type inferBySigil() {
        String sigil = getSigil();
        if (sigil.equals("@"))
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.List);
        else if (sigil.equals("%"))
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Map);
        else if (sigil.equals("&"))
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.Callable);
        return null;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        String name = getName();
        if (name.length() > 1) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, this));
            if (!collector.isSatisfied() && name.startsWith("&") && getScope().equals("my"))
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Routine,
                    this, name.substring(1)));
        }
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        String desigilname = getName();
        PsiElement decl = this;
        // Chop off sigil, if it's not sigil-only name
        if (desigilname.length() > 1)
            desigilname = desigilname.substring(1);
        // Chop off twigil if any
        if (desigilname.length() >= 2 && !Character.isLetter(desigilname.charAt(0)))
            desigilname = desigilname.substring(1);
        String finaldesigilname = desigilname;
        return new PsiMetaData() {
            @Override
            public PsiElement getDeclaration() {
                return decl;
            }

            @Override
            public String getName(PsiElement context) {
                return finaldesigilname;
            }

            @Override
            public String getName() {
                return finaldesigilname;
            }

            @Override
            public void init(PsiElement element) {
            }

            @NotNull
            @Override
            public Object[] getDependencies() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }
}
