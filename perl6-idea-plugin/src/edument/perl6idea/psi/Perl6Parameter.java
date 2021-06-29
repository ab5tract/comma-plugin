package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Perl6Parameter extends Perl6PsiElement, Perl6PsiDeclaration, Perl6LexicalSymbolContributor {
    String summary(boolean includeName);
    String getVariableName();
    List<String> getVariableNames();
    @Nullable
    PsiElement getInitializer();
    boolean isPositional();
    boolean isNamed();
    Perl6PsiElement getValueConstraint();
    boolean isSlurpy();
    boolean isRequired();
    boolean isOptional();
    boolean isExplicitlyOptional();
    boolean isCopy();
    boolean isRW();
}
