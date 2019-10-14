package edument.perl6idea.cro.template.parsing;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.stub.*;

public interface CroTemplateElementTypes {
    IFileElementType FILE = new CroTemplateFileElementType();
    IElementType APPLY = new CroTemplateElementType("APPLY");
    IElementType ARGLIST = new CroTemplateElementType("ARGLIST");
    IElementType INFIX = new CroTemplateElementType("INFIX");
    IElementType INT_LITERAL = new CroTemplateElementType("INT_LITERAL");
    IElementType LITERAL_CLOSE_TAG = new CroTemplateElementType("LITERAL_CLOSE_TAG");
    IElementType LITERAL_OPEN_TAG = new CroTemplateElementType("LITERAL_OPEN_TAG");
    IElementType LITERAL_TAG_ATTRIBUTE = new CroTemplateElementType("LITERAL_TAG_ATTRIBUTE");
    IElementType NUM_LITERAL = new CroTemplateElementType("NUM_LITERAL");
    IElementType RAT_LITERAL = new CroTemplateElementType("RAT_LITERAL");
    IElementType STRING_LITERAL = new CroTemplateElementType("STRING_LITERAL");
    IElementType USE = new CroTemplateElementType("USE");
}
