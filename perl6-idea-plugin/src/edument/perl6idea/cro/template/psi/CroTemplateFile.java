package edument.perl6idea.cro.template.psi;

import com.intellij.psi.PsiFile;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface CroTemplateFile extends PsiFile, Scope {
    void declareExportedSymbols(CroTemplateSymbolCollector collector);
}
