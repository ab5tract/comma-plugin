package edument.perl6idea.parsing;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import edument.perl6idea.Perl6Language;

public interface Perl6ElementTypes {
    IFileElementType FILE = new IFileElementType(Perl6Language.INSTANCE);
    IElementType INTEGER_LITERAL = new Perl6ElementType("INTEGER_LITERAL");
    IElementType NUMBER_LITERAL = new Perl6ElementType("NUMBER_LITERAL");
    IElementType INFIX = new Perl6ElementType("INFIX");
    IElementType VARIABLE_DECLARATION = new Perl6ElementType("VARIABLE_DECLARATION");
    IElementType SCOPED_DECLARATION = new Perl6ElementType("SCOPED_DECLARATION");
    IElementType VARIABLE = new Perl6ElementType("VARIABLE");
    IElementType POSTFIX = new Perl6ElementType("POSTFIX");
    IElementType PREFIX = new Perl6ElementType("PREFIX");
    IElementType EXPR = new Perl6ElementType("EXPR");
    IElementType STATEMENT = new Perl6ElementType("STATEMENT");
    IElementType RAT_LITERAL = new Perl6ElementType("RAT_LITERAL");
    IElementType USE_STATEMENT = new Perl6ElementType("USE_STATEMENT");
    IElementType STATEMENT_LIST = new Perl6ElementType("STATEMENT_LIST");
}