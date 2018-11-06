package edument.perl6idea.editor.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.EnterProcessor;
import com.intellij.codeInsight.editorActions.smartEnter.PlainEnterProcessor;
import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.impl.Perl6RoutineDeclImpl;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class Perl6SmartEnterProcessor extends SmartEnterProcessor {
    @Override
    public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        if (!(psiFile instanceof Perl6File)) return false;
        // Get an element
        final CaretModel caretModel = editor.getCaretModel();
        PsiElement element = psiFile.findElementAt(caretModel.getOffset() - 1);
        if (element == null) return false;

        // we will not do anything if the parser state is broken
        if (element.getNode().getElementType() == BAD_CHARACTER) return false;

        // Get closest parent statement
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        if (statement == null) return false;

        processEnter(statement, editor, project);
        EnterProcessor plain = new PlainEnterProcessor();
        plain.doEnter(editor, psiFile, true);
        CodeStyleManager.getInstance(project).reformat(statement);
        return true;
    }

    private static void processEnter(Perl6Statement element, Editor editor, Project project) {
        // Get a first node of Perl6Statement
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
        return child instanceof Perl6PackageDecl ||
               child instanceof Perl6RoutineDeclImpl ||
               child instanceof Perl6IfStatement ||
               child instanceof Perl6UnlessStatement ||
               child instanceof Perl6ForStatement ||
               child instanceof Perl6GivenStatement ||
               child instanceof Perl6WheneverStatement||
               child instanceof Perl6LoopStatement||
               child instanceof Perl6ScopedDecl && !((Perl6ScopedDecl)child).getScope().equals("unit");
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
            lastPiece instanceof Perl6Trait ||
            lastPiece instanceof Perl6RoleSignature ||
            lastPiece instanceof Perl6Signature ||
            lastPiece instanceof Perl6LongName ||
            lastPiece.getNode().getElementType() == NAME) {
            if (offsetToJump < 0)
                offsetToJump = lastPiece.getTextOffset() + lastPiece.getTextLength();
            editor.getDocument().insertString(offsetToJump, " {\n}\n");
        } else if (lastPiece instanceof Perl6Blockoid) {
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

    private static boolean controlStatementsCheck(PsiElement piece) {
        PsiElement pieceParent = piece.getParent();
        return pieceParent instanceof Perl6IfStatement ||
               pieceParent instanceof Perl6UnlessStatement ||
               pieceParent instanceof Perl6GivenStatement ||
               pieceParent instanceof Perl6WheneverStatement ||
               pieceParent instanceof Perl6LoopStatement ||
               pieceParent instanceof Perl6ForStatement;
    }

    private static void processBlockInternals(PsiElement piece, Editor editor) {
        int length = piece.getTextLength();
        if (length <= 2 || isBlockEmpty(piece, length)) {
            int offset = piece.getTextOffset();
            piece.delete();
            editor.getDocument().insertString(offset, "{\n}\n");
        }
    }

    private static boolean isBlockEmpty(PsiElement piece, int length) {
        return piece.getText().substring(1, length - 1).replaceAll("\n", "").trim().isEmpty();
    }
}
