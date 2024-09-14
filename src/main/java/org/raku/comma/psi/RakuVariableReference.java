package org.raku.comma.psi;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.stub.index.RakuStubIndexKeys;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RakuVariableReference extends PsiReferenceBase.Poly<RakuVariable> {
    public RakuVariableReference(RakuVariable var) {
        super(var, new TextRange(0, var.getTextLength()), false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RakuVariable var = getElement();
        String name = var.getVariableName();
        if (name == null)
            return ResolveResult.EMPTY_ARRAY;
        char twigil = RakuVariable.getTwigil(name);
        if (twigil == '!' || twigil == '.') {
            // Attribute; resolve through MOP.
            RakuPackageDecl enclosingPackage = var.getSelfType();
            if (enclosingPackage != null) {
                RakuSingleResolutionSymbolCollector collector = new RakuSingleResolutionSymbolCollector(name, RakuSymbolKind.Variable);
                enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                    true, true, true, enclosingPackage.getPackageKind().equals("role")));
                RakuSymbol symbol = collector.getResult();
                if (symbol != null)
                    return new ResolveResult[]{new PsiElementResolveResult(symbol.getPsi())};
            }
        }
        else if (twigil == '*') {
            if (DumbService.isDumb(getElement().getProject()))
                return ResolveResult.EMPTY_ARRAY;
            Collection<RakuVariableDecl> decls =
                StubIndex.getElements(RakuStubIndexKeys.DYNAMIC_VARIABLES, name, myElement.getProject(), GlobalSearchScope.allScope(
                    myElement.getProject()), RakuVariableDecl.class);
            if (decls.isEmpty())
                return ResolveResult.EMPTY_ARRAY;
            return ContainerUtil.map(decls, d -> new PsiElementResolveResult(d)).toArray(ResolveResult.EMPTY_ARRAY);
        }
        else {
            // Lexical; resolve through lexpad.
            RakuSymbol symbol = var.resolveLexicalSymbol(RakuSymbolKind.Variable, name);
            if (symbol != null) {
                PsiElement psi = symbol.getPsi();
                if (psi != null) {
                    if (psi.getContainingFile() != var.getContainingFile())
                        return new ResolveResult[]{new PsiElementResolveResult(psi)};
                    if (psi.getTextOffset() <= var.getTextOffset() || RakuVariable.getSigil(name) == '&')
                        return new ResolveResult[]{new PsiElementResolveResult(psi)};
                }
            }
            else {
                // Trying to resolve `(42 ~~ $foo)` is dangerous. If $foo is undeclared,
                // we search for nearest regex (which is the application we are in) to obtain $0, $1 etc to maybe resolve there,
                // so we get the var on the right of the smartmatch, this $foo, try to resolve its type, to do so we search
                // for a declaration and infinite loop from there. Fix this by never trying to resolve right variable
                // if there is no lexical.
                RakuInfixApplication infix = PsiTreeUtil.getParentOfType(var, RakuInfixApplication.class);
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
        List<RakuSymbol> syms = new ArrayList<>(getElement().getLexicalSymbolVariants(RakuSymbolKind.Variable));
        RakuPackageDecl enclosingPackage = getElement().getSelfType();
        if (enclosingPackage != null) {
            RakuVariantsSymbolCollector collector = new RakuVariantsSymbolCollector(RakuSymbolKind.Variable);
            enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                true, true, true, enclosingPackage.getPackageKind().equals("role")));
            syms.addAll(collector.getVariants());
        }
        Collection<PsiNamedElement> regexDrivenVars = obtainRegexDrivenVars(getElement());
        Collection<PsiNamedElement> elements = regexDrivenVars == null ? new ArrayList<>() : regexDrivenVars;
        Collection<String> dynamicVariables =
            StubIndex.getInstance().getAllKeys(RakuStubIndexKeys.DYNAMIC_VARIABLES, myElement.getProject());
        return Stream.concat(
            Stream
                .concat(syms.stream().filter(this::isDeclaredAfterCurrentPosition).map(sym -> sym.getName()),
                        elements.stream()),
            dynamicVariables.stream())
            .toArray();
    }

    private boolean isDeclaredAfterCurrentPosition(RakuSymbol symbol) {
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
    public static Collection<PsiNamedElement> obtainRegexDrivenVars(RakuVariable starter) {
        // Firstly, we check if we are in inline-statement (s///, subst etc) we need
        // to complete based on, or we want some more complex resolution
        RakuPsiElement anchor = PsiTreeUtil.getParentOfType(starter, RakuRegex.class, RakuQuoteRegex.class, RakuStatement.class);

        if (anchor instanceof RakuStatement) {
            RakuRegexDriver regex = PsiTreeUtil.getParentOfType(anchor, RakuQuoteRegex.class, RakuRegex.class);
            if (regex != null) {
                return regex.collectRegexVariables();
            }
            else {
                PsiElement call = PsiTreeUtil.getParentOfType(anchor, RakuMethodCall.class, RakuFile.class);
                if (call instanceof RakuMethodCall && ((RakuMethodCall)call).getCallName().equals(".subst")) {
                    PsiElement[] args = ((RakuMethodCall)call).getCallArguments();
                    if (args.length >= 2) {
                        RakuType regexType = RakuSdkType.getInstance().getCoreSettingType(starter.getProject(), RakuSettingTypeId.Regex);
                        if (((RakuPsiElement)args[0]).inferType().equals(regexType) &&
                            PsiTreeUtil.isAncestor(args[1], starter, true)) {
                            List<PsiNamedElement> elemsToReturn = new ArrayList<>();
                            if (derefAndCollectRegexVars(args[0], elemsToReturn)) {
                                return elemsToReturn;
                            }
                        }
                    }
                }
                RakuSingleResolutionSymbolCollector collector = new RakuSingleResolutionSymbolCollector("$/", RakuSymbolKind.Variable);
                anchor.applyLexicalSymbolCollector(collector);
                RakuSymbol result = collector.getResult();
                if (result != null && !result.isImplicitlyDeclared()) {
                    if (result.getPsi() instanceof RakuParameterVariable) {
                        List<GotoRelatedItem> items = NavigationUtil.collectRelatedItems(result.getPsi(), null);
                        if (items.size() == 1) {
                            PsiElement rule = items.get(0).getElement();
                            if (rule instanceof RakuRegexDecl) {
                                RakuRegexDriver driver = PsiTreeUtil.findChildOfType(rule, RakuRegex.class);
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
        else if (anchor instanceof RakuRegexDriver) {
            return ((RakuRegexDriver)anchor).collectRegexVariables();
        }
        return null;
    }

    private static boolean derefAndCollectRegexVars(PsiElement arg, List<PsiNamedElement> elemsToReturn) {
        if (arg instanceof RakuVariable regexVar) {
            PsiReference ref = regexVar.getReference();
            assert ref != null;
            PsiElement resolve = ref.resolve();
            if (resolve instanceof RakuVariableDecl) {
                PsiElement init = ((RakuVariableDecl)resolve).getInitializer(regexVar);
                if (init instanceof RakuRegexDriver) {
                    elemsToReturn.addAll(((RakuRegexDriver)init).collectRegexVariables());
                }
                return true;
            }
        }
        else if (arg instanceof RakuRegexDriver) {
            elemsToReturn.addAll(((RakuRegexDriver)arg).collectRegexVariables());
            return true;
        }
        return false;
    }

    @Nullable
    private static Collection<PsiNamedElement> deduceRegexValuesFromStatement(PsiElement anchor,
                                                                              RakuVariable starter) {
        PsiElement level = anchor;
        while (true) {
            level = PsiTreeUtil.getParentOfType(level, RakuFile.class, RakuRoutineDecl.class, RakuStatementList.class);
            if (level == null || level instanceof RakuRoutineDecl) return null;

            PsiElementProcessor.CollectElements<PsiElement> processor = new PsiElementProcessor.CollectElements<>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each.getTextOffset() > starter.getTextOffset()) {
                        return false;
                    }
                    if (each instanceof RakuInfixApplication) {
                        return searchForRegexApplication((RakuInfixApplication)each);
                    }
                    else if (each instanceof RakuWhenStatement ||
                             each instanceof RakuIfStatement ||
                             each instanceof RakuUnlessStatement) {
                        return searchForControlContextualizer((RakuControl)each);
                    }
                    else if (each instanceof RakuStatement) {
                        return searchForSinkRegex((RakuStatement)each);
                    }
                    return true;
                }

                private boolean searchForSinkRegex(RakuStatement statement) {
                    if (statement.getFirstChild() instanceof RakuRegexDriver) {
                        return super.execute(statement.getFirstChild());
                    }
                    return true;
                }

                private boolean searchForControlContextualizer(RakuControl control) {
                    if (control.getTopic() instanceof RakuRegexDriver) {
                        return super.execute(control.getTopic());
                    }
                    return true;
                }

                private boolean searchForRegexApplication(@NotNull RakuInfixApplication app) {
                    if (app.getOperator().equals("~~")) {
                        PsiElement[] ops = app.getOperands();
                        if (ops.length == 2) {
                            RakuType regexType =
                                RakuSdkType.getInstance().getCoreSettingType(starter.getProject(), RakuSettingTypeId.Regex);
                            if (ops[1] instanceof RakuPsiElement && ((RakuPsiElement)ops[1]).inferType().equals(regexType)) {
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
            RakuRoutineDecl anchorRoutineLevel = PsiTreeUtil.getParentOfType(anchor, RakuRoutineDecl.class);
            for (PsiElement infix : infixes) {
                if (infix.getTextOffset() > starter.getTextOffset())
                    break;
                if (Objects.equals(PsiTreeUtil.getParentOfType(infix, RakuRoutineDecl.class), anchorRoutineLevel))
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
