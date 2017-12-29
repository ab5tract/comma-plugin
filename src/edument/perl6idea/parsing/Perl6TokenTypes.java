package edument.perl6idea.parsing;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

public interface Perl6TokenTypes {
    IElementType INTEGER_LITERAL = new Perl6ElementType("INTEGER_LITERAL");
    IElementType NUMBER_LITERAL = new Perl6ElementType("NUMBER_LITERAL");
    IElementType STATEMENT_TERMINATOR = new Perl6ElementType("STATEMENT_TERMINATOR");
    IElementType STATEMENT_CONTROL = new Perl6ElementType("STATEMENT_CONTROL");
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType INFIX = new Perl6ElementType("INFIX");
    IElementType VARIABLE = new Perl6ElementType("VARIABLE");
    IElementType POSTFIX = new Perl6ElementType("POSTFIX");
    IElementType NAME = new Perl6ElementType("NAME");
    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    IElementType SCOPE_DECLARATOR = new Perl6ElementType("SCOPE_DECLARATOR");
    IElementType PREFIX = new Perl6ElementType("PREFIX");
    IElementType RAT_LITERAL = new Perl6ElementType("RAT_LITERAL");
}