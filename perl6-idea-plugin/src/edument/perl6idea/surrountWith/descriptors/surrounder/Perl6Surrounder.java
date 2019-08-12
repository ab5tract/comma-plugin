package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Heredoc;
import edument.perl6idea.psi.Perl6Statement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Perl6Surrounder<T extends PsiElement> implements Surrounder {

    protected final boolean myIsStatement;
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public Perl6Surrounder(boolean isStatement) {
        type = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        myIsStatement = isStatement;
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement[] elements) {
        return true;
    }

    protected abstract T createElement(Project project);

    protected abstract void insertStatements(T surrounder, PsiElement[] statements);

    /** Returns an anchor element that will be deleted and
     * caret will be placed at its start position.
     * For example, in `if True {}` element `True` will be
     * a stub to correctly create a node, but
     * should be deleted for user to type in an actual
     * conditional.
     * @param surrounder A created element that surrounds statements
     * @return an anchor element
     * */
    protected abstract PsiElement getAnchor(T surrounder);

    @Nullable
    @Override
    public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] statements) throws IncorrectOperationException {
        // Prepare managers to reformat
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = editor.getDocument();

        // Create a surrounder element, abort without one
        T surrounder = createElement(project);
        if (surrounder == null)
            return null;

        // Insert statements to surround into surrounder
        PsiElement[] elementsToInsert;
        if (statements[statements.length-1] instanceof Perl6Heredoc) {
            List<PsiElement> list = new ArrayList<>(Arrays.asList(statements));
            list.add(Perl6ElementFactory.createNewLine(project));
            elementsToInsert = list.toArray(PsiElement.EMPTY_ARRAY);
        } else {
            elementsToInsert = statements;
        }
        insertStatements(surrounder, elementsToInsert);

        // Get a container and add surrounder into it
        PsiElement container = statements[0].getParent();
        container = codeStyleManager.reformat(container);
        PsiElement statement = container.addBefore(Perl6ElementFactory.createStatementFromText(project, surrounder.getText()), statements[0]);
        surrounder = PsiTreeUtil.findChildOfType(statement, type);
        int offset = surrounder.getTextOffset();

        // Delete children range, then unblock and commit document
        container.deleteChildRange(statements[0], statements[statements.length - 1]);
        documentManager.doPostponedOperationsAndUnblockDocument(document);
        documentManager.commitDocument(document);

        // After calls to documentManager, old elements might not exist, so we
        // finding our surrounder again using a known offset to actually delete its topic
        // and provide a text range to move caret to.
        surrounder = PsiTreeUtil.getParentOfType(container.getContainingFile().findElementAt(offset), type);
        postprocess(surrounder, project);

        // Try to get a topic to delete
        if (surrounder == null)
            return null;
        PsiElement anchor = getAnchor(surrounder);
        if (anchor != null) {
            TextRange range = anchor.getTextRange();
            TextRange textRange = new TextRange(range.getStartOffset(), range.getStartOffset());
            document.deleteString(range.getStartOffset(), range.getEndOffset());
            editor.getCaretModel().moveToOffset(range.getStartOffset());
            return textRange;
        }

        return surrounder.getTextRange();
    }

    protected void postprocess(T surrounder, Project project) {}
}
