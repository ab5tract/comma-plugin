package edument.perl6idea.cro.template.parsing;

import com.intellij.psi.tree.IElementType;
import edument.perl6idea.cro.template.CroTemplateLanguage;
import org.jetbrains.annotations.NonNls;

public class CroTemplateElementType extends IElementType {
    public CroTemplateElementType(@NonNls String debugName) {
        super(debugName, CroTemplateLanguage.INSTANCE);
    }

    public String toString() {
        return "CroTemplate:" + super.toString();
    }
}
