package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class UndeclaredRoutineAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (!(element instanceof Perl6SubCall))
      return;
    final Perl6SubCall call = (Perl6SubCall)element;
    String subName = call.getCallName();
    if (subName == null)
      return;

    Perl6Symbol resolved = call.resolveSymbol(Perl6SymbolKind.Routine, subName);
    if (resolved == null) {
      holder.createErrorAnnotation(
              element,
              String.format("Subroutine %s is not declared", subName));
    }
  }
}
