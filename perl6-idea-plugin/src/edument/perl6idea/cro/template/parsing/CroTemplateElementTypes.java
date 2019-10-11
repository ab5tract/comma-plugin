package edument.perl6idea.cro.template.parsing;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.stub.*;

public interface CroTemplateElementTypes {
    IFileElementType FILE = new CroTemplateFileElementType();
    IElementType USE = new CroTemplateElementType("USE");
}
