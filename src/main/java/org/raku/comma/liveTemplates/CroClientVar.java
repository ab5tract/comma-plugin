package org.raku.comma.liveTemplates;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CroClientVar extends MacroBase {
  public CroClientVar() {
    super("croClientVar", "croClientVar(String)");
  }

  @Override
  protected @Nullable Result calculateResult(Expression @NotNull [] params, ExpressionContext context, boolean quick) {
    RakuPsiElement psi = PsiTreeUtil.getParentOfType(context.getPsiElementAtStartOffset(), RakuPsiElement.class);
    if (psi == null) {
      return null;
    }

    // TODO first in? last in?
    Optional<RakuVariableDecl> client = psi.getLexicalSymbolVariants(RakuSymbolKind.Variable)
      .stream()
      .map(s -> s.getPsi())
      .filter(s -> s instanceof RakuVariableDecl)
      .map(s -> (RakuVariableDecl)s)
      .filter(s -> s.getName() != null)
      .filter(p -> "Cro::HTTP::Client".equals(p.inferType().getName()))
      .findFirst();

    return new TextResult(client.map(v -> v.getName()).orElse("Cro::HTTP::Client"));
  }

  @Override
  public boolean isAcceptableInContext(TemplateContextType context) {
    return context instanceof RakuContext;
  }
}