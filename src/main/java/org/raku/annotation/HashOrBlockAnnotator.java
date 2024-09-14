package org.raku.annotation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.highlighter.RakuHighlighter;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuBlockOrHash;
import org.raku.psi.RakuBlockoid;
import org.jetbrains.annotations.NotNull;

public class HashOrBlockAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // If it looks nothing like a hash, nothing to do.
        if (!(element instanceof RakuBlockOrHash))
            return;
        if (!((RakuBlockOrHash)element).isHashish())
            return;

        // If it looks like a hash, but actually isn't one, then warn; perhaps a thinko.
        if (!((RakuBlockOrHash)element).isHash()) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "This will be taken as a block, not as a hash as may have been intended")
                    .create();
        }

        // If it really is a hash, apply any hash composer style to the braces.
        else {
            RakuBlockoid blockoid = ((RakuBlockOrHash)element).getBlock();
            if (blockoid != null) {
                ASTNode node = blockoid.getNode();
                hashComposer(holder, node.findChildByType(RakuTokenTypes.BLOCK_CURLY_BRACKET_OPEN));
                hashComposer(holder, node.findChildByType(org.raku.parsing.RakuTokenTypes.BLOCK_CURLY_BRACKET_CLOSE));
            }
        }
    }

    private static void hashComposer(AnnotationHolder holder, ASTNode brace) {
        if (brace != null)
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(brace)
                    .textAttributes(RakuHighlighter.HASH_COMPOSER)
                    .create();
    }
}
