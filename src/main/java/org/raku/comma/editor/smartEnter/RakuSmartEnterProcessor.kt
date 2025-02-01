package org.raku.comma.editor.smartEnter

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.*
import org.raku.comma.psi.impl.RakuRoutineDeclImpl
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import kotlin.math.min

class RakuSmartEnterProcessor : SmartEnterProcessor() {
    override fun process(project: Project, editor: Editor, psiFile: PsiFile): Boolean {
        if (psiFile !is RakuFile) return false

        // Get an element
        val caretModel = editor.caretModel
        val element = psiFile.findElementAt(caretModel.offset - 1) ?: return false

        // we will not do anything if the parser state is broken
        if (element.node.elementType === RakuTokenTypes.BAD_CHARACTER) return false

        // Get closest parent statement
        var statement = PsiTreeUtil.getParentOfType<RakuStatement?>(element, RakuStatement::class.java)
            ?: return false

        statement = PsiTreeUtil.getParentOfType<RakuStatement>(element, RakuStatement::class.java)
            ?: return false

        processEnter(statement, editor, project, psiFile);
        val plain = PlainEnterProcessor()
        plain.doEnter(editor, psiFile)

        val oldLine = editor.document.getLineNumber(editor.caretModel.offset)
        // TODO: See if we can re-enable this to some degree?
//        CodeStyleManager.getInstance(project).reformat(psiFile)
        editor.caretModel.moveToOffset(editor.document.getLineStartOffset(
                        min(
                            (editor.document.lineCount - 1).toDouble(),
                            oldLine.toDouble()
                        ).toInt()))

        CommandProcessor.getInstance().executeCommand(project, Runnable {
            val actionManager = EditorActionManager.getInstance()
            val actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_MOVE_LINE_END)
            try {
                actionHandler.execute(
                    editor,
                    caretModel.currentCaret,
                    DataManager.getInstance().dataContextFromFocusAsync.blockingGet(2000)
                )
            } catch (e: ExecutionException) {
                log.warn(e)
            } catch (e: TimeoutException) {
                log.warn(e)
            }
        }, "", null)
        return true
    }

    private val log = Logger.getInstance(RakuSmartEnterProcessor::class.java)

    private fun processEnter(element: RakuStatement, editor: Editor, project: Project, psiFile: RakuFile) {
        // Get a first node of RakuStatement
        val actualStatement = getStatementAtCaret(editor, psiFile) ?: return

        var lastChildOfStatement: PsiElement? = element.lastChild
        var prevSiblingStatement: PsiElement = lastChildOfStatement ?: return
        // In current version of the parser (2018.10), trailing PsiWhiteSpace and UNV_WHITE_SPACE
        // nodes are included as children of last statement, so we want to skip those
        while (lastChildOfStatement != null
            && (lastChildOfStatement is PsiWhiteSpace
                    || lastChildOfStatement.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE)
        ) {
            prevSiblingStatement = lastChildOfStatement
            lastChildOfStatement = lastChildOfStatement.prevSibling
        }

        val siblingStatement = element.nextSibling ?: return

        if (prevSiblingStatement.node.elementType === RakuTokenTypes.STATEMENT_TERMINATOR
        || siblingStatement.node.elementType === RakuTokenTypes.STATEMENT_TERMINATOR) {
            // `;` is already added either as part of statement to complete or as a next node, just do Enter
            return
        }

        // If we have a: package || variable || control statement
        // complete it with possible block
        if (isBlockCompletable(actualStatement)) {
            processStatement(actualStatement, editor, project)
        } else {
            // Default handler for statements
            val offsetToJump: Int
            // A temporary hack to get a bit nicer behavior (excessive whitespace cutting)
            // but it is a job for formatter to make `foo;` from `foo ;`
            val maybeWhiteSpace = actualStatement.lastChild
            offsetToJump =
                if (maybeWhiteSpace != null
                && maybeWhiteSpace.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE)
                        maybeWhiteSpace.textOffset else element.textOffset + element.textLength

            editor.document.insertString(offsetToJump, ";")
        }
    }

    private fun isBlockCompletable(child: PsiElement?): Boolean {
        return child is RakuPackageDecl
            || child is RakuRoutineDeclImpl
            || child is RakuIfStatement
            || child is RakuUnlessStatement
            || child is RakuForStatement
            || child is RakuGivenStatement
            || child is RakuWhenStatement
            || child is RakuWheneverStatement
            || child is RakuLoopStatement
            || child is RakuScopedDecl
            && child.getScope() != "unit"
    }

    private fun processStatement(element: PsiElement, editor: Editor, project: Project) {
        var lastPiece: PsiElement? = element.lastChild

        // In current version of the parser (2018.10), trailing PsiWhiteSpace and UNV_WHITE_SPACE
        // nodes are included as children of last statement, so we want to skip those,
        // but preserve if some whitespace is actually added at the end,
        // so we could handle it with nice offset setting
        var saved: PsiElement = lastPiece ?: return
        while (
            lastPiece != null
            && (lastPiece is PsiWhiteSpace || lastPiece.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE)
        ) {
            saved = lastPiece
            lastPiece = lastPiece.prevSibling ?: return
        }

        if (saved.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE) {
            lastPiece = saved
        }

        // Depend on last piece, we know what we want complete

        // If it is a scoped declaration, LastChild == FirstChild and it is declaration, so recurse
        if (isBlockCompletable(lastPiece)) {
            processStatement(lastPiece, editor, project)
            return
        }

        // Remove trailing whitespace
        var offsetToJump = -1
        if (lastPiece.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE) {
            // Delete whitespace
            offsetToJump = lastPiece.textOffset
            val tempLastPiece = lastPiece.prevSibling
            lastPiece.delete()
            lastPiece = tempLastPiece
        }

        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        val document = editor.document
        psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
        psiDocumentManager.commitDocument(document)

        // If element precedes code block
        if (controlStatementsCheck(lastPiece!!)
        || lastPiece is RakuTrait
        || lastPiece is RakuRoleSignature
        || lastPiece is RakuSignature
        || lastPiece is RakuLongName
        || lastPiece.node.elementType === RakuTokenTypes.NAME)
        {
            if (offsetToJump < 0) {
                offsetToJump = lastPiece.textOffset + lastPiece.textLength
            }
            val whiteSpace: PsiWhiteSpace? = getInnermostRightmostChild(lastPiece)
            if (whiteSpace != null) {
                offsetToJump -= whiteSpace.textLength
            }
            editor.document.insertString(offsetToJump, " {\n}")
        } else if (lastPiece is RakuBlockoid) {
            // If code block itself
            processBlockInternals(lastPiece, editor)
        } else {
            // Otherwise, just try to add `;` without duplication
            val maybeTerminator = element.nextSibling ?: return
            if (maybeTerminator.node.elementType !== RakuTokenTypes.STATEMENT_TERMINATOR) {
                if (offsetToJump < 0) {
                    offsetToJump = lastPiece.textOffset + lastPiece.textLength
                }
                editor.document.insertString(offsetToJump, ";")
            }
        }
    }

    private fun getInnermostRightmostChild(piece: PsiElement?): PsiWhiteSpace? {
        var lastChild = piece
        while (lastChild != null) {
            lastChild = lastChild.lastChild
            if (lastChild is PsiWhiteSpace) return lastChild
        }
        return null
    }

    private fun controlStatementsCheck(piece: PsiElement): Boolean {
        val pieceParent = piece.parent
        return pieceParent is RakuIfStatement
            || pieceParent is RakuUnlessStatement
            || pieceParent is RakuGivenStatement
            || pieceParent is RakuWhenStatement
            || pieceParent is RakuLoopStatement
            || pieceParent is RakuForStatement
    }

    private fun processBlockInternals(piece: PsiElement, editor: Editor) {
        val length = piece.textLength
        if (length <= 2 || isBlockEmpty(piece, length)) {
            val offset = piece.textOffset
            piece.delete()
            WriteCommandAction.runWriteCommandAction(
                editor.project,
                Runnable { editor.document.insertString(offset, "{\n}") }
            )
        }
    }

    private fun isBlockEmpty(piece: PsiElement, length: Int): Boolean {
        return piece.text.substring(1, length - 1).replace("\n".toRegex(), "").trim { it <= ' ' }.isEmpty()
    }
}
