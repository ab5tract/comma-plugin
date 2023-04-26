package edument.perl6idea.refactoring.inline;

import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.external.ExternalPerl6RoutineDecl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

abstract public class Perl6InlineActionHandler extends InlineActionHandler {
    @Override
    public boolean isEnabledForLanguage(Language language) {
        return language instanceof Perl6Language;
    }

    protected static void checkUnresolvedElements(Perl6PsiElement routine, PsiReference reference) throws IllegalInlineeException {
        // We search for self references, attributes, lexicals
        // If executed on certain call, check it, otherwise check all calls
        if (reference != null) {
            getUnresolvedForCall(routine, reference);
        } else {
            Project project = routine.getProject();
            for (PsiReference callRef : ReferencesSearch.search(routine, GlobalSearchScope.projectScope(project))) {
                getUnresolvedForCall(routine, callRef);
            }
        }
    }

    protected static void getUnresolvedForCall(Perl6PsiElement codeToInline, PsiReference reference) throws IllegalInlineeException {
        checkSelfUsages(codeToInline, reference);

        PsiElement call = reference.getElement();
        if (!(call instanceof Perl6PsiElement))
            return;

        Collection<Perl6PsiElement> varsAndCalls = PsiTreeUtil.findChildrenOfAnyType(codeToInline, Perl6Variable.class, Perl6SubCall.class);

        for (Perl6PsiElement inlineePart : varsAndCalls) {

            PsiReference psiReference = inlineePart instanceof Perl6Variable ?
                                        inlineePart.getReference() :
                                        PsiTreeUtil.findChildOfType(inlineePart, Perl6SubCallName.class).getReference();
            if (psiReference == null) continue;
            PsiElement declarationOfInlineePart = psiReference.resolve();

            // If we don't know where from it came or it is declared in the inlined code itself, skip
            if (declarationOfInlineePart == null || declarationOfInlineePart instanceof ExternalPerl6RoutineDecl || PsiTreeUtil.isAncestor(codeToInline, declarationOfInlineePart, true))
                continue;

            Perl6Symbol declFromInlineLocation = null;
            if (inlineePart instanceof Perl6Variable)
                declFromInlineLocation = ((Perl6PsiElement)call).resolveLexicalSymbol(Perl6SymbolKind.Variable,
                                                                                      ((Perl6Variable)inlineePart).getVariableName());
            else if (inlineePart instanceof Perl6SubCall) {
                declFromInlineLocation = ((Perl6PsiElement)call).resolveLexicalSymbol(Perl6SymbolKind.Routine,
                                                                                      ((Perl6SubCall)inlineePart).getCallName());
            }
            // If it is a lexical that isn't available at inlining myElement, throw it as wrong
            if (declFromInlineLocation == null || declFromInlineLocation.getPsi() == null) {
                String message = "lexical is used in original code that are not available at inlining location";
                if (inlineePart instanceof Perl6Variable) {
                    if (Perl6Variable.getTwigil(((Perl6Variable)inlineePart).getVariableName()) != ' ')
                        message = "attributes of class are used that are not available at inlining location";
                }
                throw new IllegalInlineeException(inlineePart, message);
            }

            if (!Objects.equals(declFromInlineLocation.getPsi(), declarationOfInlineePart))
                throw new IllegalInlineeException(inlineePart,
                                                  "element from original code is shadowed by another one at inlining location");
        }
    }

    private static void checkSelfUsages(Perl6PsiElement codeToInline, PsiReference reference) throws IllegalInlineeException {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6Self) {
                        return super.execute(each);
                    }
                    else if (each instanceof Perl6PackageDecl) {
                        return false;
                    }
                    return true;
                }
            };
        PsiTreeUtil.processElements(codeToInline, processor);
        Collection<PsiElement> selfs = processor.getCollection();
        if (!selfs.isEmpty()) {
            Perl6PackageDecl routinePackage = PsiTreeUtil.getParentOfType(codeToInline, Perl6PackageDecl.class);
            Perl6PackageDecl callPackage = PsiTreeUtil.getParentOfType(reference.getElement(), Perl6PackageDecl.class);
            if (callPackage == null ||
                routinePackage == null ||
                !Objects.equals(callPackage.getPackageName(), routinePackage.getPackageName())) {
                throw new IllegalInlineeException(
                    selfs.iterator().next(),
                    "a reference to `self` is found, but caller and callee are in different classes");
            }
        }
    }
}
