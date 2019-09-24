package edument.perl6idea.refactoring.inline;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.usageView.UsageViewBundle;
import com.intellij.usageView.UsageViewDescriptor;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6InlineViewDescriptor implements UsageViewDescriptor {
  private final PsiElement myElement;

  public Perl6InlineViewDescriptor(PsiElement routine) {
    myElement = routine;
  }


  @NotNull
  @Override
  public PsiElement[] getElements() {
    return new PsiElement[]{myElement};
  }

  @Override
  public String getProcessedElementsHeader() {
    if (myElement instanceof Perl6RoutineDecl) {
      return ((Perl6RoutineDecl)myElement).getRoutineKind() + " to inline";
    } else if (myElement instanceof Perl6VariableDecl) {
      return String.join(", ", ((Perl6VariableDecl)myElement).getVariableNames()) + " to inline";
    }
    return "Unknown element";
  }

  @NotNull
  @Override
  public String getCodeReferencesText(int usagesCount, int filesCount) {
    return RefactoringBundle.message("invocations.to.be.inlined", UsageViewBundle.getReferencesString(usagesCount, filesCount));
  }

  @Nullable
  @Override
  public String getCommentReferencesText(int usagesCount, int filesCount) {
    return RefactoringBundle.message("comments.elements.header",
                                     UsageViewBundle.getOccurencesString(usagesCount, filesCount));
  }
}
