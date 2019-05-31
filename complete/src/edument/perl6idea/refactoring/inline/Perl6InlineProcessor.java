package edument.perl6idea.refactoring.inline;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.BaseRefactoringProcessor;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class Perl6InlineProcessor extends BaseRefactoringProcessor {
    protected Perl6InlineProcessor(@NotNull Project project) {
        super(project);
    }

    protected static boolean checkIfNeedToWrap(PsiElement initializer) {
        boolean isSingular = initializer instanceof Perl6Variable ||
                             initializer instanceof Perl6ColonPair ||
                             initializer instanceof Perl6ComplexLiteral ||
                             initializer instanceof Perl6IntLiteral ||
                             initializer instanceof Perl6NumLiteral ||
                             initializer instanceof Perl6RatLiteral ||
                             initializer instanceof Perl6RegexLiteral ||
                             initializer instanceof Perl6StrLiteral ||
                             initializer instanceof Perl6Contextualizer ||
                             initializer instanceof Perl6ArrayComposer ||
                             initializer instanceof Perl6BlockOrHash;
        return !isSingular;
    }
}
