package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import edument.perl6idea.psi.type.Perl6Untyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.PARAMETER_QUANTIFIER;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6ParameterImpl extends ASTWrapperPsiElement implements Perl6Parameter {
    public Perl6ParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary(boolean includeName) {
        String maybeSignature = yieldSignature();
        if (maybeSignature != null)
            return maybeSignature;

        if (findChildByClass(Perl6ValueConstraint.class) != null)
            return "$";

        StringBuilder summary = new StringBuilder();
        Perl6TypeNameImpl maybeType = findChildByClass(Perl6TypeNameImpl.class);
        if (maybeType != null)
            summary.append(maybeType.getText()).append(" ");
        PsiElement maybeSlurpy = getFirstChild();
        if (maybeSlurpy.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeSlurpy.getText());

        Perl6NamedParameterImpl maybeNamed = findChildByClass(Perl6NamedParameterImpl.class);
        if (maybeNamed != null) summary.append(maybeNamed.summary());

        Perl6ParameterVariableImpl maybeNormal = findChildByClass(Perl6ParameterVariableImpl.class);
        if (maybeNormal != null) summary.append(maybeNormal.summary(includeName));

        Perl6TermDefinitionImpl term = findChildByClass(Perl6TermDefinitionImpl.class);
        if (term != null && term.getPrevSibling().getText().equals("\\"))
            summary.append(term.getText());

        PsiElement maybeQuant = getLastChild();
        while (maybeQuant != null && (
            maybeQuant instanceof Perl6Trait ||
            maybeQuant instanceof PsiWhiteSpace || maybeQuant.getNode().getElementType() == UNV_WHITE_SPACE)) {
            maybeQuant = maybeQuant.getPrevSibling();
        }
        if (maybeQuant != null && maybeQuant.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeQuant.getText());

        return summary.toString();
    }

    @Nullable
    private String yieldSignature() {
        Perl6SignatureImpl maybeSig = findChildByClass(Perl6SignatureImpl.class);
        if (maybeSig != null) {
            if (getFirstChild() instanceof Perl6NamedParameter)
                return ":$";
            if (maybeSig.getText().charAt(0) == '(')
                return "$";
            if (maybeSig.getText().charAt(0) == '[')
                return "@";
        }
        return null;
    }

    @Override
    public String getVariableName() {
        Perl6ParameterVariable var = PsiTreeUtil.findChildOfType(this, Perl6ParameterVariable.class);
        return var != null ? var.getText() : "";
    }

    @Override
    public Perl6Variable[] getVariables() {
        Perl6Variable var = PsiTreeUtil.findChildOfType(this, Perl6Variable.class);
        return var == null ? new Perl6Variable[0] : new Perl6Variable[]{var};
    }

    @Override
    public String[] getVariableNames() {
        return new String[]{getVariableName()};
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        Perl6ParameterDefault parameterDefault = PsiTreeUtil.getChildOfType(this, Perl6ParameterDefault.class);
        return parameterDefault == null ? getMultideclarationInit() : parameterDefault.getLastChild();
    }

    private PsiElement getMultideclarationInit() {
        Perl6Variable paramVariable = PsiTreeUtil.findChildOfType(this, Perl6Variable.class);
        // We try to see if we are really in a `my ($foo, $bar) ...` case or
        // the signature we are in is just a part of some signature (in a pointy block or a sub)
        PsiElement decl = PsiTreeUtil.getParentOfType(this, Perl6VariableDecl.class, Perl6PsiScope.class);
        if (decl instanceof Perl6VariableDecl)
            return ((Perl6VariableDecl)decl).getInitializer(paramVariable);

        return null;
    }

    @Override
    public boolean isPositional() {
        // If it's declared as a named parameter, it certainly isn't positional.
        if (findChildByClass(Perl6NamedParameterImpl.class) != null)
            return false;

        // If it's slurpy and has the % sigil, it's also named.
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        if (quant != null && quant.getText().equals("*") && getVariableName().startsWith("%"))
            return false;
        if (quant != null && quant.getText().equals("|"))
            return false;

        // Any other case is positional.
        return true;
    }

    @Override
    public boolean isNamed() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        if (quant != null && quant.getText().equals("*") && getVariableName().startsWith("%"))
            return true;
        return findChildByClass(Perl6NamedParameterImpl.class) != null;
    }

    @Override
    public @Nullable Perl6WhereConstraint getWhereConstraint() {
        return findChildByClass(Perl6WhereConstraint.class);
    }

    @Override
    public Perl6PsiElement getValueConstraint() {
        Perl6ValueConstraint constraint = findChildByClass(Perl6ValueConstraint.class);
        return constraint == null ? null : PsiTreeUtil.getChildOfType(constraint, Perl6PsiElement.class);
    }

    @Override
    public boolean isSlurpy() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        return quant != null &&
               (quant.getText().equals("*") || quant.getText().equals("**")
                || quant.getText().equals("+"));
    }

    @Override
    public boolean isRequired() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        return quant != null && quant.getText().equals("!");
    }

    @Override
    public boolean isExplicitlyOptional() {
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        return quant != null && quant.getText().equals("?");
    }

    @Override
    public boolean isOptional() {
        // Certainly optional if it has a default.
        if (findChildByClass(Perl6ParameterDefault.class) != null)
            return true;

        // If there's a quantifier and it's ?, that is decisively optional;
        // any other quantifier implies non-optional (slurpy, required, etc.)
        PsiElement quant = findChildByType(PARAMETER_QUANTIFIER);
        if (quant != null) {
            if (quant.getText().equals("?"))
                return true;
            return false;
        }

        // Otherwise, positional defaults to required, and named to not.
        return !isPositional();
    }

    @Override
    public boolean isCopy() {
        return hasTrait("copy");
    }

    @Override
    public boolean isRW() {
        // Trivially RW if we have the trait.
        if (hasTrait("rw"))
            return true;

        // However, we can also be in a <-> pointy block.
        Perl6Signature signature = PsiTreeUtil.getParentOfType(this, Perl6Signature.class);
        if (signature != null) {
            PsiElement signatured = signature.getParent();
            return signatured instanceof Perl6PointyBlock &&
                   ((Perl6PointyBlock)signatured).getLambda().equals("<->");
        }
        return false;
    }

    private boolean hasTrait(String traitName) {
        Perl6Trait[] traits = PsiTreeUtil.getChildrenOfType(this, Perl6Trait.class);
        if (traits != null)
            for (Perl6Trait trait : traits) {
                if (trait.getTraitModifier().equals("is") && trait.getTraitName().equals(traitName))
                    return true;
            }
        return false;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        Perl6TermDefinition defterm = findChildByClass(Perl6TermDefinition.class);
        if (defterm != null) {
            String name = defterm.getText();
            if (name.length() > 0) {
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, this));
            }
        }
    }

    @Override
    public @NotNull String getScope() {
        return "my";
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement defterm = getNameIdentifier();
        return defterm == null ? null : defterm.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(Perl6TermDefinition.class);
    }

    @Override
    public @NotNull Perl6Type inferType() {
        @Nullable Perl6TypeName type = findChildByClass(Perl6TypeName.class);
        if (type != null) {
            return new Perl6UnresolvedType(type.getTypeName());
        }
        else {
            return Perl6Untyped.INSTANCE;
        }
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean equalsParameter(Perl6Parameter other) {
        // If sigils differ, not equal
        if (isPositional() != other.isPositional() ||
            isNamed() != other.isNamed() ||
            isSlurpy() != other.isSlurpy())
            return false;

        if (Perl6Variable.getSigil(other.getVariableName()) != Perl6Variable.getSigil(getVariableName()))
            return false;

        // if there is a where clause, check it by text
        Perl6WhereConstraint selfWhere = getWhereConstraint();
        Perl6WhereConstraint otherWhere = other.getWhereConstraint();
        if (otherWhere != null ^ selfWhere != null) {
            return false;
        }
        else if (otherWhere != null) {
            if (!selfWhere.getText().equals(otherWhere.getText()))
                return false;
        }

        Perl6Type selfType = inferType();
        Perl6Type otherType = other.inferType();
        if (selfType.equals(otherType))
            return true;
        return false; // Better to get more false negatives than false positives
    }
}
