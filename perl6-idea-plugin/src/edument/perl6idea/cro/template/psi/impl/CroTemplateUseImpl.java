package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import edument.perl6idea.cro.template.psi.CroTemplateFile;
import edument.perl6idea.cro.template.psi.CroTemplateStringLiteral;
import edument.perl6idea.cro.template.psi.CroTemplateUse;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.jetbrains.annotations.NotNull;

public class CroTemplateUseImpl extends ASTWrapperPsiElement implements CroTemplateUse {
    public CroTemplateUseImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void declareToCollector(CroTemplateSymbolCollector collector) {
        CroTemplateStringLiteral stringUse = findChildByClass(CroTemplateStringLiteral.class);
        if (stringUse != null) {
            declareFileSymbols(collector, stringUse.getStringValue());
        }
    }

    private void declareFileSymbols(CroTemplateSymbolCollector collector, String file) {
        // Locate the template file relative to this one.
        PsiDirectory cwd = getContainingFile().getOriginalFile().getContainingDirectory();
        String[] parts = file.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (cwd == null)
                return;
            cwd = cwd.findSubdirectory(parts[i]);
        }
        if (cwd == null)
            return;
        PsiFile referencedFile = cwd.findFile(parts[parts.length - 1]);
        if (!(referencedFile instanceof CroTemplateFile))
            return;

        // If we get here, then we resolved the file.
        ((CroTemplateFile)referencedFile).declareExportedSymbols(collector);
    }
}
