package org.raku.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.raku.RakuLanguage;
import org.raku.psi.RakuPsiElement;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuContext extends TemplateContextType {
    protected RakuContext() {
        super("Raku statement");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        PsiFile file = context.getFile();
        if (!file.getLanguage().is(RakuLanguage.getInstance()))
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
        // Assume Raku elements providing Raku context
        return element instanceof RakuPsiElement;
    }
}
