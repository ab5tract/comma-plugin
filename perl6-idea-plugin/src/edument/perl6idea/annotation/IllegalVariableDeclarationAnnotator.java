package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.REGEX_CAPTURE_NAME;

public class IllegalVariableDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6ScopedDecl))
            return;

        Perl6VariableDecl varDecl = PsiTreeUtil.getChildOfType(element, Perl6VariableDecl.class);
        if (varDecl == null) return;
        Perl6Variable var = PsiTreeUtil.getChildOfType(varDecl, Perl6Variable.class);
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
            var.getNextSibling() instanceof Perl6Signature)
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot declare a contextualizer")
                    .range(element).create();
    }
}
