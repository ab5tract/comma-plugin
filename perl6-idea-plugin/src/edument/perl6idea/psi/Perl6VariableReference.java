package edument.perl6idea.psi;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Perl6VariableReference extends PsiReferenceBase.Poly<Perl6Variable> {
    public Perl6VariableReference(Perl6Variable var) {
        super(var, new TextRange(0, var.getTextLength()), false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Perl6Variable var = getElement();
        String name = var.getVariableName();
        if (name == null)
            return ResolveResult.EMPTY_ARRAY;
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
                    return new ResolveResult[]{new PsiElementResolveResult(symbol.getPsi())};
            }
        }
        else if (twigil == '*') {
            Collection<Perl6VariableDecl> decls =
                StubIndex.getElements(Perl6StubIndexKeys.DYNAMIC_VARIABLES, name, myElement.getProject(), GlobalSearchScope.allScope(
                    myElement.getProject()), Perl6VariableDecl.class);
            if (decls.isEmpty())
                return ResolveResult.EMPTY_ARRAY;
            return ContainerUtil.map(decls, d -> new PsiElementResolveResult(d)).toArray(ResolveResult.EMPTY_ARRAY);
        }
        else {
            // Lexical; resolve through lexpad.
            Perl6Symbol symbol = var.resolveLexicalSymbol(Perl6SymbolKind.Variable, name);
            if (symbol != null) {
                PsiElement psi = symbol.getPsi();
                if (psi != null) {
                    if (psi.getContainingFile() != var.getContainingFile())
                        return new ResolveResult[]{new PsiElementResolveResult(psi)};
                    if (psi.getTextOffset() <= var.getTextOffset() || Perl6Variable.getSigil(name) == '&')
                        return new ResolveResult[]{new PsiElementResolveResult(psi)};
                }
            }
            else {
                // Trying to resolve `(42 ~~ $foo)` is dangerous. If $foo is undeclared,
                // we search for nearest regex (which is the application we are in) to obtain $0, $1 etc to maybe resolve there,
                // so we get the var on the right of the smartmatch, this $foo, try to resolve its type, to do so we search
                // for a declaration and infinite loop from there. Fix this by never trying to resolve right variable
                // if there is no lexical.
                Perl6InfixApplication infix = PsiTreeUtil.getParentOfType(var, Perl6InfixApplication.class);
                if (infix != null && infix.getOperator().equals("~~") &&
                    (PsiEquivalenceUtil.areElementsEquivalent(infix.getOperands()[1], var) ||
                     PsiEquivalenceUtil.areElementsEquivalent(infix.getOperands()[1], var.getParent())))
                    return ResolveResult.EMPTY_ARRAY;

                Collection<PsiNamedElement> regexDrivenVars = obtainRegexDrivenVars(var);
                if (regexDrivenVars == null)
                    return ResolveResult.EMPTY_ARRAY;
                for (PsiNamedElement regexVar : regexDrivenVars) {
                    if (Objects.equals(regexVar.getName(), name))
                        return new ResolveResult[]{new PsiElementResolveResult(regexVar)};
                }
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public Object @NotNull [] getVariants() {
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
        Collection<String> dynamicVariables =
            StubIndex.getInstance().getAllKeys(Perl6StubIndexKeys.DYNAMIC_VARIABLES, myElement.getProject());
        return Stream.concat(
            Stream
                .concat(syms.stream().filter(this::isDeclaredAfterCurrentPosition).map(sym -> sym.getName()),
                        elements.stream()),
            dynamicVariables.stream())
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
            }
            else {
                PsiElement call = PsiTreeUtil.getParentOfType(anchor, Perl6MethodCall.class, Perl6File.class);
                if (call instanceof Perl6MethodCall && ((Perl6MethodCall)call).getCallName().equals(".subst")) {
                    PsiElement[] args = ((Perl6MethodCall)call).getCallArguments();
                    if (args.length >= 2) {
                        Perl6Type regexType = Perl6SdkType.getInstance().getCoreSettingType(starter.getProject(), Perl6SettingTypeId.Regex);
                        if (((Perl6PsiElement)args[0]).inferType().equals(regexType) &&
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
                }
                else {
                    return deduceRegexValuesFromStatement(anchor, starter);
                }
            }
        }
        else if (anchor instanceof Perl6RegexDriver) {
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
        }
        else if (arg instanceof Perl6RegexDriver) {
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

            PsiElementProcessor.CollectElements<PsiElement> processor = new PsiElementProcessor.CollectElements<>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each.getTextOffset() > starter.getTextOffset()) {
                        return false;
                    }
                    if (each instanceof Perl6InfixApplication) {
                        return searchForRegexApplication((Perl6InfixApplication)each);
                    }
                    else if (each instanceof Perl6WhenStatement ||
                             each instanceof Perl6IfStatement ||
                             each instanceof Perl6UnlessStatement) {
                        return searchForControlContextualizer((P6Control)each);
                    }
                    else if (each instanceof Perl6Statement) {
                        return searchForSinkRegex((Perl6Statement)each);
                    }
                    return true;
                }

                private boolean searchForSinkRegex(Perl6Statement statement) {
                    if (statement.getFirstChild() instanceof Perl6RegexDriver) {
                        return super.execute(statement.getFirstChild());
                    }
                    return true;
                }

                private boolean searchForControlContextualizer(P6Control control) {
                    if (control.getTopic() instanceof Perl6RegexDriver) {
                        return super.execute(control.getTopic());
                    }
                    return true;
                }

                private boolean searchForRegexApplication(@NotNull Perl6InfixApplication app) {
                    if (app.getOperator().equals("~~")) {
                        PsiElement[] ops = app.getOperands();
                        if (ops.length == 2) {
                            Perl6Type regexType =
                                Perl6SdkType.getInstance().getCoreSettingType(starter.getProject(), Perl6SettingTypeId.Regex);
                            if (ops[1] instanceof Perl6PsiElement && ((Perl6PsiElement)ops[1]).inferType().equals(regexType)) {
                                return super.execute(ops[1]);
                            }
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
