package org.raku.comma.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.raku.comma.psi.*;
import org.raku.comma.refactoring.RakuBlockRenderer;
import org.raku.comma.refactoring.RakuNameSuggester;
import org.raku.comma.refactoring.helpers.RakuIntroduceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.raku.comma.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public abstract class IntroduceHandler implements RefactoringActionHandler {
    private final IntroduceValidator myValidator;
    private final String myDialogTitle;

    protected IntroduceHandler(IntroduceValidator validator, String dialogTitle) {
        myValidator = validator;
        myDialogTitle = dialogTitle;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        performAction(new IntroduceOperation(project, editor, file, null));
    }

    protected void performAction(IntroduceOperation operation) {
        PsiFile file = operation.getFile();
        if (!CommonRefactoringUtil.checkReadOnlyStatus(file))
            return;

        Editor editor = operation.getEditor();
        if (editor.getSettings().isVariableInplaceRenameEnabled()) {
            TemplateState templateState = TemplateManagerImpl.getTemplateState(editor);
            if (templateState != null && !templateState.isFinished())
                return;
        }

        PsiElement element1 = null;
        PsiElement element2 = null;
        SelectionModel selectionModel = editor.getSelectionModel();

        if (selectionModel.hasSelection()) {
            element1 = file.findElementAt(selectionModel.getSelectionStart());
            element2 = file.findElementAt(selectionModel.getSelectionEnd() - 1);
            if (element1 instanceof PsiWhiteSpace || element1 != null && element1.getNode().getElementType() == UNV_WHITE_SPACE) {
                int startOffset = element1.getTextRange().getEndOffset();
                element1 = file.findElementAt(startOffset);
            }
            if (element2 instanceof PsiWhiteSpace || element2 != null && element2.getNode().getElementType() == UNV_WHITE_SPACE) {
                int endOffset = element2.getTextRange().getStartOffset();
                element2 = file.findElementAt(endOffset - 1);
            }
        } else {
            if (smartIntroduce(operation)) return;
            CaretModel caretModel = editor.getCaretModel();
            Document document = editor.getDocument();
            int lineNumber = document.getLineNumber(caretModel.getOffset());
            if ((lineNumber >= 0) && (lineNumber < document.getLineCount())) {
                element1 = file.findElementAt(document.getLineStartOffset(lineNumber));
                element2 = file.findElementAt(document.getLineEndOffset(lineNumber) - 1);
            }
        }

        final Project project = operation.getProject();
        if (element1 == null || element2 == null) {
            showCannotPerformError(project, editor);
            return;
        }

        element1 = PsiTreeUtil.getParentOfType(PsiTreeUtil.findCommonParent(element1, element2), RakuPsiElement.class, false);
        if (element1 == null || isValidIntroduceContext(element1) == null) {
            showCannotPerformError(project, editor);
            return;
        }

        operation.setElement(element1);
        performActionOnElement(operation);
    }

    private void showCannotPerformError(Project project, Editor editor) {
        CommonRefactoringUtil.showErrorHint(project, editor, "Cannot extract this code", myDialogTitle,
                                            "refactoring.extractVariable");
    }

    private boolean smartIntroduce(IntroduceOperation operation) {
        final Editor editor = operation.getEditor();
        final PsiFile file = operation.getFile();
        final int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = file.findElementAt(offset);
        if ((elementAtCaret instanceof PsiWhiteSpace && offset == elementAtCaret.getTextOffset() || elementAtCaret == null) && offset > 0) {
            elementAtCaret = file.findElementAt(offset - 1);
        }
        while (elementAtCaret != null && !(elementAtCaret instanceof RakuPsiElement)) {
            elementAtCaret = elementAtCaret.getParent();
        }
        if (elementAtCaret == null) {
            showCannotPerformError(operation.getProject(), editor);
            return true;
        }

        if (!checkIntroduceContext(file, editor, elementAtCaret)) return true;
        final List<PsiElement> expressions = new ArrayList<>();
        while (elementAtCaret != null) {
            if (elementAtCaret instanceof RakuStatement || elementAtCaret instanceof RakuFile)
                break;
            if (elementAtCaret instanceof RakuExtractable)
                expressions.add(elementAtCaret);
            if (elementAtCaret instanceof RakuMethodCall && !(elementAtCaret.getParent() instanceof RakuPostfixApplication))
                expressions.add(elementAtCaret);
            elementAtCaret = elementAtCaret.getParent();
        }

        if (expressions.size() == 1) {
            operation.setElement(expressions.get(0));
            performActionOnElement(operation);
            return true;
        } else if (expressions.size() > 1) {
            IntroduceTargetChooser.showChooser(editor, expressions, new Pass<>() {
                @Override
                public void pass(PsiElement element) {
                    operation.setElement(element);
                    performActionOnElement(operation);
                }
            }, psi -> psi.getText());
            return true;
        }
        return false;
    }

    private void performActionOnElement(IntroduceOperation operation) {
        PsiElement element = operation.getElement();
        operation.setInitializer(element);
        List<PsiElement> occurrences = getOccurrences(element, PsiTreeUtil.getParentOfType(element, RakuStatementList.class));
        if (occurrences.size() == 0) {
            showCannotPerformError(operation.getProject(), operation.getEditor());
            return;
        }
        operation.setOccurrences(occurrences);
        operation.setSuggestedNames(getSuggestedNames(element));
        if (operation.getOccurrences().size() == 0) {
            operation.setReplaceAll(false);
        }
        performActionOnElementOccurrences(operation);
    }

    protected void performActionOnElementOccurrences(IntroduceOperation operation) {
        Editor editor = operation.getEditor();
        if (editor.getSettings().isVariableInplaceRenameEnabled()) {
            ensureName(operation);
            if (operation.isReplaceAll() != null) {
                performScopeSelectionAndIntroduce(operation);
            } else {
                OccurrencesChooser.simpleChooser(editor).showChooser(operation.getElement(), operation.getOccurrences(), new Pass<>() {
                    @Override
                    public void pass(OccurrencesChooser.ReplaceChoice replaceChoice) {
                        operation.setReplaceAll(replaceChoice == OccurrencesChooser.ReplaceChoice.ALL);
                        performScopeSelectionAndIntroduce(operation);
                    }
                });
            }
        } else {
            performIntroduceWithDialog(operation);
        }
    }

    protected List<PsiElement> calculateAnchors(IntroduceOperation operation,
                                              PsiElement element, List<PsiElement> occurrences) {
        // Calculate innermost scope for all occurrences to extract
        RakuStatementList commonBase = PsiTreeUtil.getNonStrictParentOfType(
                PsiTreeUtil.findCommonParent(
                        operation.isReplaceAll()
                                ? occurrences
                                : Collections.singletonList(element)),
                RakuStatementList.class);
        if (commonBase == null) {
            showCannotPerformError(operation.getProject(), operation.getEditor());
            return null;
        }
        // Iterate all scopes above the innermost one,
        // calculating "insert before this" anchor using
        // the fact that it will always be an ancestor of
        // the first occurrence we have to extract
        List<PsiElement> anchors = new LinkedList<>();
        occurrences.sort(Comparator.comparingInt(PsiElement::getTextOffset));
        PsiElement earliestOccurrence = occurrences.get(0);
        while (commonBase != null) {
            for (PsiElement statement : commonBase.getChildren()) {
                if (PsiTreeUtil.isAncestor(statement, earliestOccurrence, false)) {
                    anchors.add(statement);
                    break;
                }
            }
            commonBase = PsiTreeUtil.getParentOfType(commonBase, RakuStatementList.class);
        }
        return anchors;
    }

    protected static void ensureName(IntroduceOperation operation) {
        if (operation.getName() == null) {
            Collection<String> suggestedNames = operation.getSuggestedNames();
            if (suggestedNames.size() > 0) {
                operation.setName(suggestedNames.iterator().next());
            } else {
                operation.setName("$x");
            }
        }
    }

    private void performIntroduceWithDialog(IntroduceOperation operation) {
        Project project = operation.getProject();
        if (operation.getName() == null) {
            RakuIntroduceDialog dialog = new RakuIntroduceDialog(project, myDialogTitle,
                                                                 myValidator, getHelpId(),
                                                                 operation.getSuggestedNames());
            if (!dialog.showAndGet())
                return;
            operation.setName(dialog.getName());
            operation.setReplaceAll(true);
        }

        PsiElement declaration = performRefactoring(operation);
        removeLeftoverStatement(operation);
        Editor editor = operation.getEditor();
        editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
        editor.getSelectionModel().removeSelection();
    }

    protected abstract String getHelpId();

    private void performScopeSelectionAndIntroduce(IntroduceOperation operation) {
        List<PsiElement> anchors = calculateAnchors(operation, operation.getElement(), operation.getOccurrences());
        if (anchors == null) {
            showCannotPerformError(operation.getProject(), operation.getEditor());
            return;
        }

        // Selection goes on psi scopes, but we collected suitable anchors
        // Ultimately, we want to know selected scope index to set corresponding anchor
        Map<RakuPsiScope, PsiElement> anchorByScope = new HashMap<>();
        List<RakuPsiScope> scopes = new LinkedList<>();
        anchors.forEach(anchor -> {
            RakuPsiScope scope = PsiTreeUtil.getParentOfType(anchor, RakuPsiScope.class);
            scopes.add(scope);
            anchorByScope.put(scope, anchor);
        });

        IntroduceTargetChooser.showChooser(operation.getEditor(),
                                           scopes, new Pass<>() {
                @Override
                public void pass(RakuPsiScope scope) {
                    operation.setAnchor(anchorByScope.get(scope));
                    performInplaceIntroduce(operation);
                }
            }, RakuBlockRenderer::renderBlock, "Scope");
    }

    private void performInplaceIntroduce(IntroduceOperation operation) {
        PsiElement element = performRefactoring(operation);
        PsiNamedElement declaration = PsiTreeUtil.findChildOfAnyType(element, RakuVariableDecl.class, RakuConstant.class);
        if (element == null || declaration == null) {
            showCannotPerformError(operation.getProject(), operation.getEditor());
            return;
        }
        if (operation.isOccurrencesReplaceable()) {
            List<PsiElement> occurrences = operation.getOccurrences();
            Editor editor = operation.getEditor();
            PsiNamedElement occurrence = (PsiNamedElement)findOccurrenceUnderCaret(occurrences, editor);
            if (occurrence == null) {
                showCannotPerformError(operation.getProject(), operation.getEditor());
                return;
            }
            editor.getCaretModel().moveToOffset(occurrence.getTextRange().getStartOffset());
            final InplaceVariableIntroducer<PsiElement> introducer =
                new Perl6InplaceVariableIntroducer(declaration, operation, occurrences);
            Collection<String> names = operation.getSuggestedNames();
            introducer.performInplaceRefactoring(new LinkedHashSet<>(names));
        } else {
            removeLeftoverStatement(operation);
        }
    }

    protected static void removeLeftoverStatement(IntroduceOperation operation) {
        PsiElement initializer = operation.getInitializer();
        RakuStatement statement = PsiTreeUtil.getParentOfType(initializer, RakuStatement.class, false);
        if (statement != null) {
            WriteCommandAction.writeCommandAction(operation.getProject()).run(() -> {
                if (statement.getPrevSibling() instanceof PsiWhiteSpace) {
                    statement.getPrevSibling().delete();
                }
                statement.delete();
            });
        }
    }

    private static PsiElement findOccurrenceUnderCaret(List<PsiElement> occurrences, Editor editor) {
        if (occurrences.isEmpty()) return null;
        int offset = editor.getCaretModel().getOffset();
        for (PsiElement occurrence : occurrences) {
            if (occurrence != null && occurrence.getTextRange().containsOffset(offset))
                return occurrence;
        }
        return null;
    }

    protected PsiElement performRefactoring(IntroduceOperation operation) {
        PsiElement declaration = createDeclaration(operation);
        declaration = performReplace(declaration, operation);
        declaration = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(declaration);
        return declaration;
    }

    private PsiElement performReplace(PsiElement declaration, IntroduceOperation operation) {
        PsiElement expression = operation.getInitializer();
        Project project = operation.getProject();
        return WriteCommandAction.writeCommandAction(project).compute(() -> {
            PsiElement anchor = operation.getAnchor();
            try {
                RefactoringEventData afterData = new RefactoringEventData();
                afterData.addElement(declaration);
                project.getMessageBus().syncPublisher(RefactoringEventListener.REFACTORING_EVENT_TOPIC)
                    .refactoringStarted(getRefactoringId(), afterData);
                PsiElement newExpression = createExpression(project, operation.getName());

                PsiElement operationElement = operation.getElement();
                PsiElement list = PsiTreeUtil.getNonStrictParentOfType(operationElement, RakuStatementList.class);
                boolean needsToBeReplaced = !(operationElement.getParent() instanceof RakuStatementList ||
                                              operationElement.getParent().getParent() instanceof RakuStatementList);
                operation.setOccurrencesReplaceable(needsToBeReplaced);

                if (needsToBeReplaced) {
                    if (operation.isReplaceAll()) {
                        List<PsiElement> newOccurrences = new ArrayList<>();
                        for (PsiElement occurrence : operation.getOccurrences()) {
                            PsiElement replaced = replaceExpression(occurrence, newExpression);
                            if (replaced != null)
                                newOccurrences.add(replaced);
                        }
                        operation.setOccurrences(newOccurrences);
                    }
                    else {
                        PsiElement replaced = replaceExpression(expression, newExpression);
                        operation.setOccurrences(Collections.singletonList(replaced));
                    }
                }
                postRefactoring(list, operation);
            } finally {
                final RefactoringEventData afterData = new RefactoringEventData();
                afterData.addElement(declaration);
                project.getMessageBus().syncPublisher(RefactoringEventListener.REFACTORING_EVENT_TOPIC)
                    .refactoringDone(getRefactoringId(), afterData);
            }
            return anchor.getParent().addBefore(declaration, anchor);
        });
    }

    protected void postRefactoring(PsiElement statementList, IntroduceOperation operation) {
        if (statementList != null) {
            PsiDocumentManager.getInstance(operation.getProject()).doPostponedOperationsAndUnblockDocument(operation.getEditor().getDocument());
            CodeStyleManager.getInstance(operation.getProject()).reformat(statementList);
        }
    }

    private static PsiElement replaceExpression(PsiElement expression, PsiElement newExpression) {
        return expression.replace(newExpression);
    }

    private static RakuVariable createExpression(Project project, String name) {
        return RakuElementFactory.createVariable(project, name);
    }

    protected abstract String getRefactoringId();

    @NotNull
    private static PsiElement findTopmostStatementOfExpression(PsiElement expression) {
        // If we are extracting a whole statement, it is already a good enough anchor,
        // but if it is a statement in some other context (say, hash index), we need to search for another one
        if (expression instanceof RakuStatement && expression.getParent() instanceof RakuStatementList)
            return expression;

        PsiElement anchor = expression;
        do {
            // It is possible that first element will be a statement already,
            // so we are skipping it and making expression null
            if (expression instanceof RakuStatement) {
                anchor = anchor.getParent();
                expression = null;
            } else {
                RakuStatement tempAnchor = PsiTreeUtil.getParentOfType(anchor, RakuStatement.class);
                if (tempAnchor == null)
                    break;
                anchor = tempAnchor;
            }
        } while (!(anchor.getParent() instanceof RakuStatementList));
        return anchor;
    }

    protected abstract PsiElement createDeclaration(IntroduceOperation operation);

    private static Collection<String> getSuggestedNames(PsiElement element) {
        return RakuNameSuggester.suggest(element);
    }

    private static List<PsiElement> getOccurrences(PsiElement element, PsiElement scope) {
        List<PsiElement> occurrences = new ArrayList<>();
        Deque<PsiElement> toWalk = new ArrayDeque<>();
        toWalk.add(scope);
        while (!toWalk.isEmpty()) {
            PsiElement el = toWalk.pop();
            if (PsiEquivalenceUtil.areElementsEquivalent(el, element)) {
                occurrences.add(el);
            } else {
                toWalk.addAll(Arrays.asList(el.getChildren()));
            }
        }
        return occurrences;
    }

    private boolean checkIntroduceContext(PsiFile file, Editor editor, PsiElement element) {
        element = isValidIntroduceContext(element);
        if (element == null) {
            CommonRefactoringUtil.showErrorHint(file.getProject(), editor, "Cannot refactor with this selection",
                                                myDialogTitle, "refactoring.extractMethod");
            return false;
        }
        return true;
    }

    private static PsiElement isValidIntroduceContext(PsiElement element) {
        boolean valid = element instanceof RakuExtractable;
        if (element instanceof RakuTypeName) {
            valid = !(element.getParent() instanceof RakuParameter) && !(element.getParent() instanceof RakuScopedDecl);
        } else if (element instanceof RakuVariable) {
            valid = !(element.getParent() instanceof RakuVariableDecl) && !(element.getParent() instanceof RakuParameterVariable);
        } else if (element instanceof RakuLongName) {
            return PsiTreeUtil.getParentOfType(element, RakuPostfixApplication.class);
        }
        return valid ? element : null;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
    }

    public enum InitPlace {
        SAME_BLOCK
    }

    private static class Perl6InplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
        Perl6InplaceVariableIntroducer(PsiNamedElement occurrence, IntroduceOperation operation, List<PsiElement> occurrences) {
            super(occurrence, operation.getEditor(), operation.getProject(), "Introduce variable", occurrences.toArray(PsiElement.EMPTY_ARRAY), null);
        }
    }
}
