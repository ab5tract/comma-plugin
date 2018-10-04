package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.ChangeMyScopeToOurScopeFix;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

public class MyScopedVariableExportedAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6ScopedDecl)) return;

        Perl6ScopedDecl decl = (Perl6ScopedDecl)element;
        if (!decl.getScope().equals("my")) return;
        if (!(decl.getLastChild() instanceof Perl6VariableDecl)) return;

        Perl6VariableDecl variableDecl = (Perl6VariableDecl)decl.getLastChild();
        if (!variableDecl.isExported()) return;

        // If variable is exported and the scope is `my`, annotate
        holder.createErrorAnnotation(element, "`my` scoped variable cannot be exported")
              .registerFix(new ChangeMyScopeToOurScopeFix(decl.getTextOffset()));
    }
}
