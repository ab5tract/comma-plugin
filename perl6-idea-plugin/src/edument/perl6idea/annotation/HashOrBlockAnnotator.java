package edument.perl6idea.annotation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.highlighter.Perl6Highlighter;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6BlockOrHash;
import edument.perl6idea.psi.Perl6Blockoid;
import org.jetbrains.annotations.NotNull;

public class HashOrBlockAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // If it looks nothing like a hash, nothing to do.
        if (!(element instanceof Perl6BlockOrHash))
            return;
        if (!((Perl6BlockOrHash)element).isHashish())
            return;

        // If it looks like a hash, but actually isn't one, then warn; perhaps a thinko.
        if (!((Perl6BlockOrHash)element).isHash()) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "This will be taken as a block, not as a hash as may have been intended")
                    .create();
        }

        // If it really is a hash, apply any hash composer style to the braces.
        else {
            Perl6Blockoid blockoid = ((Perl6BlockOrHash)element).getBlock();
            if (blockoid != null) {
                ASTNode node = blockoid.getNode();
                hashComposer(holder, node.findChildByType(Perl6TokenTypes.BLOCK_CURLY_BRACKET_OPEN));
                hashComposer(holder, node.findChildByType(Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE));
            }
        }
    }

    private static void hashComposer(AnnotationHolder holder, ASTNode brace) {
        if (brace != null)
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(brace)
                    .textAttributes(Perl6Highlighter.HASH_COMPOSER)
                    .create();
    }
}
