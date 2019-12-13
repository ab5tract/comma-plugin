package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
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
                for (PsiNamedElement regexVar : obtainRegexDrivenVars(var)) {
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
        return Stream
            .concat(syms.stream().filter(this::isDeclaredAfterCurrentPosition).map(sym -> sym.getName()),
                    obtainRegexDrivenVars(getElement()).stream())
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

    private static Collection<PsiNamedElement> obtainRegexDrivenVars(Perl6Variable starter) {
        // Firstly, we check if we are in inline-statement (s///, subst etc) we need
        // to complete based on, or we want some more complex resolution
        PsiElement anchor = PsiTreeUtil.getParentOfType(starter, Perl6MethodCall.class, Perl6Statement.class);

        if (anchor instanceof Perl6Statement || anchor instanceof Perl6MethodCall &&
                                                !Objects.equals(((Perl6MethodCall)anchor).getCallName(), "subst")) {
            Perl6RegexDriver regex = PsiTreeUtil.getParentOfType(anchor, Perl6QuoteRegex.class, Perl6Regex.class);
            if (regex != null) {
                return regex.collectRegexVariables();
            } else {
                Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector("$/", Perl6SymbolKind.Variable);
                ((Perl6PsiElement)anchor).applyLexicalSymbolCollector(collector);
                if (!collector.getResult().isImplicitlyDeclared())
                    return new ArrayList<>();
                return deduceRegexValuesFromStatement(anchor);
            }
        }
        return new ArrayList<>();
    }

    private static Collection<PsiNamedElement> deduceRegexValuesFromStatement(PsiElement anchor) {
        PsiElement level = anchor;
        while (true) {
            level = PsiTreeUtil.getParentOfType(level, Perl6File.class, Perl6RoutineDecl.class, Perl6StatementList.class);
            if (level == null || level instanceof Perl6RoutineDecl) return new ArrayList<>();

            PsiElementProcessor.CollectElements<PsiElement> processor = new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (!(each instanceof Perl6InfixApplication))
                        return true;
                    Perl6InfixApplication application = (Perl6InfixApplication)each;
                    if (application.getOperator().equals("~~")) {
                        PsiElement[] ops = application.getOperands();
                        if (ops.length == 2) {
                            if (ops[1] instanceof Perl6PsiElement && ((Perl6PsiElement)ops[1]).inferType().equals("Regex"))
                                return super.execute(ops[1]);
                        }
                    }
                    return application.getTextOffset() < anchor.getTextOffset();
                }
            };
            PsiTreeUtil.processElements(level, processor);
            Collection<PsiElement> infixes = processor.getCollection();

            PsiElement curr = null;
            for (PsiElement infix : infixes) {
                curr = infix;
            }
            if (curr instanceof Perl6QuoteRegex) {
                return ((Perl6QuoteRegex)curr).collectRegexVariables();
            }
        }
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }
}
