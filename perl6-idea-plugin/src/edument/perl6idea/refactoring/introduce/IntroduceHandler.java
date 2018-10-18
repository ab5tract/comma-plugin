package edument.perl6idea.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
            if (element1 instanceof PsiWhiteSpace) {
                int startOffset = element1.getTextRange().getEndOffset();
                element1 = file.findElementAt(startOffset);
            }
            if (element2 instanceof PsiWhiteSpace) {
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

        element1 = PsiTreeUtil.findCommonParent(element1, element2);
        if (element1 == null) {
            showCannotPerformError(project, editor);
        }

        operation.setElement(element1);
        performActionOnElement(operation);
    }

    private void showCannotPerformError(Project project, Editor editor) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot extract", myDialogTitle,
                                                "refactoring.extractMethod");
    }

    private boolean smartIntroduce(IntroduceOperation operation) {
        final Editor editor = operation.getEditor();
        final PsiFile file = operation.getFile();
        final int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = file.findElementAt(offset);
        if ((elementAtCaret instanceof PsiWhiteSpace && offset == elementAtCaret.getTextOffset() || elementAtCaret == null) && offset > 0) {
            elementAtCaret = file.findElementAt(offset - 1);
        }
        if (!checkIntroduceContext(file, editor, elementAtCaret)) return true;
        final List<PsiElement> expressions = new ArrayList<>();
        while (elementAtCaret != null) {
            if (elementAtCaret instanceof Perl6Statement || elementAtCaret instanceof Perl6File)
                break;
            expressions.add(elementAtCaret);
            elementAtCaret = elementAtCaret.getParent();
        }

        if (expressions.size() == 1) {
            operation.setElement(expressions.get(0));
            performActionOnElement(operation);
            return true;
        } else if (expressions.size() > 1) {
            IntroduceTargetChooser.showChooser(editor, expressions, new Pass<PsiElement>() {
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
        operation.setOccurrences(getOccurrences(element, PsiTreeUtil.getParentOfType(element, Perl6StatementList.class)));
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
                performInplaceIntroduce(operation);
            } else {
                OccurrencesChooser.simpleChooser(editor).showChooser(operation.getElement(), operation.getOccurrences(), new Pass<OccurrencesChooser.ReplaceChoice>() {
                    @Override
                    public void pass(OccurrencesChooser.ReplaceChoice replaceChoice) {
                        operation.setReplaceAll(replaceChoice == OccurrencesChooser.ReplaceChoice.ALL);
                        performInplaceIntroduce(operation);
                    }
                });
            }
        } else {
            performIntroduceWithDialog(operation);
        }
    }

    protected static void ensureName(IntroduceOperation operation) {
        if (operation.getName() == null) {
            Collection<String> suggestednames = operation.getSuggestedNames();
            if (suggestednames.size() > 0) {
                operation.setName(suggestednames.iterator().next());
            } else {
                operation.setName("$x");
            }
        }
    }

    private void performIntroduceWithDialog(IntroduceOperation operation) {
        Project project = operation.getProject();
        if (operation.getName() == null) {
            Perl6IntroduceDialog dialog = new Perl6IntroduceDialog(project, myDialogTitle,
                                                                   myValidator, getHelpId(),
                                                                   operation);
            if (!dialog.showAndGet())
                return;
            operation.setName(dialog.getName());
            operation.setReplaceAll(true);
        }

        PsiElement declaration = performRefactoring(operation);
        Editor editor = operation.getEditor();
        editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
        editor.getSelectionModel().removeSelection();
    }

    protected abstract String getHelpId();

    private void performInplaceIntroduce(IntroduceOperation operation) {
        PsiElement statement = performRefactoring(operation);
        if (statement instanceof Perl6Statement) {
            List<PsiElement> occurrences = operation.getOccurrences();
            Editor editor = operation.getEditor();
            PsiNamedElement occurrence = (PsiNamedElement)findOccurrenceUnderCaret(occurrences, editor);
            editor.getCaretModel().moveToOffset(occurrence.getTextRange().getStartOffset());
            final InplaceVariableIntroducer<PsiElement> introducer =
                new Perl6InplaceVariableIntroducer(occurrence, operation, occurrences);
            introducer.performInplaceRefactoring(new LinkedHashSet<>(operation.getSuggestedNames()));
        }
    }

    private static PsiElement findOccurrenceUnderCaret(List<PsiElement> occurrences, Editor editor) {
        if (occurrences.isEmpty()) return null;
        int offset = editor.getCaretModel().getOffset();
        for (PsiElement occurrence : occurrences) {
            if (occurrence != null && occurrence.getTextRange().contains(offset - 1))
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
        return new WriteCommandAction<PsiElement>(project, expression.getContainingFile()) {
            @Override
            protected void run(@NotNull Result<PsiElement> result) throws Throwable {
                try {
                    RefactoringEventData afterData = new RefactoringEventData();
                    afterData.addElement(declaration);
                    project.getMessageBus().syncPublisher(RefactoringEventListener.REFACTORING_EVENT_TOPIC)
                           .refactoringStarted(getRefactoringId(), afterData);
                    result.setResult(addDeclaration(operation, declaration));
                    PsiElement newExpression = createExpression(project, operation.getName());

                    if (operation.isReplaceAll()) {
                        List<PsiElement> newOccurrences = new ArrayList<>();
                        for (PsiElement occurrence : operation.getOccurrences()) {
                            PsiElement replaced = replaceExpression(occurrence, newExpression, operation);
                            if (replaced != null)
                                newOccurrences.add(replaced);
                        }
                        operation.setOccurrences(newOccurrences);
                    }
                    else {
                        PsiElement replaced = replaceExpression(expression, newExpression, operation);
                        operation.setOccurrences(Collections.singletonList(replaced));
                    }
                    postRefactoring(operation.getElement());
                } finally {
                    final RefactoringEventData afterData = new RefactoringEventData();
                    afterData.addElement(declaration);
                    project.getMessageBus().syncPublisher(RefactoringEventListener.REFACTORING_EVENT_TOPIC)
                           .refactoringDone(getRefactoringId(), afterData);
                }
            }
        }.execute().getResultObject();
    }

    protected void postRefactoring(PsiElement element) {}

    private static PsiElement replaceExpression(PsiElement expression, PsiElement newExpression, IntroduceOperation operation) {
        return expression.replace(newExpression);
    }

    private static PsiElement createExpression(Project project, String name) {
        return Perl6ElementFactory.createVariable(project, name);
    }

    private static PsiElement addDeclaration(IntroduceOperation operation, PsiElement declaration) {
        PsiElement expression = operation.getInitializer();
        return addDeclaration(expression, declaration, operation.getOccurrences(), operation.isReplaceAll());
    }

    protected abstract String getRefactoringId();

    protected static PsiElement addDeclaration(PsiElement expression,
                                               PsiElement declaration,
                                               List<PsiElement> occurrences,
                                               Boolean all) {
        PsiElement anchor = all ? findAnchor(occurrences) : PsiTreeUtil.getParentOfType(expression, Perl6Statement.class);
        assert anchor != null;
        PsiElement parent = anchor.getParent();
        return parent.addBefore(declaration, anchor);
    }

    protected abstract PsiElement createDeclaration(IntroduceOperation operation);

    private static Collection<String> getSuggestedNames(PsiElement element) {
        return generateSuggestedNames(element);
    }

    private static Collection<String> generateSuggestedNames(PsiElement element) {
        // TODO Smarter heuristic can be used here
        return new ArrayList<>(Collections.singletonList("$x"));
    }

    private static List<PsiElement> getOccurrences(PsiElement element, PsiElement scope) {
        List<PsiElement> occurrences = new ArrayList<>();
        Stack<PsiElement> toWalk = new Stack<>();
        toWalk.add(scope);
        while (!toWalk.empty()) {
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
        if (!isValidIntroduceContext(element)) {
            CommonRefactoringUtil.showErrorHint(file.getProject(), editor, "Cannot refactor with this selection",
                                                myDialogTitle, "refactoring.extractMethod");
            return false;
        }
        return true;
    }

    protected static PsiElement findAnchor(List<PsiElement> occurrences) {
        occurrences.sort(Comparator.comparingInt(PsiElement::getTextOffset));
        PsiElement element = occurrences.get(0);
        if (element == null) return null;
        return PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
    }

    private static boolean isValidIntroduceContext(PsiElement element) {
        // STUB
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
    }

    public enum InitPlace {
        SAME_BLOCK
    }

    private static class Perl6InplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
        public Perl6InplaceVariableIntroducer(PsiNamedElement occurrence, IntroduceOperation operation, List<PsiElement> occurrences) {
            super(occurrence, operation.getEditor(), operation.getProject(), "Introduce variable", occurrences.toArray(PsiElement.EMPTY_ARRAY), null);
        }
    }
}
