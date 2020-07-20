package edument.perl6idea.providers;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6Heredoc;
import edument.perl6idea.psi.Perl6StrLiteral;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.LanguageInjectionSupport;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Perl6MultiHostInjector implements MultiHostInjector {
  @Override
  public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
    InjectedLanguage injected = context.getUserData(LanguageInjectionSupport.TEMPORARY_INJECTED_LANGUAGE);
    if (injected == null || injected.getLanguage() == null)
      return;
    if (context instanceof Perl6StrLiteral)
      injectStrLiteral(registrar, (Perl6StrLiteral)context, injected.getLanguage());
    if (context instanceof Perl6Heredoc && ((Perl6Heredoc)context).getIndentation() != 0)
      injectHeredoc(registrar, (Perl6Heredoc)context, injected.getLanguage());
  }

  private void injectStrLiteral(@NotNull MultiHostRegistrar registrar, @NotNull Perl6StrLiteral context, @NotNull Language language) {
    registrar.startInjecting(language);
    int i = 0;
    int start = 0;
    for (ASTNode child : context.getNode().getChildren(TokenSet.ANY)) {
      // start textual bit
      if (start == 0 && child.getElementType() == Perl6TokenTypes.STRING_LITERAL_CHAR)
        start = i;
      // end textual bit
      if (start != 0 && child.getElementType() != Perl6TokenTypes.STRING_LITERAL_CHAR) {
        registrar.addPlace(null, null, context, new TextRange(start, i));
        start = 0;
      }
      i += child.getTextLength();
    }
    registrar.doneInjecting();
  }

  private void injectHeredoc(@NotNull MultiHostRegistrar registrar, @NotNull Perl6Heredoc context, @NotNull Language language) {
    registrar.startInjecting(language);
    int indentation = context.getIndentation();
    int end = context.getStringText().length();
    int strLen = 0;
    ASTNode[] nodes = context.getNode().getChildren(TokenSet.ANY);
    for (int i = 0; strLen < end && i < nodes.length; i++) {
      for (int j = 0; j < indentation && nodes[i].getText().equals(" "); ++j, ++i)
        strLen += nodes[i].getTextLength();
      int start = strLen;
      for (; strLen < end && i < nodes.length && !nodes[i].getText().equals("\n"); ++i)
         strLen += nodes[i].getTextLength();
      registrar.addPlace(null, null, context, new TextRange(start, strLen));
      strLen += 1;
    }
    registrar.doneInjecting();
  }

  @Override
  public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    return Arrays.asList(Perl6StrLiteral.class, Perl6Heredoc.class);
  }
}
