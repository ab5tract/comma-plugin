package org.raku.editor.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;
import org.raku.psi.impl.RakuRoutineDeclImpl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.raku.parsing.RakuTokenTypes.*;

@InternalIgnoreDependencyViolation
public class RakuSmartEnterProcessor extends SmartEnterProcessor {
    private static final Logger LOG = Logger.getInstance(RakuSmartEnterProcessor.class);

    @Override
    public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        if (!(psiFile instanceof RakuFile)) return false;
        // Get an element
        final CaretModel caretModel = editor.getCaretModel();
        PsiElement element = psiFile.findElementAt(caretModel.getOffset() - 1);
        if (element == null) return false;

        // we will not do anything if the parser state is broken
        if (element.getNode().getElementType() == BAD_CHARACTER) return false;

        // Get closest parent statement
        RakuStatement statement = PsiTreeUtil.getParentOfType(element, RakuStatement.class);
        if (statement == null) return false;

        processEnter(statement, editor, project);
        PlainEnterProcessor plain = new PlainEnterProcessor();
        plain.doEnter(editor, psiFile);

        int oldLine = editor.getDocument().getLineNumber(editor.getCaretModel().getOffset());
        CodeStyleManager.getInstance(project).reformat(psiFile);
        editor.getCaretModel().moveToOffset(editor.getDocument().getLineStartOffset(Math.min(editor.getDocument().getLineCount() - 1, oldLine)));

