package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import edument.perl6idea.psi.stub.Perl6VariableDeclStubElementType;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Perl6VariableDeclImpl extends Perl6MemberStubBasedPsi<Perl6VariableDeclStub>
        implements Perl6VariableDecl, PsiMetaOwner {
    public Perl6VariableDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6VariableDeclImpl(Perl6VariableDeclStub stub, Perl6VariableDeclStubElementType type) {
        super(stub, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6TermDefinition term = getTerm();
        if (term != null)
            return term;
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
                return new LocalSearchScope(parent);
        }
        return super.getUseScope();
    }

    @Override
    public String getName() {
        Perl6VariableDeclStub stub = getStub();
        if (stub != null)
            return stub.getVariableNames()[0];
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : null;
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
    public String[] getVariableNames() {
        Perl6VariableDeclStub stub = getStub();
        if (stub != null)
            return stub.getVariableNames();
        Perl6Variable[] variables = getVariables();
        return variables.length == 0
               ? ArrayUtil.EMPTY_STRING_ARRAY
               : Arrays.stream(variables).map(v -> v.getVariableName()).filter(n -> n != null).toArray(String[]::new);
    }

    @Override
    public Perl6Variable[] getVariables() {
        Perl6Signature signature = PsiTreeUtil.getChildOfType(this, Perl6Signature.class);
        if (signature == null) {
            Perl6Variable variable = getVariable();
            return variable == null ? new Perl6Variable[0] : new Perl6Variable[]{variable};
        } else {
            return PsiTreeUtil.findChildrenOfType(signature, Perl6Variable.class).toArray(new Perl6Variable[0]);
        }
    }

    @Override
    public boolean hasInitializer() {
        return getAssignmentInfix() != null;
    }

    @Nullable
    @Override
    public PsiElement getInitializer(Perl6Variable variable) {
        Perl6Infix infix = getAssignmentInfix();
        if (infix == null) return null;

        PsiElement identificator = infix.getPrevSibling();
        while (identificator != null && !(identificator instanceof Perl6Variable || identificator instanceof Perl6Signature)) {
            identificator = identificator.getPrevSibling();
        }

        if (identificator instanceof Perl6Variable) {
            return infix.skipWhitespacesForward();
        } else if (identificator != null) {
            PsiElement init = extractInitializerForSignatureVar((Perl6Signature) identificator, variable, infix);
            if (init != null)
                return init;
        }
        return null;
    }

    @Nullable
    private static PsiElement extractInitializerForSignatureVar(Perl6Signature signature, Perl6Variable variable, @NotNull Perl6Infix infix) {
        int initIndex = -1;
        Perl6Parameter[] parameters = signature.getParameters();
        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            Perl6Parameter parameter = parameters[i];
            Perl6Variable parameterVariable = PsiTreeUtil.findChildOfType(parameter, Perl6Variable.class);
            if (Objects.equals(parameterVariable.getName(), variable.getName())) {
                initIndex = i;
                break;
            }
        }

        if (initIndex >= 0) {
            // We have an initializer index, try to find it in initializer list
            PsiElement multiInit = infix.skipWhitespacesForward();
            if (multiInit instanceof Perl6InfixApplication) {
                Perl6Infix[] commas = PsiTreeUtil.getChildrenOfType(multiInit, Perl6Infix.class);
                if (commas != null && commas.length >= initIndex) {
                    return initIndex == 0 ?
                            commas[0].skipWhitespacesBackward() :
                            commas[initIndex - 1].skipWhitespacesForward();
                }
            }
        }
        return null;
    }

    @Nullable
    private Perl6Infix getAssignmentInfix() {
        Perl6Infix infix = PsiTreeUtil.getChildOfType(this, Perl6Infix.class);
        if (infix == null)
            return null;
        if (!Objects.equals(infix.getOperator().getText(), "="))
            return null;
        return infix;
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        Perl6Infix assignmentInfix = getAssignmentInfix();
        return assignmentInfix == null ? null : assignmentInfix.getRightSide();
    }

    @Override
    public void removeVariable(Perl6Variable variable) {
        PsiNamedElement[] variables = getDeclaredVariables();
        if (variables.length == 1) {
            delete();
        } else {
            // Should we enclose resulting variable list with parentheses or no
            boolean shouldEnclose = variables.length - 1 != 1;

            StringJoiner signature = new StringJoiner(", ");
            String nameToAvoid = variable.getName();
            for (PsiNamedElement var : variables) {
                if (!Objects.equals(var.getName(), nameToAvoid))
                    signature.add(var.getName());
            }

            // Get part of multi-initializer that we have to exclude
            PsiElement deleteInit = getInitializer(variable);
            if (deleteInit == null)
                return;

            // Get all pieces, excluding deleteInit
            List<PsiElement> initPartsToPreserve = new ArrayList<>();
            for (PsiElement initNode : getInitializer().getChildren()) {
                if (!Objects.equals(initNode, deleteInit) && !(initNode instanceof Perl6Infix))
                    initPartsToPreserve.add(initNode);
            }
            // If we have only a single value left after exclusion, it is not InfixApplication anymore,
            // so we can just replace it with the value directly
            if (initPartsToPreserve.size() == 1) {
                getInitializer().replace(initPartsToPreserve.get(0));
            } else {
                // Otherwise, create a new application and use it
                Perl6InfixApplication newApplication = Perl6ElementFactory.
                        createInfixApplication(getProject(), ", ", initPartsToPreserve);
                getInitializer().replace(newApplication);
            }

            PsiElement newDeclaration = Perl6ElementFactory.createVariableAssignment(
                    variable.getProject(),
                    String.format(shouldEnclose ? "(%s)" : "%s", signature.toString()),
                    getInitializer().getText(), false);

            PsiTreeUtil.getParentOfType(this, Perl6Statement.class).replace(newDeclaration);
        }
    }

    private PsiNamedElement[] getDeclaredVariables() {
        Collection<PsiNamedElement> variables = PsiTreeUtil.findChildrenOfType(this, Perl6ParameterVariable.class);
        if (variables.size() == 0) {
            variables = PsiTreeUtil.findChildrenOfType(this, Perl6Variable.class);
        }
        return variables.toArray(new PsiNamedElement[0]);
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
        if (type != null) return getCutName(type.getText());
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
        PsiElement value = Perl6PsiUtil.skipSpaces(infix.getNextSibling(), true);
        if (value instanceof Perl6Variable) {
            String variableName = ((Perl6Variable)value).getVariableName();
            if (Objects.equals(variableName, getVariableNames()[0])) {
                return null;
            }
        } else if (value instanceof Perl6PostfixApplication) {
            if (value.getFirstChild() instanceof Perl6Variable)
                if (Objects.equals(((Perl6Variable)value.getFirstChild()).getVariableName(), getVariableNames()[0])) {
                    return null;
                }
        }
        return value instanceof Perl6PsiElement ? ((Perl6PsiElement)value).inferType() : null;
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
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        if (!getScope().equals("has")) {
            String name = getName();
            if (name != null && name.length() > 1) {
                Perl6TermDefinition defterm = getTerm();
                if (defterm != null) {
                    collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.TypeOrConstant, this));
                }
                else {
                    collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, this));
                    if (collector.isSatisfied()) return;
                    if (name.startsWith("&"))
                        collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Routine,
                                this, name.substring(1)));
                }
            }
        }
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        if (!getScope().equals("has"))
            return;

        String[] attributes = getVariableNames();

        for (String name : attributes) {
            if (name == null || name.length() <= 2)
                continue;

            if (Perl6Variable.getTwigil(name) == '!' && symbolsAllowed.privateAttributesVisible) {
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, name));
            }
            else if (Perl6Variable.getTwigil(name) == '.') {
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, name));
                if (collector.isSatisfied()) return;
                if (symbolsAllowed.privateAttributesVisible) {
                    collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable,
                                                                         this, name.substring(0, 1) + "!" + name.substring(2)));
                    if (collector.isSatisfied()) return;
                }
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol( // Offer self.foo;
                                                                      Perl6SymbolKind.Method, this, '.' + name.substring(2)));
            }
        }
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        PsiElement decl = this;
        String desigilname = getName();
        if (getTerm() == null) {
            // Chop off sigil, if it's not sigil-only name
            if (desigilname != null && desigilname.length() > 1)
                desigilname = desigilname.substring(1);
            // Chop off twigil if any
            if (desigilname != null && desigilname.length() >= 2 && !Character.isLetter(desigilname.charAt(0)))
                desigilname = desigilname.substring(1);
        }
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

    private Perl6Variable getVariable() {
        if (findChildByClass(Perl6Signature.class) != null)
            return null;
        return findChildByClass(Perl6Variable.class);
    }

    private Perl6TermDefinition getTerm() {
        return findChildByClass(Perl6TermDefinition.class);
    }
}
