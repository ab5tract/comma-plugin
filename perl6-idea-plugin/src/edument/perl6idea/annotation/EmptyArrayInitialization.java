package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.RemoveInitializerFix;
import edument.perl6idea.psi.Perl6ArrayComposer;
import edument.perl6idea.psi.Perl6ParenthesizedExpr;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

public class EmptyArrayInitialization implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6VariableDecl))
            return;

        Perl6VariableDecl decl = (Perl6VariableDecl)element;
        String name = decl.getVariableName();
        if (Perl6Variable.getSigil(name) != '@')
            return;

        boolean shouldAnnotate = false;
        PsiElement initializer = decl.getInitializer();

        if (initializer instanceof Perl6ArrayComposer) {
            shouldAnnotate = ((Perl6ArrayComposer)initializer).getElements().length == 0;
        } else if (initializer instanceof Perl6ParenthesizedExpr) {
            shouldAnnotate = ((Perl6ParenthesizedExpr)initializer).getElements().length == 0;
        }

        if (shouldAnnotate)
            holder
                .createWeakWarningAnnotation(initializer, "Initialization of empty array is redundant")
                .registerFix(new RemoveInitializerFix(decl));
    }
}
