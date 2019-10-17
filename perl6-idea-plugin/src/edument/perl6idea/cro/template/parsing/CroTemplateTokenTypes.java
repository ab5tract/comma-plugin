package edument.perl6idea.cro.template.parsing;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

public interface CroTemplateTokenTypes {
    IElementType ATTRIBUTE_EQUALS = new CroTemplateElementType("ATTRIBUTE_EQUALS");
    IElementType ATTRIBUTE_NAME = new CroTemplateElementType("ATTRIBUTE_NAME");
    IElementType ATTRIBUTE_QUOTE = new CroTemplateElementType("ATTRIBUTE_QUOTE");
    IElementType ATTRIBUTE_VALUE = new CroTemplateElementType("ATTRIBUTE_VALUE");
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType CLOSE_PAREN = new CroTemplateElementType("CLOSE_PAREN");
    IElementType COMMA = new CroTemplateElementType("COMMA");
    IElementType DECL_OPENER = new CroTemplateElementType("DECL_OPENER");
    IElementType DOT = new CroTemplateElementType("DOT");
    IElementType END_OF_EXPR = new CroTemplateElementType("END_OF_EXPR");
    IElementType IDENTIFER = new CroTemplateElementType("IDENTIFER");
    IElementType INFIX = new CroTemplateElementType("INFIX");
    IElementType INT_LITERAL = new CroTemplateElementType("INT_LITERAL");
    IElementType LITERAL_TAG_CLOSE = new CroTemplateElementType("LITERAL_TAG_CLOSE");
    IElementType LITERAL_TAG_NAME = new CroTemplateElementType("LITERAL_TAG_NAME");
    IElementType LITERAL_TAG_OPEN = new CroTemplateElementType("LITERAL_TAG_OPEN");
    IElementType LITERAL_TAG_SLASH = new CroTemplateElementType("LITERAL_TAG_SLASH");
    IElementType LITERAL_TEXT = new CroTemplateElementType("LITERAL_TEXT");
    IElementType MACRO_NAME = new CroTemplateElementType("MACRO_NAME");
    IElementType NUM_LITERAL = new CroTemplateElementType("NUM_LITERAL");
    IElementType OPEN_PAREN = new CroTemplateElementType("OPEN_PAREN");
    IElementType RAT_LITERAL = new CroTemplateElementType("RAT_LITERAL");
    IElementType STRING_QUOTE_SINGLE = new CroTemplateElementType("STRING_QUOTE_SINGLE");
    IElementType STRING_TEXT = new CroTemplateElementType("STRING_TEXT");
    IElementType SYNTAX_WHITE_SPACE = new CroTemplateElementType("SYNTAX_WHITE_SPACE");
    IElementType TEMPLATE_TAG_APPLY_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_APPLY_SIGIL");
    IElementType TEMPLATE_TAG_CLOSE = new CroTemplateElementType("TEMPLATE_TAG_CLOSE");
    IElementType TEMPLATE_TAG_COND_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_COND_SIGIL");
    IElementType TEMPLATE_TAG_DECL_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_DECL_SIGIL");
    IElementType TEMPLATE_TAG_ITER_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_ITER_SIGIL");
    IElementType TEMPLATE_TAG_OPEN = new CroTemplateElementType("TEMPLATE_TAG_OPEN");
    IElementType TEMPLATE_TAG_SLASH = new CroTemplateElementType("TEMPLATE_TAG_SLASH");
    IElementType TEMPLATE_TAG_VAR_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_VAR_SIGIL");
}
