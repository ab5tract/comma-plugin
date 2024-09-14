package org.raku.formatter;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor;
import org.jetbrains.annotations.NotNull;

public class RakuFormatPostprocessor implements PostFormatProcessor {
    @NotNull
    @Override
    public PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
        CodeFormatterFacade facade = new CodeFormatterFacade(settings, source.getLanguage(), false);
        ASTNode node = facade.processElement(source.getNode());
        return facade.processElement(node).getPsi();
    }

    @NotNull
    @Override
    public TextRange processText(@NotNull PsiFile source, @NotNull TextRange rangeToReformat, @NotNull CodeStyleSettings settings) {
        return processElement(source, settings).getTextRange();
    }
}
