package org.raku.comma.annotation;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

public class MissingThingsAnnotator implements Annotator {
    private static final TokenSet T_PAREN_OPEN = TokenSet.create(RakuTokenTypes.PARENTHESES_OPEN);
    private static final TokenSet T_PAREN_CLOSE = TokenSet.create(RakuTokenTypes.PARENTHESES_CLOSE);
    private static final TokenSet T_ARRAY_COMP_OPEN = TokenSet.create(RakuTokenTypes.ARRAY_COMPOSER_OPEN);
    private static final TokenSet T_ARRAY_COMP_CLOSE = TokenSet.create(RakuTokenTypes.ARRAY_COMPOSER_CLOSE);
    private static final TokenSet T_ARRAY_INDEX_OPEN = TokenSet.create(RakuTokenTypes.ARRAY_INDEX_BRACKET_OPEN);
    private static final TokenSet T_ARRAY_INDEX_CLOSE = TokenSet.create(RakuTokenTypes.ARRAY_INDEX_BRACKET_CLOSE);
    private static final TokenSet T_BLOCK_OPEN = TokenSet.create(RakuTokenTypes.BLOCK_CURLY_BRACKET_OPEN);
    private static final TokenSet T_BLOCK_CLOSE = TokenSet.create(RakuTokenTypes.BLOCK_CURLY_BRACKET_CLOSE);
    private static final TokenSet T_RX_GROUP_OPEN = TokenSet.create(RakuTokenTypes.REGEX_GROUP_BRACKET_OPEN);
    private static final TokenSet T_RX_GROUP_CLOSE = TokenSet.create(RakuTokenTypes.REGEX_GROUP_BRACKET_CLOSE);
    private static final TokenSet T_RX_ASS_OPEN = TokenSet.create(RakuTokenTypes.REGEX_ASSERTION_ANGLE_OPEN);
    private static final TokenSet T_RX_ASS_CLOSE = TokenSet.create(RakuTokenTypes.REGEX_ASSERTION_ANGLE_CLOSE);
    private static final TokenSet T_RX_CAP_OPEN = TokenSet.create(RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN);
    private static final TokenSet T_RX_CAP_CLOSE = TokenSet.create(RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE);

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof ASTDelegatePsiElement)) return;

        if (element instanceof RakuSubCall ||
            element instanceof RakuMethodCall ||
            element instanceof RakuParenthesizedExpr ||
            element instanceof RakuLoopStatement ||
            element instanceof RakuVariableDecl ||
            element instanceof RakuSignature ||
            element instanceof RakuCall) {
            ASTNode[] opener = element.getNode().getChildren(T_PAREN_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_PAREN_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing )").range(opener[0]).create();
        }
        else if (element instanceof RakuArrayComposer) {
            ASTNode[] opener = element.getNode().getChildren(T_ARRAY_COMP_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_ARRAY_COMP_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing ]").range(opener[0]).create();
        }
        else if (element instanceof RakuArrayIndex) {
            ASTNode[] opener = element.getNode().getChildren(T_ARRAY_INDEX_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_ARRAY_INDEX_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing ]").range(opener[0]).create();
        }
        else if (element instanceof RakuBlockoid) {
            ASTNode[] opener = element.getNode().getChildren(T_BLOCK_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_BLOCK_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing }").range(opener[0]).create();
        }
        else if (element instanceof RakuRegexGroup) {
            ASTNode[] opener = element.getNode().getChildren(T_RX_GROUP_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_RX_GROUP_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing ]").range(opener[0]).create();
        }
        else if (element instanceof RakuRegexAssertion) {
            ASTNode[] opener = element.getNode().getChildren(T_RX_ASS_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_RX_ASS_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing >").range(opener[0]).create();
        }
        else if (element instanceof RakuRegexCapturePositional) {
            ASTNode[] opener = element.getNode().getChildren(T_RX_CAP_OPEN);
            ASTNode[] closer = element.getNode().getChildren(T_RX_CAP_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing )").range(opener[0]).create();
        }
    }
}
