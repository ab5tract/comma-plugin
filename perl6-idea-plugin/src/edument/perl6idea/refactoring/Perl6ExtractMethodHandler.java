package edument.perl6idea.refactoring;

import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiExpressionTrimRenderer;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import edument.perl6idea.psi.P6Extractable;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6ExtractMethodHandler implements RefactoringActionHandler, ContextAwareActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        final Pass<PsiElement[]> callback = new Pass<PsiElement[]>() {
            public void pass(final PsiElement[] selectedValue) {
                invokeOnElements(project, editor, file, selectedValue);
            }
        };
        selectAndPass(project, editor, file, callback);
    }

    private void selectAndPass(Project project, Editor editor, PsiFile file, Pass<PsiElement[]> callback) {
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        if (!editor.getSelectionModel().hasSelection()) {
            final int offset = editor.getCaretModel().getOffset();
            final List<Perl6PsiElement> expressions = collectExpressions(file, editor, offset);
            PsiDocumentManager.getInstance(project).commitAllDocuments();
            if (expressions.isEmpty()) {
                // TODO Throw an exception here
            } else if (expressions.size() == 1) {
                callback.pass(new PsiElement[]{expressions.get(0)});
                return;
            } else {
                IntroduceTargetChooser.showChooser(editor, expressions, new Pass<Perl6PsiElement>() {
                    @Override
                    public void pass(Perl6PsiElement perl6PsiElement) {
                        callback.pass(new PsiElement[]{perl6PsiElement});
                    }
                }, psi -> psi.getText());
                return;
            }
        }
        callback.pass(getElements(project, editor, file));
    }

    private PsiElement[] getElements(Project project, Editor editor, PsiFile file) {
        SelectionModel selectionModel = editor.getSelectionModel();
        PsiElement startLeaf = file.findElementAt(selectionModel.getSelectionStart());
        PsiElement endLeaf = file.findElementAt(selectionModel.getSelectionEnd());
        PsiElement start = PsiTreeUtil.getNonStrictParentOfType(skipSpaces(startLeaf, true), Perl6Statement.class);
        PsiElement end = PsiTreeUtil.getNonStrictParentOfType(skipSpaces(endLeaf, false), Perl6Statement.class);
        PsiElement[] emptyArray = PsiElement.EMPTY_ARRAY;

        if (start == null || end == null) {
            if (end != null)
                return new PsiElement[]{end};
            else if (start != null)
                return new PsiElement[]{start};
            else {
                return emptyArray;
            }
        }

        if (PsiTreeUtil.isAncestor(start, end, true)) {
            return new PsiElement[]{start};
        } else if (PsiTreeUtil.isAncestor(end, start, true)) {
            return new PsiElement[]{end};
        }

        List<PsiElement> elements = new ArrayList<>();
        while (start != end) {
            elements.add(start);
            start = start.getNextSibling();
        }
        elements.add(end);
        return elements.toArray(PsiElement.EMPTY_ARRAY);
    }

    private PsiElement skipSpaces(PsiElement type, boolean moveToLeft) {
        if (type == null) return null;
        if (moveToLeft) {
            while (type != null && type.getNode().getElementType() == UNV_WHITE_SPACE) {
                type = type.getNextSibling();
            }
        } else {
            while (type instanceof PsiWhiteSpace) {
                type = type.getPrevSibling();
            }
        }
        return type;
    }

    @NotNull
    private List<Perl6PsiElement> collectExpressions(PsiFile file, Editor editor, int offset) {
        List<Perl6PsiElement> exprs = new ArrayList<>();
        PsiElement element = file.findElementAt(offset);
        while (!(element instanceof Perl6StatementList) && element != null) {
            if (element instanceof P6Extractable)
                exprs.add((Perl6PsiElement)element);
            element = element.getParent();
        }
        return exprs;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        if (dataContext != null) {
            final PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
            final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
            if (file != null && editor != null) {
                invokeOnElements(project, editor, file, elements);
            }
        }
    }

    private void invokeOnElements(Project project, Editor editor, PsiFile file, PsiElement[] elements) {

    }

    @Override
    public boolean isAvailableForQuickList(@NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext) {
        return false;
    }
}
