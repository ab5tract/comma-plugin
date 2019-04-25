package edument.perl6idea.refactoring.inline;

import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.CompletePerl6ElementFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Perl6InlineHandler extends InlineActionHandler {
    public static final String CRITICAL_ERROR_TITLE = "Cannot inline variable";
    private boolean isRoutineParameter;

    @Override
    public boolean isEnabledForLanguage(Language lang) {
        return lang instanceof Perl6Language;
    }

    @Override
    public boolean canInlineElement(PsiElement element) {
        return element instanceof Perl6Variable || element instanceof Perl6VariableDecl;
    }

    @Override
    public void inlineElement(Project project, Editor editor, PsiElement element) {
        if (element instanceof Perl6VariableDecl || element instanceof Perl6ParameterVariable) {
            inlineVariable(project, editor, (PsiNamedElement) element);
        }
    }

    private void inlineVariable(Project project, Editor editor, PsiNamedElement variable) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            /* General algorithm is described in this method
             * All specific details for various situations are delegated to
             * the particular method */

            Perl6Infix assignment = null;

            // Get an initializer value to inline it
            PsiElement initValue = getInplaceInitializer(variable);
            // If it is not found, search for a single assignment
            if (initValue == null)
                assignment = findSingleAssignment(project, editor, variable);
            // If there is no suitable, single assignment, we can't continue
            if (initValue == null && assignment == null)
                return;

            // Find all usages and replace those with initializer found
            replaceUsagesWithInit(variable, initValue, assignment);

            // Remove obsolete code pieces
            update(variable, initValue, assignment);
        });
    }

    private void replaceUsagesWithInit(PsiNamedElement variable, @Nullable PsiElement init, @Nullable Perl6Infix assignment) {
        PsiElement value;
        if (init != null)
            value = init;
        else
            value = assignment.getRightSide();

        for (PsiReference usage : ReferencesSearch.search(variable).findAll()) {
            usage.getElement().replace(value.copy());
        }
    }

    private Perl6Infix findSingleAssignment(Project project, Editor editor, PsiNamedElement variable) {
        List<Perl6Infix> initializers = getAllInitializers(variable);
        if (initializers.isEmpty()) {
            reportError(project, editor, variable.getName(), "the variable is not initialized");
        } else if (initializers.size() == 1) {
            return initializers.get(0);
        } else {
            reportError(project, editor, variable.getName(), "the variable has many initializers");
        }
        return null;
    }

    private static List<Perl6Infix> getAllInitializers(PsiElement element) {
        List<Perl6Infix> assignments = new ArrayList<>();
        for (PsiReference usage : ReferencesSearch.search(element).findAll()) {
            Perl6PsiElement usageElement = (Perl6PsiElement) usage.getElement();
            PsiElement maybeInfix = usageElement.skipWhitespacesForward();
            if (maybeInfix instanceof Perl6Infix && ((Perl6Infix) maybeInfix).getOperator().equals("=")) {
                assignments.add((Perl6Infix) maybeInfix);
            }
        }
        return assignments;
    }

    private void update(PsiNamedElement variable, @Nullable PsiElement init, @Nullable Perl6Infix assignment) {
        if (variable instanceof Perl6VariableDecl) {
            updateVariable(variable, assignment);
        } else if (variable instanceof Perl6ParameterVariable) {
            updateParameter(variable);
        }
    }

    private void updateParameter(PsiNamedElement variable) {
        if (isRoutineParameter) {
            // We are updating a routine parameter
            updateRoutineParameter(variable);
        } else {
            // We are updating multi-variable declaration
            updateMultiDeclarationParameter(variable);
        }
    }

    private void updateMultiDeclarationParameter(PsiNamedElement variable) {
        Perl6VariableDecl decl = PsiTreeUtil.getParentOfType(variable, Perl6VariableDecl.class);
        if (decl == null) return;

        // Should we enclose resulting variable list with parens or no
        boolean shouldEnclose = false;

        StringJoiner signature = new StringJoiner(", ");
        String nameToAvoid = variable.getName();
        for (PsiElement child : decl.getChildren()) {
            if (child instanceof Perl6Signature) {
                Perl6Parameter[] parameters = ((Perl6Signature) child).getParameters();
                for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                    shouldEnclose = parametersLength - 1 != 1;
                    Perl6Parameter parameter = parameters[i];
                    if (!Objects.equals(parameter.getVariableName(), nameToAvoid))
                        signature.add(parameter.getVariableName());
                }
                break;
            }
        }

        // Get part of multi-initializer that we have to exclude
        PsiElement deleteInit = decl.getInitializer(variable);

        // Get all pieces, excluding deleteInit
        List<PsiElement> initPartsToPreserve = new ArrayList<>();
        for (PsiElement initNode : decl.getInitializer().getChildren()) {
            if (!Objects.equals(initNode, deleteInit) && !(initNode instanceof Perl6Infix))
                initPartsToPreserve.add(initNode);
        }
        // If we have only a single value left after exclusion, it is not InfixApplication anymore,
        // so we can just replace it with the value directly
        if (initPartsToPreserve.size() == 1) {
            decl.getInitializer().replace(initPartsToPreserve.get(0));
        } else {
            // Otherwise, create a new application and use it
            Perl6InfixApplication newApplication = CompletePerl6ElementFactory.
                    createInfixApplication(decl.getProject(), initPartsToPreserve);
            decl.getInitializer().replace(newApplication);
        }

        PsiElement newDeclaration = CompletePerl6ElementFactory.createVariableAssignment(
                variable.getProject(),
                String.format(shouldEnclose ? "(%s)" : "%s", signature.toString()),
                decl.getInitializer().getText(), false);

        PsiTreeUtil.getParentOfType(decl, Perl6Statement.class).replace(newDeclaration);
    }

    private void updateRoutineParameter(PsiNamedElement variable) {
        Perl6Parameter parameter = PsiTreeUtil.getParentOfType(variable, Perl6Parameter.class);
        assert parameter != null;
        Perl6Signature signature = PsiTreeUtil.getParentOfType(parameter, Perl6Signature.class);
        assert signature != null;
        List<Perl6Parameter> params = Arrays
                .stream(signature.getParameters())
                .filter(p -> !Objects.equals(p, parameter))
                .collect(Collectors.toList());
        Perl6Signature updatedSignature = CompletePerl6ElementFactory.createRoutineSignature(
                parameter.getProject(), params);
        signature.replace(updatedSignature);
    }

    private void updateVariable(PsiNamedElement variable, @Nullable Perl6Infix assignment) {
        // Delete first assignment if any
        if (assignment != null)
            PsiTreeUtil.getParentOfType(assignment, Perl6Statement.class).delete();
        // Delete variable declaration
        PsiTreeUtil.getParentOfType(variable, Perl6Statement.class).delete();
    }

    @Nullable
    private PsiElement getInplaceInitializer(PsiNamedElement variable) {
        if (variable instanceof Perl6VariableDecl) {
            return ((Perl6VariableDecl) variable).getInitializer(variable);
        } else if (variable instanceof Perl6ParameterVariable) {
            Perl6Statement statement = PsiTreeUtil.getParentOfType(variable, Perl6Statement.class);
            // We are declaring a routine, so might have a default
            isRoutineParameter = PsiTreeUtil.findChildOfType(statement, Perl6RoutineDecl.class) != null;
            if (isRoutineParameter) {
                Perl6Parameter parameter = PsiTreeUtil.getParentOfType(variable, Perl6Parameter.class);
                assert parameter != null;
                return parameter.getInitializer();
            } else {
                // If we are declaring list of variables with `my ($a, $b)`, ask Perl6VariableDecl
                Perl6VariableDecl decl = PsiTreeUtil.getParentOfType(variable, Perl6VariableDecl.class);
                if (decl != null)
                    return decl.getInitializer(variable);
            }
        }
        return null;
    }

    private static void reportError(Project project, Editor editor, String name, String reason) {
        CommonRefactoringUtil.showErrorHint(project, editor,
                String.format("Cannot inline the variable %s: %s", name, reason),
                CRITICAL_ERROR_TITLE, null);
    }
}
