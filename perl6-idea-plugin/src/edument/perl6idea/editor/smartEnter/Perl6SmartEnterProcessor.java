package edument.perl6idea.editor.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.EnterProcessor;
import com.intellij.codeInsight.editorActions.smartEnter.PlainEnterProcessor;
import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class Perl6SmartEnterProcessor extends SmartEnterProcessor {
    @Override
    public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        if (!(psiFile instanceof Perl6File)) return false;
        final CaretModel caretModel = editor.getCaretModel();
        PsiElement element = psiFile.findElementAt(caretModel.getOffset() - 1);
        if (element == null) return false;
        if (element.getNode().getElementType() == BAD_CHARACTER) return false;
        element = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        if (element == null) return false;
        boolean result = processEnter(element, editor);
        if (result) {
            EnterProcessor plain = new PlainEnterProcessor();
            plain.doEnter(editor, psiFile, true);
        }
        return result;
    }

    private static boolean processEnter(PsiElement element, Editor editor) {
        PsiElement child = element.getFirstChild();
        if (child == null) return false;
        if (child instanceof Perl6PackageDecl ||
            (child instanceof Perl6ScopedDecl && !((Perl6ScopedDecl)child).getScope().equals("unit"))) {
            return processPackageDeclaration(child, editor);
        } else if (element.getLastChild().getNode().getElementType() == STATEMENT_TERMINATOR) {
            return false;
        } else {
            // Default handler for statements
            int offsetToJump;
            if (child.getLastChild().getNode().getElementType() == UNV_WHITE_SPACE)
                offsetToJump = child.getLastChild().getTextOffset();
            else
                offsetToJump = child.getTextOffset() + child.getTextLength();

            editor.getDocument().insertString(offsetToJump, ";");
            return true;
        }
    }

    private static boolean processPackageDeclaration(PsiElement element, Editor editor) {
        PsiElement lastPiece = element.getLastChild();
        // Depend on last piece, we know what we want complete
        if (lastPiece instanceof Perl6PackageDecl) {
            return processPackageDeclaration(lastPiece, editor);
        }
        if (lastPiece.getNode().getElementType() == UNV_WHITE_SPACE) {
            if (PsiTreeUtil.getChildrenOfType(element, Perl6Blockoid.class) == null) {
                // Delete whitespace
                int offsetToJump = lastPiece.getTextOffset();
                lastPiece.delete();
                editor.getDocument().insertString(offsetToJump, " {\n}\n");
            }
        } else if (lastPiece instanceof Perl6Trait ||
                   lastPiece instanceof Perl6RoleSignature ||
                   lastPiece.getNode().getElementType() == NAME) {
            int offsetToJump = lastPiece.getTextOffset() + lastPiece.getTextLength();
            editor.getDocument().insertString(offsetToJump, " {\n}\n");
        } else if (lastPiece instanceof Perl6Blockoid) {
            int offsetToJump = lastPiece.getTextOffset();
            lastPiece.delete();
            editor.getDocument().insertString(offsetToJump, "{\n}\n");
        } else {
            PsiElement maybeTerminator = element.getNextSibling();
            if (maybeTerminator == null || maybeTerminator.getNode().getElementType() != STATEMENT_TERMINATOR) {
                int offsetToJump = lastPiece.getTextOffset() + lastPiece.getTextLength();
                editor.getDocument().insertString(offsetToJump, ";");
                return true;
            }
            return false;
        }
        return true;
    }
}
