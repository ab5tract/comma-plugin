package edument.perl6idea.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafElement;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6PsiElement;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuContext extends TemplateContextType {
    protected RakuContext() {
        super("Raku statement");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        PsiFile file = context.getFile();
        if (!file.getLanguage().is(Perl6Language.INSTANCE))
            return false;
        return isValidContext(context, file);
    }

  /*
   * Make sure the template is being expanded
   */
    private static boolean isValidContext(@NotNull TemplateActionContext context, PsiFile file) {
        PsiElement element = file.findElementAt(context.getStartOffset());
        if (!(element instanceof LeafElement))
            return false;
        element = element.getParent();
        // Assume Perl6 elements providing Raku context
        return element instanceof Perl6PsiElement;
    }
}
