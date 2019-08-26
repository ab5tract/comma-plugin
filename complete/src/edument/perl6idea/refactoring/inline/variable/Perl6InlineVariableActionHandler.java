package edument.perl6idea.refactoring.inline.variable;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.inline.IllegalInlineeException;
import edument.perl6idea.refactoring.inline.Perl6InlineActionHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Perl6InlineVariableActionHandler extends Perl6InlineActionHandler {
    @Override
    public boolean canInlineElement(PsiElement element) {
        return (element instanceof Perl6VariableDecl || element instanceof Perl6ParameterVariable) &&
               element.getLanguage() instanceof Perl6Language;
    }

    @Override
    public void inlineElement(Project project, Editor editor, PsiElement element) {
        PsiReference reference = editor != null ?
                                 TargetElementUtil.findReference(editor, editor.getCaretModel().getOffset()) : null;

        boolean invokedOnDeclaration = reference == null;

        if (reference != null && PsiTreeUtil.isAncestor(element, reference.getElement(), false)) {
            invokedOnDeclaration = true;
        }

        PsiElement initializer;

        if (element instanceof Perl6VariableDecl) {
            initializer = ((Perl6VariableDecl) element).getInitializer();
        } else if (element instanceof Perl6ParameterVariable) {
            Perl6Parameter parameter = PsiTreeUtil.getParentOfType(element, Perl6Parameter.class);
            initializer = parameter != null ? parameter.getInitializer() : null;
        } else {
            reportError(project, editor, "refactoring is unsupported here");
            return;
        }

        if (initializer == null) {
            reportError(project, editor, "refactoring is supported only when the initializer is present");
            return;
        }

        Collection<PsiReference> usages = invokedOnDeclaration ?
                                          ReferencesSearch.search(element, GlobalSearchScope.projectScope(project)).findAll() :
                                          Collections.singletonList(reference);

        for (PsiReference callRef : usages) {
            PsiElement variable = callRef.getElement();
            if (checkIfVariableIsLeftValue(variable)) {
                reportError(project, editor,"variable to be inlined has occurrences as lvalue" );
                return;
            }
        }

        try {
            checkUnresolvedElements((Perl6PsiElement)element, reference);
        }
        catch (IllegalInlineeException ex) {
            reportError(project, editor, ex.toString());
            return;
        }

        PsiElement refElement = null;
        if (reference != null) {
            refElement = reference.getElement();
        }

        Perl6InlineVariableDialog dialog = new Perl6InlineVariableDialog(project, element, refElement, editor, invokedOnDeclaration);
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            dialog.doAction();
        } else {
            dialog.show();
        }
    }

    private static boolean checkIfVariableIsLeftValue(PsiElement usageElement) {
        PsiElement refParent = usageElement == null ? null : usageElement.getParent();
        if (!(refParent instanceof Perl6InfixApplication))
            return false;

        Perl6InfixApplication application = (Perl6InfixApplication)refParent;
        PsiElement[] operands = application.getOperands();
        if (operands.length < 2 || !application.getOperator().equals("="))
            return false;

        for (PsiElement operand : operands) {
            if (Objects.equals(operand, usageElement))
                return true;
        }
        return false;
    }

    protected void reportError(Project project, Editor editor, String reason) {
        CommonRefactoringUtil.showErrorHint(project, editor,
                String.format("Cannot perform inline refactoring: %s", reason),
                "Cannot inline variable", null);
    }
}
