package edument.perl6idea.psi.impl;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6Heredoc;
import org.jetbrains.annotations.NotNull;

public class Perl6HeredocImpl extends ASTWrapperPsiElement implements Perl6Heredoc {
    public Perl6HeredocImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getStringText() {
        String text = StringUtil.trimEnd(getText(), '\n');
        return text.substring(0, text.lastIndexOf('\n'));
    }

  @Override
  public int getIndentation() {
    String text = StringUtil.trimEnd(getText(), '\n');
    String lastLine = text.substring(text.lastIndexOf('\n') + 1, text.length() - 1);
    int i = 0;
    while (lastLine.charAt(i) == ' ')
      i++;
    return i;
  }

  @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        return this;
    }

    @Override
    public @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
      return LiteralTextEscaper.createSimple(this);
    }
}
