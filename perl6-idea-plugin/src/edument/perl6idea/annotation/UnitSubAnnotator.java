package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6ScopedDecl;
import org.jetbrains.annotations.NotNull;

public class UnitSubAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6ScopedDecl))
            return;

        PsiElement declarator = element.getFirstChild();
        if (declarator != null && declarator.getText().equals("unit")) {
            PsiElement next = declarator.getNextSibling();
            while (next != null) {
                if (next instanceof Perl6RoutineDecl) break;
                next = next.getNextSibling();
            }
            if (next == null) return;
            Perl6LongName name = PsiTreeUtil.findChildOfType(element, Perl6LongName.class);
            if (name == null) return;
            if (!name.getText().equals("MAIN"))
                holder.newAnnotation(HighlightSeverity.ERROR, "The unit sub syntax is only allowed for the sub MAIN")
                    .range(declarator).create();
        }
    }
}
