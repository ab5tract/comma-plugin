package edument.perl6idea.cro.template.parsing;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

public interface CroTemplateTokenTypes {
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType LITERAL_TEXT = new CroTemplateElementType("LITERAL_TEXT");
    IElementType USE_OPENER = new CroTemplateElementType("USE_OPENER");
    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
}
