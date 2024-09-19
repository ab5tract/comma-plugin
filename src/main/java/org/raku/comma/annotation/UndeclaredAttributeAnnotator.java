package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.psi.RakuParameterVariable;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.symbols.MOPSymbolsAllowed;
import org.raku.comma.psi.symbols.RakuSingleResolutionSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.utils.CommaProjectUtil;

import java.util.Objects;

public class UndeclaredAttributeAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Filter out anything except attribute usages.
        if (! (element instanceof final RakuVariable variable)) return;
        if (element.getParent() instanceof RakuVariableDecl || element.getParent() instanceof RakuParameterVariable) {
            return;
        }
        String variableName = variable.getVariableName();
        if (variableName == null || variableName.length() <= 2 || RakuVariable.getTwigil(variable.getText()) != '!') {
            return;
        }

        PsiReference reference = variable.getReference();
        if (reference == null) return;
        RakuPackageDecl enclosingPackage = variable.getSelfType();
        if (enclosingPackage == null) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                                 String.format("Attribute %s is used where no self is in scope", variableName))
                                       .range(element).create();
            return;
        }
        RakuSingleResolutionSymbolCollector collector = new RakuSingleResolutionSymbolCollector(variableName, RakuSymbolKind.Variable);
        enclosingPackage.contributeMOPSymbols(collector, new MOPSymbolsAllowed(
                true, true, true, enclosingPackage.getPackageKind().equals("role")));
        if (collector.getResult() == null && ! CommaProjectUtil.isProjectRakudo(element)) {
            holder.newAnnotation(HighlightSeverity.ERROR, String.format("Attribute %s is used, but not declared", variableName))
                  .range(element).create();
        }
    }
}
