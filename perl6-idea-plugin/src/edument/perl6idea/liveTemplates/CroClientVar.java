package edument.perl6idea.liveTemplates;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroBase;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CroClientVar extends MacroBase {
  public CroClientVar() {
    super("croClientVar", "croClientVar(String)");
  }

  @Override
  protected @Nullable Result calculateResult(Expression @NotNull [] params, ExpressionContext context, boolean quick) {
    Perl6PsiElement psi = PsiTreeUtil.getParentOfType(context.getPsiElementAtStartOffset(), Perl6PsiElement.class);
    if (psi == null) {
      return null;
    }

    // TODO first in? last in?
    Optional<Perl6VariableDecl> client = psi.getLexicalSymbolVariants(Perl6SymbolKind.Variable)
      .stream()
      .map(s -> s.getPsi())
      .filter(s -> s instanceof Perl6VariableDecl)
      .map(s -> (Perl6VariableDecl)s)
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