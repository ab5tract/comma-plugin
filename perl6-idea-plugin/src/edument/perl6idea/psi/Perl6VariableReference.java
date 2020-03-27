package edument.perl6idea.psi;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Perl6VariableReference extends PsiReferenceBase<Perl6Variable> {
    public Perl6VariableReference(Perl6Variable var) {
        super(var, new TextRange(0, var.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6Variable var = getElement();
        String name = var.getVariableName();
        if (name == null)
            return null;
        char twigil = Perl6Variable.getTwigil(name);
        if (twigil == '!' || twigil == '.') {
            // Attribute; resolve through MOP.
            Perl6PackageDecl enclosingPackage = var.getSelfType();
            if (enclosingPackage != null) {
                Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(name, Perl6SymbolKind.Variable);
                enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                        true, true, true, enclosingPackage.getPackageKind().equals("role")));
                Perl6Symbol symbol = collector.getResult();
                if (symbol != null)
                    return symbol.getPsi();
            }
        }
        else {
            // Lexical; resolve through lexpad.
            Perl6Symbol symbol = var.resolveLexicalSymbol(Perl6SymbolKind.Variable, name);
            if (symbol != null) {
                PsiElement psi = symbol.getPsi();
                if (psi != null) {
                    if (psi.getContainingFile() != var.getContainingFile())
                        return psi;
                    if (psi.getTextOffset() <= var.getTextOffset() || Perl6Variable.getSigil(name) == '&')
                        return psi;
                }
            } else {
                Collection<PsiNamedElement> regexDrivenVars = obtainRegexDrivenVars(var);
                if (regexDrivenVars == null)
                    return null;
                for (PsiNamedElement regexVar : regexDrivenVars) {
                    if (Objects.equals(regexVar.getName(), name))
                        return regexVar;
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<Perl6Symbol> syms = new ArrayList<>(getElement().getLexicalSymbolVariants(Perl6SymbolKind.Variable));
        Perl6PackageDecl enclosingPackage = getElement().getSelfType();
        if (enclosingPackage != null) {
            Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Variable);
            enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                    true, true, true, enclosingPackage.getPackageKind().equals("role")));
            syms.addAll(collector.getVariants());
        }
        Collection<PsiNamedElement> regexDrivenVars = obtainRegexDrivenVars(getElement());
        Collection<PsiNamedElement> elements = regexDrivenVars == null ? new ArrayList<>() : regexDrivenVars;
        return Stream
            .concat(syms.stream().filter(this::isDeclaredAfterCurrentPosition).map(sym -> sym.getName()),
                    elements.stream())
            .toArray();
    }

    private boolean isDeclaredAfterCurrentPosition(Perl6Symbol symbol) {
        PsiElement psi = symbol.getPsi();

        // No PSI element or imported from another file is always fine.
        if (psi == null)
            return true;
        if (psi.getContainingFile() != getElement().getContainingFile())
            return true;

        // Declared in this file before the current location is fine.
        if (psi.getTextOffset() < getElement().getTextOffset())
            return true;

        // Declared later, but routine or attribute is fine.
        String name = symbol.getName();
        if (name.length() >= 2) {
            if (name.charAt(0) == '&')
                return true;
            char twigil = name.charAt(1);
            return twigil == '!' || twigil == '.';
        }

        return false;
    }

    @Nullable
    public static Collection<PsiNamedElement> obtainRegexDrivenVars(Perl6Variable starter) {
        // Firstly, we check if we are in inline-statement (s///, subst etc) we need
        // to complete based on, or we want some more complex resolution
        Perl6PsiElement anchor = PsiTreeUtil.getParentOfType(starter, Perl6Regex.class, Perl6QuoteRegex.class, Perl6Statement.class);

        if (anchor instanceof Perl6Statement) {
            Perl6RegexDriver regex = PsiTreeUtil.getParentOfType(anchor, Perl6QuoteRegex.class, Perl6Regex.class);
            if (regex != null) {
                return regex.collectRegexVariables();
            } else {
                PsiElement call = PsiTreeUtil.getParentOfType(anchor, Perl6MethodCall.class, Perl6File.class);
                if (call instanceof Perl6MethodCall && ((Perl6MethodCall)call).getCallName().equals(".subst")) {
                    PsiElement[] args = ((Perl6MethodCall)call).getCallArguments();
                    if (args.length >= 2) {
                        if (((Perl6PsiElement)args[0]).inferType().equals("Regex") &&
                            PsiTreeUtil.isAncestor(args[1], starter, true)) {
                            List<PsiNamedElement> elemsToReturn = new ArrayList<>();
                            if (derefAndCollectRegexVars(args[0], elemsToReturn)) {
                                return elemsToReturn;
                            }
                        }
                    }
                }
                Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector("$/", Perl6SymbolKind.Variable);
                anchor.applyLexicalSymbolCollector(collector);
                Perl6Symbol result = collector.getResult();
                if (result != null && !result.isImplicitlyDeclared()) {
                    if (result.getPsi() instanceof Perl6ParameterVariable) {
                        List<GotoRelatedItem> items = NavigationUtil.collectRelatedItems(result.getPsi(), null);
                        if (items.size() == 1) {
                            PsiElement rule = items.get(0).getElement();
                            if (rule instanceof Perl6RegexDecl) {
                                Perl6RegexDriver driver = PsiTreeUtil.findChildOfType(rule, Perl6Regex.class);
                                return driver == null ? null : driver.collectRegexVariables();
                            }
                        }
                    }
                } else {
                    return deduceRegexValuesFromStatement(anchor, starter);
                }
            }
        } else if (anchor instanceof Perl6RegexDriver) {
            return ((Perl6RegexDriver)anchor).collectRegexVariables();
        }
        return null;
    }

    private static boolean derefAndCollectRegexVars(PsiElement arg, List<PsiNamedElement> elemsToReturn) {
        if (arg instanceof Perl6Variable) {
            Perl6Variable regexVar = (Perl6Variable)arg;
            PsiReference ref = regexVar.getReference();
            assert ref != null;
            PsiElement resolve = ref.resolve();
            if (resolve instanceof Perl6VariableDecl) {
                PsiElement init = ((Perl6VariableDecl)resolve).getInitializer(regexVar);
                if (init instanceof Perl6RegexDriver) {
                    elemsToReturn.addAll(((Perl6RegexDriver)init).collectRegexVariables());
                }
                return true;
            }
        } else if (arg instanceof Perl6RegexDriver) {
            elemsToReturn.addAll(((Perl6RegexDriver)arg).collectRegexVariables());
            return true;
        }
        return false;
    }

    @Nullable
    private static Collection<PsiNamedElement> deduceRegexValuesFromStatement(PsiElement anchor,
                                                                              Perl6Variable starter) {
        PsiElement level = anchor;
        while (true) {
            level = PsiTreeUtil.getParentOfType(level, Perl6File.class, Perl6RoutineDecl.class, Perl6StatementList.class);
            if (level == null || level instanceof Perl6RoutineDecl) return null;

            PsiElementProcessor.CollectElements<PsiElement> processor = new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6InfixApplication)
                        return searchForRegexApplication((Perl6InfixApplication)each);
                    else if (each instanceof Perl6WhenStatement || each instanceof Perl6IfStatement || each instanceof Perl6UnlessStatement)
                        return searchForControlContextualizer((P6Control)each);
                    else if (each instanceof Perl6Statement)
                        return searchForSinkRegex((Perl6Statement)each);
                    return true;
                }

                private boolean searchForSinkRegex(Perl6Statement statement) {
                    if (statement.getFirstChild() instanceof Perl6RegexDriver)
                        return super.execute(statement.getFirstChild());
                    return true;
                }

                private boolean searchForControlContextualizer(P6Control control) {
                    if (control.getTopic() instanceof Perl6RegexDriver)
                        return super.execute(control.getTopic());
                    return true;
                }

                private boolean searchForRegexApplication(@NotNull Perl6InfixApplication app) {
                    if (app.getOperator().equals("~~")) {
                        PsiElement[] ops = app.getOperands();
                        if (ops.length == 2) {
                            if (ops[1] instanceof Perl6PsiElement && ((Perl6PsiElement)ops[1]).inferType().equals("Regex"))
                                return super.execute(ops[1]);
                        }
                    }
                    return app.getTextOffset() < anchor.getTextOffset();
                }
            };
            PsiTreeUtil.processElements(level, processor);
            Collection<PsiElement> infixes = processor.getCollection();

            PsiElement curr = null;

            // Might be null of top level
            Perl6RoutineDecl anchorRoutineLevel = PsiTreeUtil.getParentOfType(anchor, Perl6RoutineDecl.class);
            for (PsiElement infix : infixes) {
                if (infix.getTextOffset() > starter.getTextOffset())
                    break;
                if (Objects.equals(PsiTreeUtil.getParentOfType(infix, Perl6RoutineDecl.class), anchorRoutineLevel))
                    curr = infix;
            }
            if (curr != null) {
                List<PsiNamedElement> elemsToReturn = new ArrayList<>();
                return derefAndCollectRegexVars(curr, elemsToReturn) ? elemsToReturn : null;
            }
        }
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }
}
