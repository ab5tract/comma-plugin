package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.psi.*;
import org.raku.psi.effects.Effect;
import org.raku.psi.effects.EffectCollection;
import org.raku.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class UselessUseAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof RakuStatementList &&
        (element.getParent() instanceof RakuBlockoid || element.getParent() instanceof RakuFile)) {
      RakuStatement[] statements = getStatements(element);
      boolean lastSunk = isLastStatementSunk((RakuStatementList)element);
      for (int i = 0; i < statements.length - (lastSunk ? 0 : 1); i++) {
        EffectCollection effect = statements[i].inferEffects();
        if (!effect.is(Effect.IMPURE) && !effect.is(Effect.DECLARATION))
          holder.newAnnotation(HighlightSeverity.WARNING, "Useless use of value in sink (void) context")
              .range(statements[i])
              .create();
      }
    }
  }

  private static RakuStatement[] getStatements(@NotNull PsiElement element) {
    return Arrays.stream(element.getChildren())
      .filter(c -> c instanceof RakuStatement)
      .toArray(RakuStatement[]::new);
  }

  private static boolean isLastStatementSunk(RakuStatementList statementList) {
    // Last in file will be sunk always.
    PsiElement parent = statementList.getParent();
    if (parent instanceof RakuFile)
      return true;

    // Otherwise, ensure we have a blockoid; all other cases we consider
    // as not sunk (which is conservative: it won't lead to bogus useless
    // use warnings.)
    if (!(parent instanceof RakuBlockoid))
      return false;

    // If we're in a routine declaration, check the return type; if it's
    // Nil then we're sunk.
    PsiElement blockoidParent = parent.getParent();
    if (blockoidParent instanceof RakuRoutineDecl) {
      RakuType type = ((RakuRoutineDecl)blockoidParent).getReturnType();
      return type.nominalType().getName().equals("Nil");
    }

    // Pointy blocks and normal blocks need us to look further out.
    if (blockoidParent instanceof RakuPointyBlock || blockoidParent instanceof RakuBlock) {
      // If it's a statement-level loop, then always sunk.
      PsiElement holder = blockoidParent.getParent();
      if (isLoop(holder))
        return holder.getParent() instanceof RakuStatement;

      // If it's a conditional, then sunk if the conditional is both
      // statement level and itself sunk.
      if (isConditional(holder)) {
        PsiElement maybeStatement = holder.getParent();
        if (maybeStatement instanceof RakuStatement) {
          PsiElement maybeStatemnetList = maybeStatement.getParent();
          if (maybeStatemnetList instanceof RakuStatementList) {
            RakuStatement[] statements = getStatements(maybeStatemnetList);
            return statements[statements.length - 1] != maybeStatement ||
                   isLastStatementSunk((RakuStatementList)maybeStatemnetList);
          }
        }
        return false;
      }
    }

    return false;
  }

  private static boolean isConditional(PsiElement holder) {
    return holder instanceof RakuIfStatement ||
           holder instanceof RakuUnlessStatement ||
           holder instanceof RakuWithoutStatement;
  }

  private static boolean isLoop(PsiElement holder) {
    return holder instanceof RakuForStatement ||
           holder instanceof RakuWhileStatement ||
           holder instanceof RakuUntilStatement ||
           holder instanceof RakuRepeatStatement ||
           holder instanceof RakuLoopStatement;
  }
}
