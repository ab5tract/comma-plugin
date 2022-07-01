package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Perl6Parameter extends Perl6PsiElement, Perl6PsiDeclaration, Perl6LexicalSymbolContributor, Perl6VariableSource {
    String summary(boolean includeName);
    String getVariableName();
    @Nullable
    PsiElement getInitializer();
    boolean isPositional();
    boolean isNamed();
    @Nullable
    Perl6WhereConstraint getWhereConstraint();
    @Nullable
    Perl6PsiElement getValueConstraint();
    boolean isSlurpy();
    boolean isRequired();
    boolean isOptional();
    boolean isExplicitlyOptional();
    boolean isCopy();
    boolean isRW();
    boolean equalsParameter(Perl6Parameter other);
}
