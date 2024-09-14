package org.raku.refactoring.inline;

import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.RakuLanguage;
import org.raku.psi.*;
import org.raku.psi.external.ExternalRakuRoutineDecl;
import org.raku.psi.symbols.RakuSymbol;
import org.raku.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

abstract public class RakuInlineActionHandler extends InlineActionHandler {
    @Override
    public boolean isEnabledForLanguage(Language language) {
        return language instanceof RakuLanguage;
    }

    protected static void checkUnresolvedElements(RakuPsiElement routine, PsiReference reference) throws IllegalInlineeException {
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

    protected static void getUnresolvedForCall(RakuPsiElement codeToInline, PsiReference reference) throws IllegalInlineeException {
        checkSelfUsages(codeToInline, reference);

        PsiElement call = reference.getElement();
        if (!(call instanceof RakuPsiElement))
            return;

        Collection<RakuPsiElement> varsAndCalls = PsiTreeUtil.findChildrenOfAnyType(codeToInline, RakuVariable.class, RakuSubCall.class);

        for (RakuPsiElement inlineePart : varsAndCalls) {

            PsiReference psiReference = inlineePart instanceof RakuVariable ?
                                        inlineePart.getReference() :
                                        PsiTreeUtil.findChildOfType(inlineePart, RakuSubCallName.class).getReference();
            if (psiReference == null) continue;
            PsiElement declarationOfInlineePart = psiReference.resolve();

            // If we don't know where from it came or it is declared in the inlined code itself, skip
            if (declarationOfInlineePart == null || declarationOfInlineePart instanceof ExternalRakuRoutineDecl || PsiTreeUtil.isAncestor(codeToInline, declarationOfInlineePart, true))
                continue;

            RakuSymbol declFromInlineLocation = null;
            if (inlineePart instanceof RakuVariable)
                declFromInlineLocation = ((RakuPsiElement)call).resolveLexicalSymbol(RakuSymbolKind.Variable,
                                                                                     ((RakuVariable)inlineePart).getVariableName());
            else if (inlineePart instanceof RakuSubCall) {
                declFromInlineLocation = ((RakuPsiElement)call).resolveLexicalSymbol(RakuSymbolKind.Routine,
                                                                                     ((RakuSubCall)inlineePart).getCallName());
            }
            // If it is a lexical that isn't available at inlining myElement, throw it as wrong
            if (declFromInlineLocation == null || declFromInlineLocation.getPsi() == null) {
                String message = "lexical is used in original code that are not available at inlining location";
                if (inlineePart instanceof RakuVariable) {
                    if (RakuVariable.getTwigil(((RakuVariable)inlineePart).getVariableName()) != ' ')
                        message = "attributes of class are used that are not available at inlining location";
                }
                throw new IllegalInlineeException(inlineePart, message);
            }

            if (!Objects.equals(declFromInlineLocation.getPsi(), declarationOfInlineePart))
                throw new IllegalInlineeException(inlineePart,
                                                  "element from original code is shadowed by another one at inlining location");
        }
    }

    private static void checkSelfUsages(RakuPsiElement codeToInline, PsiReference reference) throws IllegalInlineeException {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof RakuSelf) {
                        return super.execute(each);
                    }
                    else if (each instanceof RakuPackageDecl) {
                        return false;
                    }
                    return true;
                }
            };
        PsiTreeUtil.processElements(codeToInline, processor);
        Collection<PsiElement> selfs = processor.getCollection();
        if (!selfs.isEmpty()) {
            RakuPackageDecl routinePackage = PsiTreeUtil.getParentOfType(codeToInline, RakuPackageDecl.class);
            RakuPackageDecl callPackage = PsiTreeUtil.getParentOfType(reference.getElement(), RakuPackageDecl.class);
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
