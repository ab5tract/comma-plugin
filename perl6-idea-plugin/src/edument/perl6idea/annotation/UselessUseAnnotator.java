package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.effects.Effect;
import edument.perl6idea.psi.effects.EffectCollection;
import edument.perl6idea.psi.type.Perl6Type;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class UselessUseAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof Perl6StatementList &&
        (element.getParent() instanceof Perl6Blockoid || element.getParent() instanceof Perl6File)) {
      Perl6Statement[] statements = getStatements(element);
      boolean lastSunk = isLastStatementSunk((Perl6StatementList)element);
      for (int i = 0; i < statements.length - (lastSunk ? 0 : 1); i++) {
        EffectCollection effect = statements[i].inferEffects();
        if (!effect.is(Effect.IMPURE) && !effect.is(Effect.DECLARATION))
          holder.newAnnotation(HighlightSeverity.WARNING, "Useless use of value in sink (void) context")
              .range(statements[i])
              .create();
      }
    }
  }

  private static Perl6Statement[] getStatements(@NotNull PsiElement element) {
    return Arrays.stream(element.getChildren())
      .filter(c -> c instanceof Perl6Statement)
      .toArray(Perl6Statement[]::new);
  }

  private static boolean isLastStatementSunk(Perl6StatementList statementList) {
    // Last in file will be sunk always.
    PsiElement parent = statementList.getParent();
    if (parent instanceof Perl6File)
      return true;

    // Otherwise, ensure we have a blockoid; all other cases we consider
    // as not sunk (which is conservative: it won't lead to bogus useless
    // use warnings.)
    if (!(parent instanceof Perl6Blockoid))
      return false;

    // If we're in a routine declaration, check the return type; if it's
    // Nil then we're sunk.
    PsiElement blockoidParent = parent.getParent();
    if (blockoidParent instanceof Perl6RoutineDecl) {
      Perl6Type type = ((Perl6RoutineDecl)blockoidParent).getReturnType();
      return type.nominalType().getName().equals("Nil");
    }

    // Pointy blocks and normal blocks need us to look further out.
    if (blockoidParent instanceof Perl6PointyBlock || blockoidParent instanceof Perl6Block) {
      // If it's a statement-level loop, then always sunk.
      PsiElement holder = blockoidParent.getParent();
      if (isLoop(holder))
        return holder.getParent() instanceof Perl6Statement;

      // If it's a conditional, then sunk if the conditional is both
      // statement level and itself sunk.
      if (isConditional(holder)) {
        PsiElement maybeStatement = holder.getParent();
        if (maybeStatement instanceof Perl6Statement) {
          PsiElement maybeStatemnetList = maybeStatement.getParent();
          if (maybeStatemnetList instanceof Perl6StatementList) {
            Perl6Statement[] statements = getStatements(maybeStatemnetList);
            return statements[statements.length - 1] != maybeStatement ||
                   isLastStatementSunk((Perl6StatementList)maybeStatemnetList);
          }
        }
        return false;
      }
    }

    return false;
  }

  private static boolean isConditional(PsiElement holder) {
    return holder instanceof Perl6IfStatement ||
           holder instanceof Perl6UnlessStatement ||
           holder instanceof Perl6WithoutStatement;
  }

  private static boolean isLoop(PsiElement holder) {
    return holder instanceof Perl6ForStatement ||
           holder instanceof Perl6WhileStatement ||
           holder instanceof Perl6UntilStatement ||
           holder instanceof Perl6RepeatStatement ||
           holder instanceof Perl6LoopStatement;
  }
}