        CommandProcessor.getInstance().executeCommand(project, () -> {
            EditorActionManager actionManager = EditorActionManager.getInstance();
            EditorActionHandler actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_MOVE_LINE_END);
            try {
                actionHandler.execute(editor, caretModel.getCurrentCaret(), DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(2000));
            }
            catch (ExecutionException | TimeoutException e) {
                LOG.warn(e);
            }
        }, "", null);
        return true;
    }

    private static void processEnter(RakuStatement element, Editor editor, Project project) {
        // Get a first node of RakuStatement
        PsiElement actualStatement = element.getFirstChild();
        if (actualStatement == null) return;

        PsiElement lastChildOfStatement = element.getLastChild();
        // In current version of the parser (2018.10), trailing PsiWhiteSpace and UNV_WHITE_SPACE
        // nodes are included as children of last statement, so we want to skip those
        while (lastChildOfStatement != null && (lastChildOfStatement instanceof PsiWhiteSpace ||
                                                lastChildOfStatement.getNode().getElementType() == UNV_WHITE_SPACE))
            lastChildOfStatement = lastChildOfStatement.getPrevSibling();
        PsiElement siblingStatement = element.getNextSibling();

        if (lastChildOfStatement != null && lastChildOfStatement.getNode().getElementType() == STATEMENT_TERMINATOR ||
            siblingStatement != null && siblingStatement.getNode().getElementType() == STATEMENT_TERMINATOR) {
            // `;` is already added either as part of statement to complete or as a next node, just do Enter
            return;
        }

        // If we have a: package || variable || control statement
        // complete it with possible block
        if (isBlockCompletable(actualStatement)) {
            processStatement(actualStatement, editor, project);
        } else {
            // Default handler for statements
            int offsetToJump;
            // A temporary hack to get a bit nicer behavior (excessive whitespace cutting)
            // but it is a job for formatter to make `foo;` from `foo ;`
            PsiElement maybeWhiteSpace = actualStatement.getLastChild();
            if (maybeWhiteSpace != null && maybeWhiteSpace.getNode().getElementType() == UNV_WHITE_SPACE)
                offsetToJump = maybeWhiteSpace.getTextOffset();
            else
                offsetToJump = element.getTextOffset() + element.getTextLength();

            editor.getDocument().insertString(offsetToJump, ";");
        }
    }

    private static boolean isBlockCompletable(PsiElement child) {
        return child instanceof RakuPackageDecl ||
               child instanceof RakuRoutineDeclImpl ||
               child instanceof RakuIfStatement ||
               child instanceof RakuUnlessStatement ||
               child instanceof RakuForStatement ||
               child instanceof RakuGivenStatement ||
               child instanceof RakuWhenStatement ||
               child instanceof RakuWheneverStatement ||
               child instanceof RakuLoopStatement ||
               child instanceof RakuScopedDecl && !((RakuScopedDecl)child).getScope().equals("unit");
    }

    private static void processStatement(PsiElement element, Editor editor, Project project) {
        PsiElement lastPiece = element.getLastChild();

        // In current version of the parser (2018.10), trailing PsiWhiteSpace and UNV_WHITE_SPACE
        // nodes are included as children of last statement, so we want to skip those,
        // but preserve if some whitespace is actually added at the end,
        // so we could handle it with nice offset setting
        PsiElement saved = null;
        while (lastPiece != null && (lastPiece instanceof PsiWhiteSpace ||
                                     lastPiece.getNode().getElementType() == UNV_WHITE_SPACE)) {
            saved = lastPiece;
            lastPiece = lastPiece.getPrevSibling();
        }

        if (lastPiece == null) return;
        if (saved != null && saved.getNode().getElementType() == UNV_WHITE_SPACE) {
            lastPiece = saved;
        }
        // Depend on last piece, we know what we want complete

        // If it is a scoped declaration, LastChild == FirstChild and it is declaration, so recurse
        if (isBlockCompletable(lastPiece)) {
            processStatement(lastPiece, editor, project);
            return;
        }

        // Remove trailing whitespace
        int offsetToJump = -1;
        if (lastPiece.getNode().getElementType() == UNV_WHITE_SPACE) {
            // Delete whitespace
            offsetToJump = lastPiece.getTextOffset();
            PsiElement tempLastPiece = lastPiece.getPrevSibling();
            lastPiece.delete();
            lastPiece = tempLastPiece;
        }

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = editor.getDocument();
        psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
        psiDocumentManager.commitDocument(document);

        // If element precedes code block
        if (controlStatementsCheck(lastPiece) ||
            lastPiece instanceof RakuTrait ||
            lastPiece instanceof RakuRoleSignature ||
            lastPiece instanceof RakuSignature ||
            lastPiece instanceof RakuLongName ||
            lastPiece.getNode().getElementType() == NAME) {
            if (offsetToJump < 0)
                offsetToJump = lastPiece.getTextOffset() + lastPiece.getTextLength();
            PsiWhiteSpace whiteSpace = getInnermostRightmostChild(lastPiece);
            if (whiteSpace != null)
                offsetToJump -= whiteSpace.getTextLength();
            editor.getDocument().insertString(offsetToJump, " {\n}");
        } else if (lastPiece instanceof RakuBlockoid) {
            // If code block itself
            processBlockInternals(lastPiece, editor);
        } else {
            // Otherwise, just try to add `;` without duplication
            PsiElement maybeTerminator = element.getNextSibling();
            if (maybeTerminator == null || maybeTerminator.getNode().getElementType() != STATEMENT_TERMINATOR) {
                if (offsetToJump < 0)
                    offsetToJump = lastPiece.getTextOffset() + lastPiece.getTextLength();
                editor.getDocument().insertString(offsetToJump, ";");
            }
        }
    }

    private static PsiWhiteSpace getInnermostRightmostChild(PsiElement piece) {
        PsiElement lastChild = piece;
        while (lastChild != null) {
            lastChild = lastChild.getLastChild();
            if (lastChild instanceof PsiWhiteSpace)
                return (PsiWhiteSpace)lastChild;
        }
        return null;
    }

    private static boolean controlStatementsCheck(PsiElement piece) {
        PsiElement pieceParent = piece.getParent();
        return pieceParent instanceof RakuIfStatement ||
               pieceParent instanceof RakuUnlessStatement ||
               pieceParent instanceof RakuGivenStatement ||
               pieceParent instanceof RakuWhenStatement ||
               pieceParent instanceof RakuLoopStatement ||
               pieceParent instanceof RakuForStatement;
    }

    private static void processBlockInternals(PsiElement piece, Editor editor) {
        int length = piece.getTextLength();
        if (length <= 2 || isBlockEmpty(piece, length)) {
            int offset = piece.getTextOffset();
            piece.delete();
            editor.getDocument().insertString(offset, "{\n}");
        }
    }

    private static boolean isBlockEmpty(PsiElement piece, int length) {
        return piece.getText().substring(1, length - 1).replaceAll("\n", "").trim().isEmpty();
    }
}
