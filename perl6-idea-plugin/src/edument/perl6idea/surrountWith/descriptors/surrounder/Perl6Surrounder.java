package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6Block;
import edument.perl6idea.psi.Perl6Do;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Heredoc;
import edument.perl6idea.utils.Perl6PsiUtil;
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

    protected boolean isExpression() {
        return false;
    }

    protected boolean isControl() {
        return true;
    }

    @Nullable
    @Override
    public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] statements) throws IncorrectOperationException {
        if (!myIsStatement && statements.length > 1) {
            throw new IncorrectOperationException("Cannot surround this expression");
        }

        // Prepare managers to reformat
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = editor.getDocument();

        // Create a surrounder element, abort without one
        T surrounder = createElement(project);
        if (surrounder == null)
            return null;

        // Insert statements to surround into surrounder
        PsiElement[] elementsToInsert = prepareSurroundedStatements(project, statements);
        insertStatements(surrounder, elementsToInsert);

        if (myIsStatement) {
            // Obtain list of statements and add the new one
            PsiElement container = statements[0].getParent();
            container = codeStyleManager.reformat(container);
            String surrounderText = surrounder.getText();
            // If it needs a semicolon to be valid expression,
            // e.g. `if` block's closing bracket is valid, but not a hash one
            if (isExpression())
                surrounderText += ";";
            PsiElement statement = container.addBefore(Perl6ElementFactory.createStatementFromText(project, surrounderText), statements[0]);
            // Regain surrounder node out of statement so we could process it later
            surrounder = PsiTreeUtil.findChildOfType(statement, type);
            // Delete elements we surrounded
            container.deleteChildRange(statements[0], statements[statements.length - 1]);
        }
        else {
            PsiElement toReplace = statements[0];
            toReplace = codeStyleManager.reformat(toReplace);
            // Create a do wrapper
            PsiElement replacement = null;
            if (isControl()) {
                Perl6Do doWrapper = Perl6ElementFactory.createDoStatement(project);
                Perl6Block block = PsiTreeUtil.getParentOfType(doWrapper.getBlock(), Perl6Block.class);
                // Replace block of `do` with our single expression
                block.replace(surrounder);
                replacement = doWrapper;
            } else {
                replacement = surrounder;
            }
            // Replace element with wrapper
            toReplace = toReplace.replace(replacement);
            // Regain surrounder node we need to process
            surrounder = PsiTreeUtil.findChildOfType(toReplace, type, false);
        }

        documentManager.doPostponedOperationsAndUnblockDocument(document);
        documentManager.commitDocument(document);

        // Try to get a topic to delete
        PsiElement anchor = getAnchor(surrounder);
        if (anchor == null)
            return surrounder.getTextRange();

        TextRange range = anchor.getTextRange();
        TextRange textRange = new TextRange(range.getStartOffset(), range.getStartOffset());
        document.deleteString(range.getStartOffset(), range.getEndOffset());
        editor.getCaretModel().moveToOffset(range.getStartOffset());
        return textRange;
    }

    @NotNull
    private static PsiElement[] prepareSurroundedStatements(@NotNull Project project,
                                                            @NotNull PsiElement[] statements) {
        // Here we adjust statements that we will be surrounding so that they form
        // a correct resulting output
        PsiElement[] elementsToInsert;
        // If last statement is Heredoc, we need to add a newline after it
        // because otherwise closing element of surrounder will be added
        // and Heredoc terminator will be broken
        if (statements[statements.length-1] instanceof Perl6Heredoc) {
            List<PsiElement> list = new ArrayList<>(Arrays.asList(statements));
            list.add(Perl6ElementFactory.createNewLine(project));
            elementsToInsert = list.toArray(PsiElement.EMPTY_ARRAY);
        } else {
            elementsToInsert = statements;
        }
        return elementsToInsert;
    }
}
