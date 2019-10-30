package edument.perl6idea.cro.template.parsing;

import com.intellij.psi.tree.IFileElementType;
import edument.perl6idea.cro.template.CroTemplateLanguage;

public class CroTemplateFileElementType extends IFileElementType {
    public CroTemplateFileElementType() {
        super(CroTemplateLanguage.INSTANCE);
    }
}
