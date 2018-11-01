package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import edument.perl6idea.psi.stub.Perl6VariableDeclStubElementType;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6VariableDeclImpl extends Perl6MemberStubBasedPsi<Perl6VariableDeclStub> implements Perl6VariableDecl, PsiMetaOwner {
    public Perl6VariableDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6VariableDeclImpl(Perl6VariableDeclStub stub, Perl6VariableDeclStubElementType type) {
        super(stub, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = getVariable();
        return varNode != null ? varNode.getVariableToken() : null;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        String varScope = getScope();
        if (varScope.equals("my") || varScope.equals("state")) {
            Perl6StatementList parent = PsiTreeUtil.getParentOfType(this, Perl6StatementList.class);
            if (parent != null)
                return new LocalSearchScope(parent, getVariableName());
        }
        return super.getUseScope();
    }

    @Override
    public String getName() {
        Perl6VariableDeclStub stub = getStub();
        if (stub != null)
            return stub.getVariableName();
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6Variable var = Perl6ElementFactory.createVariable(getProject(), name);
        ASTNode keyNode = getVariable().getNode();
        ASTNode newKeyNode = var.getVariableToken().getNode();
        getNode().replaceChild(keyNode, newKeyNode);
        return this;
    }

    @Override
    public String getVariableName() {
        return getName();
    }

    @Override
    public String inferType() {
        Perl6VariableDeclStub stub = getStub();
        if (stub != null) {
            String variableType = stub.getVariableType();
            if (variableType != null)
                return variableType;
        }
        PsiElement type = PsiTreeUtil.getPrevSiblingOfType(this, Perl6TypeName.class);
        if (type != null) return getCuttedName(type.getText());
        String assignBasedType = resolveAssign();
        if (assignBasedType != null) return assignBasedType;
        return inferBySigil();
    }

    private String inferBySigil() {
        Perl6Variable variable = PsiTreeUtil.getChildOfType(this, Perl6Variable.class);
        if (variable != null) {
            return variable.getTypeBySigil(variable.getText(), this);
        }
        return null;
    }

    private String resolveAssign() {
        PsiElement infix = PsiTreeUtil.getChildOfType(this, Perl6InfixImpl.class);
        if (infix == null || !infix.getText().equals("=")) return null;
        PsiElement value = infix.getNextSibling();
        while (value instanceof PsiWhiteSpace || (value != null && value.getNode().getElementType() == UNV_WHITE_SPACE))
            value = value.getNextSibling();
        return value == null ? null : ((Perl6PsiElement)value).inferType();
    }

    @Override
    public String defaultScope() {
        return "my";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(Perl6:VARIABLE_DECLARATION)";
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        String name = getName();
        if (name != null && name.length() > 1) {
            offerVariableSymbols(collector, name, this);
        }
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        PsiElement decl = this;
        String desigilname = getName();
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
            public Object[] getDependences() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }

    public static void offerVariableSymbols(Perl6SymbolCollector collector, String name, Perl6VariableDecl var) {
        boolean isInstanceScoped = var.getScope().equals("has");
        // Contribute usual attributes or private if allowed
        String askerKind = collector.enclosingPackageKind();
        // If private variable and we collect internals, it is class asking for composed variable or whatever that gets its own parts (level == 1)
        // then contribute, or contribute if it is not a private variable
        if (Perl6Variable.getTwigil(name) == '!' && collector.areInternalPartsCollected() &&
            (askerKind != null && askerKind.equals("class") || collector.getNestingLevel() == 0) ||
            Perl6Variable.getTwigil(name) != '!')
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, var, isInstanceScoped));
        if (collector.isSatisfied()) return;

        // if it's $.foo, contribute $!foo too
        if (Perl6Variable.getTwigil(name) == '.' && collector.areInternalPartsCollected())
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                                                                 var, name.substring(0, 1) + "!" + name.substring(2)));
        if (collector.isSatisfied()) return;

        // Offer routine if `&name`;
        if (name.startsWith("&") && var.getScope().equals("my"))
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Routine,
                     var, name.substring(1)));
        if (collector.isSatisfied()) return;

        // Offer self-centered symbols
        if (isInstanceScoped && Perl6Variable.getTwigil(name) == '.') {
            if (Perl6Variable.getTwigil(name) == '.') {
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer self!foo;
                    Perl6SymbolKind.Method, var, '!' + name.substring(2)));
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer self.foo;
                    Perl6SymbolKind.Method, var, '.' + name.substring(2)));
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer $.foo;
                    Perl6SymbolKind.Method, var,
                    Perl6Variable.getSigil(var.getVariableName()) + '.' + var.getVariableName().substring(2)
                ));
            }
        }
    }

    private Perl6Variable getVariable() {
        return findChildByClass(Perl6Variable.class);
    }
}
