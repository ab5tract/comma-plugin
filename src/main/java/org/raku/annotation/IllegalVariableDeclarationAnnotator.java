package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuScopedDecl;
import org.raku.psi.RakuSignature;
import org.raku.psi.RakuVariable;
import org.raku.psi.RakuVariableDecl;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuTokenTypes.REGEX_CAPTURE_NAME;

public class IllegalVariableDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuScopedDecl))
            return;

        RakuVariableDecl varDecl = PsiTreeUtil.getChildOfType(element, RakuVariableDecl.class);
        if (varDecl == null) return;
        RakuVariable var = PsiTreeUtil.getChildOfType(varDecl, RakuVariable.class);
        if (var == null) return;
        if (var.getFirstChild() != null && var.getFirstChild().getNode().getElementType() == REGEX_CAPTURE_NAME)
            if (var.getFirstChild().getText().equals("$<") && var.getLastChild().getText().equals(">") || // $<foo>
                var.getFirstChild().getText().equals("@<") && var.getLastChild().getText().equals(">") || // @<foo>
                var.getFirstChild().getText().equals("%<") && var.getLastChild().getText().equals(">"))   // %<foo>
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot declare a regex named match variable")
                    .range(element).create();

            else // $ + integer
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot declare a regex positional match variable")
                    .range(element).create();

        // Check out contextualizer
        if ((var.getText().equals("$") ||
             var.getText().equals("@") ||
             var.getText().equals("%")) &&
            var.getNextSibling() instanceof RakuSignature)
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot declare a contextualizer")
                    .range(element).create();
    }
}
