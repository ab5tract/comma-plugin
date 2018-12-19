package edument.perl6idea.annotation;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class MissingThingsAnnotator implements Annotator {
    private static final TokenSet T_PAREN_OPEN = TokenSet.create(Perl6TokenTypes.PARENTHESES_OPEN);
    private static final TokenSet T_PAREN_CLOSE = TokenSet.create(Perl6TokenTypes.PARENTHESES_CLOSE);
    private static final TokenSet T_ARRAY_COMP_OPEN = TokenSet.create(Perl6TokenTypes.ARRAY_COMPOSER_OPEN);
    private static final TokenSet T_ARRAY_COMP_CLOSE = TokenSet.create(Perl6TokenTypes.ARRAY_COMPOSER_CLOSE);
    private static final TokenSet T_ARRAY_INDEX_OPEN = TokenSet.create(Perl6TokenTypes.ARRAY_INDEX_BRACKET_OPEN);
    private static final TokenSet T_ARRAY_INDEX_CLOSE = TokenSet.create(Perl6TokenTypes.ARRAY_INDEX_BRACKET_CLOSE);

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6SubCall ||
                element instanceof Perl6MethodCall ||
                element instanceof Perl6ParenthesizedExpr ||
                element instanceof Perl6LoopStatement ||
                element instanceof Perl6VariableDecl ||
                element instanceof Perl6Signature ||
                element instanceof Perl6Call) {
            ASTDelegatePsiElement check = (ASTDelegatePsiElement)element;
            ASTNode[] opener = check.getNode().getChildren(T_PAREN_OPEN);
            ASTNode[] closer = check.getNode().getChildren(T_PAREN_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.createErrorAnnotation(opener[0], "Missing closing )");
        }
        else if (element instanceof Perl6ArrayComposer) {
            ASTDelegatePsiElement check = (ASTDelegatePsiElement)element;
            ASTNode[] opener = check.getNode().getChildren(T_ARRAY_COMP_OPEN);
            ASTNode[] closer = check.getNode().getChildren(T_ARRAY_COMP_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.createErrorAnnotation(opener[0], "Missing closing ]");
        }
        else if (element instanceof  Perl6ArrayIndex) {
            ASTDelegatePsiElement check = (ASTDelegatePsiElement)element;
            ASTNode[] opener = check.getNode().getChildren(T_ARRAY_INDEX_OPEN);
            ASTNode[] closer = check.getNode().getChildren(T_ARRAY_INDEX_CLOSE);
            if (opener.length > 0 && closer.length == 0)
                holder.createErrorAnnotation(opener[0], "Missing closing ]");
        }
    }
}
