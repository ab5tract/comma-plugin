package edument.perl6idea.refactoring.inline.call;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.refactoring.inline.Perl6InlineActionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class Perl6InlineCallActionHandler extends Perl6InlineActionHandler {
    @Override
    public boolean canInlineElement(PsiElement element) {
        return element instanceof Perl6RoutineDecl &&
               element.getNavigationElement() instanceof Perl6RoutineDecl &&
               element.getLanguage() instanceof Perl6Language;
    }

    @Override
    public void inlineElement(Project project, Editor editor, PsiElement element) {
        Perl6RoutineDecl routine = (Perl6RoutineDecl)element.getNavigationElement();

        PsiReference reference = editor != null ?
                                 TargetElementUtil.findReference(editor, editor.getCaretModel().getOffset()) : null;

        if (routine.getParent() instanceof Perl6MultiDecl) {
            String typeOfMulti = routine.getParent().getFirstChild().getText();
            if (Objects.equals(typeOfMulti, "proto"))
                reportError(project, editor, "inlining of routine with proto is not supported");
            else if (Objects.equals(typeOfMulti, "multi"))
                reportError(project, editor, "inlining of routine with multi is not supported");
            return;
        }

        if (reference != null) {
            final PsiElement refElement = reference.getElement();
            if (refElement != null && !isEnabledForLanguage(refElement.getLanguage())) {
                reportError(project, editor, "inlining of routine is not supported for " + refElement.getLanguage().getDisplayName());
                return;
            }
        }

        if (hasStateVariables(routine)) {
            reportError(project, editor, "state variables are present");
            return;
        }

        PsiElement unresolvedElement = getUnresolvedElements(routine, reference);
        if (unresolvedElement instanceof Perl6Self) {
            reportError(project, editor, "a reference to `self` is found, but caller and callee are in different classes");
            return;
        } else if (unresolvedElement instanceof Perl6Variable) {
            String variableName = ((Perl6Variable)unresolvedElement).getVariableName();
            if (Perl6Variable.getTwigil(variableName) == '!' || Perl6Variable.getTwigil(variableName) == '.') {
                reportError(project, editor, "attributes of class that contains a routine are used");
            } else {
                reportError(project, editor, "lexical variables are used in original routine that are not available at call location");
            }
            return;
        }

        boolean allowInlineThisOnly = false;

        if (hasBadReturns(routine)) {
            reportError(project, editor, "return statement interrupts the execution flow");
                return;
        }
        PsiElement[] routineContent = routine.getContent();
        if (routineContent == null || routineContent.length == 0) {
            reportError(project, editor, "Refactoring cannot be applied to empty routine");
            return;
        }


        PsiElement refElement = null;
        if (reference != null) {
            refElement = reference.getElement();
        }

        Perl6InlineCallDialog dialog = new Perl6InlineCallDialog(project, routine, refElement, editor, allowInlineThisOnly);
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            dialog.doAction();
        } else {
            dialog.show();
        }
    }

    private static PsiElement getUnresolvedElements(Perl6RoutineDecl routine, PsiReference reference) {
        // We search for self references, attributes, lexicals
        // If executed on call, check it only, otherwise check all calls
        if (reference != null) {
            return getUnresolvedForCall(routine, reference);
        } else {
            Project project = routine.getProject();
            for (PsiReference callRef : ReferencesSearch.search(routine, GlobalSearchScope.projectScope(project))) {
                PsiElement item = getUnresolvedForCall(routine, callRef);
                if (item != null)
                    return item;
            }
        }
        return null;
    }

    private static PsiElement getUnresolvedForCall(Perl6RoutineDecl routine, PsiReference reference) {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6Self)
                        return super.execute(each);
                    else if (each instanceof Perl6PackageDecl)
                        return false;
                    return true;
                }
            };
        for (PsiElement part : routine.getContent()) {
            PsiTreeUtil.processElements(part, processor);
        }
        Collection<PsiElement> selfs = processor.getCollection();
        if (!selfs.isEmpty()) {
            Perl6PackageDecl routinePackage = PsiTreeUtil.getParentOfType(routine, Perl6PackageDecl.class);
            Perl6PackageDecl callPackage = PsiTreeUtil.getParentOfType(reference.getElement(), Perl6PackageDecl.class);
            if (callPackage == null || routinePackage == null || !Objects.equals(callPackage.getPackageName(), routinePackage.getPackageName())) {
                return selfs.iterator().next();
            }
        }

        PsiElement call = reference.getElement();
        PsiElement[] statements = routine.getContent();
        for (PsiElement statement : statements) {
            Collection<Perl6Variable> vars = PsiTreeUtil.findChildrenOfType(statement, Perl6Variable.class);
            for (Perl6Variable var : vars) {
                PsiReference varRef = var.getReference();
                if (varRef == null) continue;
                PsiElement varDecl = varRef.resolve();
                if (varDecl == null)
                    continue;
                if (PsiTreeUtil.isAncestor(routine, varDecl, true))
                    continue;
                if (call instanceof Perl6PsiElement) {
                    Perl6Symbol declFromCall = ((Perl6PsiElement)call).resolveSymbol(Perl6SymbolKind.Variable, var.getVariableName());
                    if (declFromCall == null || declFromCall.getPsi() == null)
                        return var;
                }
            }
        }
        return null;
    }

    private static boolean hasStateVariables(Perl6RoutineDecl routine) {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6VariableDecl) {
                        PsiElement parent = each.getParent();
                        if (parent instanceof Perl6ScopedDecl) {
                            if (Objects.equals(((Perl6ScopedDecl)parent).getScope(), "state")) {
                                return super.execute(each);
                            }
                        }
                    }
                    else if (each instanceof Perl6RoutineDecl || each instanceof Perl6PackageDecl) {
                        return false;
                    }
                    return true;
                }
            };
        for (PsiElement part : routine.getContent()) {
            PsiTreeUtil.processElements(part, processor);
        }
        return !processor.getCollection().isEmpty();
    }

    private static boolean hasBadReturns(Perl6RoutineDecl routine) {
        Collection<PsiElement> returnStatements = collectReturns(routine);
        PsiElement[] statements = routine.getContent();
        if (statements == null || statements.length == 0) {
            return false;
        }

        checkTrailingReturn(routine, returnStatements);
        return !returnStatements.isEmpty();
    }

    private static void checkTrailingReturn(@NotNull Perl6RoutineDecl routine, Collection<PsiElement> returnStatements) {
        PsiElement[] statements = routine.getContent();
        PsiElement last = statements[statements.length - 1];
        returnStatements.remove(last);
    }

    private static Collection<PsiElement> collectReturns(Perl6RoutineDecl routine) {
        PsiElementProcessor.CollectElements<PsiElement> processor =
            new PsiElementProcessor.CollectElements<PsiElement>() {
                @Override
                public boolean execute(@NotNull PsiElement each) {
                    if (each instanceof Perl6SubCall) {
                        if (Objects.equals(((Perl6SubCall)each).getCallName(), "return")) {
                            Perl6Statement statement = PsiTreeUtil.getParentOfType(each, Perl6Statement.class);
                            assert statement != null;
                            return super.execute(statement);
                        }
                    }
                    return !(each instanceof Perl6RoutineDecl || each instanceof Perl6PackageDecl);
                }
            };
        PsiElement[] contents = routine.getContent();
        for (PsiElement statement : contents) {
            PsiTreeUtil.processElements(statement, processor);
        }
        return processor.getCollection();
    }

    protected void reportError(Project project, Editor editor, String reason) {
        CommonRefactoringUtil.showErrorHint(project, editor,
                String.format("Cannot perform inline refactoring: %s", reason),
                "Cannot inline routine", null);
    }
}
