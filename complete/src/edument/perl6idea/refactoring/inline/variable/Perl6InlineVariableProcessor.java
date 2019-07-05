package edument.perl6idea.refactoring.inline.variable;

import com.intellij.history.LocalHistory;
import com.intellij.history.LocalHistoryAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.usageView.UsageViewDescriptor;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.CompletePerl6ElementFactory;
import edument.perl6idea.refactoring.inline.Perl6InlineProcessor;
import edument.perl6idea.refactoring.inline.Perl6InlineViewDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Perl6InlineVariableProcessor extends Perl6InlineProcessor {
    private final PsiElement myDeclaration;
    private final PsiElement myReference;
    private final Editor myEditor;
    private final boolean myInlineThisOnly;
    private final boolean myDeleteTheDeclaration;

    public Perl6InlineVariableProcessor(Project project,
                                        PsiElement decl,
                                        PsiElement reference,
                                        Editor editor,
                                        boolean inlineThisOnly,
                                        boolean isDeleteTheDeclaration) {
        super(project);
        myDeclaration = decl;
        myReference = reference;
        myEditor = editor;
        myInlineThisOnly = inlineThisOnly;
        myDeleteTheDeclaration = isDeleteTheDeclaration;
    }

    @NotNull
    @Override
    protected UsageViewDescriptor createUsageViewDescriptor(@NotNull UsageInfo[] usages) {
        return new Perl6InlineViewDescriptor(myDeclaration);
    }

    @NotNull
    @Override
    protected UsageInfo[] findUsages() {
        if (myInlineThisOnly) {
            return new UsageInfo[]{new UsageInfo(myReference)};
        }
        Set<UsageInfo> usages = new HashSet<>();

        for (PsiReference reference : ReferencesSearch.search(myDeclaration, GlobalSearchScope.projectScope(myProject))) {
            if (!PsiTreeUtil.isAncestor(myDeclaration, reference.getElement(), false))
                usages.add(new UsageInfo(reference.getElement()));
        }

        return usages.toArray(UsageInfo.EMPTY_ARRAY);
    }

    @Override
    protected void performRefactoring(@NotNull UsageInfo[] usages) {
        RangeMarker position = null;
        if (myEditor != null) {
            final int offset = myEditor.getCaretModel().getOffset();
            position = myEditor.getDocument().createRangeMarker(offset, offset + 1);
        }

        LocalHistoryAction action = LocalHistory.getInstance().startAction(getCommandName());
        try {
            doRefactoring(usages);
        }
        finally {
            action.finish();
        }

        if (position != null) {
            if (position.isValid()) {
                myEditor.getCaretModel().moveToOffset(position.getStartOffset());
            }
            position.dispose();
        }
    }

    private void doRefactoring(UsageInfo[] usages) {
        for (UsageInfo usage : usages) {
            PsiElement usageElement = usage.getElement();
            assert usageElement != null;
            Perl6VariableDecl decl = PsiTreeUtil.getNonStrictParentOfType(myDeclaration, Perl6VariableDecl.class);

            PsiElement initializer = null;

            // If we work with a multi-declaration, it can be either a `my (...)` form or
            // a routine parameter
            if (myDeclaration instanceof Perl6ParameterVariable) {
                if (decl != null)
                // Presence of declaration means we are dealing with a multi-declaration,
                // so ask for particular initializer
                {
                    initializer = decl.getInitializer((Perl6Variable)usageElement);
                }
                else {
                    // Ask for a parameter initializer
                    Perl6Parameter parameter = PsiTreeUtil.getNonStrictParentOfType(myDeclaration, Perl6Parameter.class);
                    if (parameter != null)
                        initializer = parameter.getInitializer();
                }
            }
            else if (decl != null) {
                // If just a simple variable declaration, inline its initializer
                initializer = decl.getInitializer();
            }

            assert initializer != null;
            if (checkIfNeedToWrap(initializer))
                initializer = CompletePerl6ElementFactory.createParenthesesExpr(initializer);
            else
                initializer = initializer.copy();

            inlineElement(usageElement, initializer);
        }

        PsiDocumentManager.getInstance(myProject).commitAllDocuments();

        if (myDeleteTheDeclaration) {
            deleteDeclaration();
        }
    }

    private void inlineElement(PsiElement usageElement, PsiElement initializer) {
        PsiElement parent = usageElement.getParent();
        if (parent instanceof Perl6ColonPair) {
            // if ':$foo', we unwrap into foo => ...
            String key = ((Perl6ColonPair)parent).getKey();
            parent.replace(CompletePerl6ElementFactory.createFatArrow(usageElement.getProject(), key, initializer));
        } else {
            usageElement.replace(initializer);
        }
    }

    private void deleteDeclaration() {
        if (myDeclaration instanceof Perl6VariableDecl) {
            Perl6Statement statement = PsiTreeUtil.getParentOfType(myDeclaration, Perl6Statement.class);
            statement.delete();
        }
        if (myDeclaration instanceof Perl6ParameterVariable) {
            Perl6VariableDecl decl = PsiTreeUtil.getParentOfType(myDeclaration, Perl6VariableDecl.class);
            if (decl != null)
                decl.removeVariable((Perl6Variable)myReference);
            else {
                Perl6Parameter parameter = PsiTreeUtil.getParentOfType(myDeclaration, Perl6Parameter.class);
                assert parameter != null;
                Perl6Signature signature = PsiTreeUtil.getParentOfType(parameter, Perl6Signature.class);
                assert signature != null;
                List<Perl6Parameter> params = Arrays
                        .stream(signature.getParameters())
                        .filter(p -> !Objects.equals(p, parameter))
                        .collect(Collectors.toList());
                Perl6Signature updatedSignature = Perl6ElementFactory.createRoutineSignature(
                        parameter.getProject(), params);
                signature.replace(updatedSignature);
            }
        }
        if (myDeclaration instanceof Perl6RoutineDecl) {
            Perl6Statement statement = PsiTreeUtil.getParentOfType(myDeclaration, Perl6Statement.class);
            if (statement != null) statement.delete();
        }
    }

    @NotNull
    @Override
    protected String getCommandName() {
        return "Inline variable";
    }
}
