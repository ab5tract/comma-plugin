package org.raku.comma.refactoring.inline.variable;

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
import org.raku.comma.psi.*;
import org.raku.comma.refactoring.CompleteRakuElementFactory;
import org.raku.comma.refactoring.inline.RakuInlineProcessor;
import org.raku.comma.refactoring.inline.RakuInlineViewDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RakuInlineVariableProcessor extends RakuInlineProcessor {
    private final PsiElement myDeclaration;
    private final PsiElement myReference;
    private final Editor myEditor;
    private final boolean myInlineThisOnly;
    private final boolean myDeleteTheDeclaration;

    public RakuInlineVariableProcessor(Project project,
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
        return new RakuInlineViewDescriptor(myDeclaration);
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
            RakuVariableDecl decl = PsiTreeUtil.getNonStrictParentOfType(myDeclaration, RakuVariableDecl.class);

            PsiElement initializer = null;

            // If we work with a multi-declaration, it can be either a `my (...)` form or
            // a routine parameter
            if (myDeclaration instanceof RakuParameterVariable) {
                if (decl != null)
                // Presence of declaration means we are dealing with a multi-declaration,
                // so ask for particular initializer
                {
                    initializer = decl.getInitializer((RakuVariable)usageElement);
                }
                else {
                    // Ask for a parameter initializer
                    RakuParameter parameter = PsiTreeUtil.getNonStrictParentOfType(myDeclaration, RakuParameter.class);
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
                initializer = CompleteRakuElementFactory.createParenthesesExpr(initializer);
            else
                initializer = initializer.copy();

            inlineElement(usageElement, initializer);
        }

        PsiDocumentManager.getInstance(myProject).commitAllDocuments();

        if (myDeleteTheDeclaration) {
            deleteDeclaration();
        }
    }

    private static void inlineElement(PsiElement usageElement, PsiElement initializer) {
        PsiElement parent = usageElement.getParent();
        if (parent instanceof RakuColonPair) {
            // if ':$foo', we unwrap into foo => ...
            String key = ((RakuColonPair)parent).getKey();
            parent.replace(RakuElementFactory.createFatArrow(usageElement.getProject(), key, initializer));
        } else {
            usageElement.replace(initializer);
        }
    }

    private void deleteDeclaration() {
        if (myDeclaration instanceof RakuVariableDecl) {
            RakuStatement statement = PsiTreeUtil.getParentOfType(myDeclaration, RakuStatement.class);
            if (statement != null) statement.delete();
        }
        if (myDeclaration instanceof RakuParameterVariable) {
            RakuVariableDecl decl = PsiTreeUtil.getParentOfType(myDeclaration, RakuVariableDecl.class);
            if (decl != null)
                decl.removeVariable((RakuVariable)myReference);
            else {
                RakuParameter parameter = PsiTreeUtil.getParentOfType(myDeclaration, RakuParameter.class);
                assert parameter != null;
                RakuSignature signature = PsiTreeUtil.getParentOfType(parameter, RakuSignature.class);
                assert signature != null;
                List<RakuParameter> params = Arrays
                        .stream(signature.getParameters())
                        .filter(p -> !Objects.equals(p, parameter))
                        .collect(Collectors.toList());
                RakuSignature updatedSignature = RakuElementFactory.createRoutineSignature(
                        parameter.getProject(), params);
                signature.replace(updatedSignature);
            }
        }
        if (myDeclaration instanceof RakuRoutineDecl) {
            RakuStatement statement = PsiTreeUtil.getParentOfType(myDeclaration, RakuStatement.class);
            if (statement != null) statement.delete();
        }
    }

    @NotNull
    @Override
    protected String getCommandName() {
        return "Inline variable";
    }
}
