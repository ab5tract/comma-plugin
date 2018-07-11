package edument.perl6idea.parsing;

import com.intellij.psi.tree.IElementType;
import edument.perl6idea.Perl6Language;
import org.jetbrains.annotations.NonNls;

public class Perl6ElementType extends IElementType {
    public Perl6ElementType(@NonNls String debugName) {
        super(debugName, Perl6Language.INSTANCE);
    }

    public String toString() {
        return "Perl6:" + super.toString();
    }
}
