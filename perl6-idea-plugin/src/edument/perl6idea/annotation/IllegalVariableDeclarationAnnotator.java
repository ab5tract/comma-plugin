package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.REGEX_CAPTURE_NAME;

public class IllegalVariableDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6ScopedDecl))
            return;

        Perl6Variable var = PsiTreeUtil.findChildOfType(element, Perl6Variable.class);
        if (var == null) return;
        if (var.getFirstChild() != null && var.getFirstChild().getNode().getElementType() == REGEX_CAPTURE_NAME)
            if (var.getFirstChild().getText().equals("$<") && var.getLastChild().getText().equals(">")) // $<foo>
                holder.createErrorAnnotation(element, "Cannot declare a regex named match variable");
            else // $ + integer
                holder.createErrorAnnotation(element, "Cannot declare a regex positional match variable");

        // Check out contextualizer
        if (var.getText().equals("$") && var.getNextSibling() != null && var.getNextSibling() instanceof Perl6Signature)
            holder.createErrorAnnotation(element, "Cannot declare a contextualizer");
    }
}
