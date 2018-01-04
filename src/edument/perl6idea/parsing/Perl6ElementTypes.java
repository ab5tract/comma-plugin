package edument.perl6idea.parsing;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import edument.perl6idea.Perl6Language;

public interface Perl6ElementTypes {
    IFileElementType FILE = new IFileElementType(Perl6Language.INSTANCE);
    IElementType INTEGER_LITERAL = new Perl6ElementType("INTEGER_LITERAL");
    IElementType NUMBER_LITERAL = new Perl6ElementType("NUMBER_LITERAL");
    IElementType HYPER_WHATEVER = new Perl6ElementType("HYPER_WHATEVER");
    IElementType FOR_STATEMENT = new Perl6ElementType("FOR_STATEMENT");
    IElementType ROUTINE_DECLARATION = new Perl6ElementType("ROUTINE_DECLARATION");
    IElementType WHENEVER_STATEMENT = new Perl6ElementType("WHENEVER_STATEMENT");
    IElementType REPEAT_STATEMENT = new Perl6ElementType("REPEAT_STATEMENT");
    IElementType UNTIL_STATEMENT = new Perl6ElementType("UNTIL_STATEMENT");
    IElementType INFIX = new Perl6ElementType("INFIX");
    IElementType VARIABLE_DECLARATION = new Perl6ElementType("VARIABLE_DECLARATION");
    IElementType SCOPED_DECLARATION = new Perl6ElementType("SCOPED_DECLARATION");
    IElementType VARIABLE = new Perl6ElementType("VARIABLE");
    IElementType WITHOUT_STATEMENT = new Perl6ElementType("WITHOUT_STATEMENT");
    IElementType POSTFIX = new Perl6ElementType("POSTFIX");
    IElementType TYPE_NAME = new Perl6ElementType("TYPE_NAME");
    IElementType LOOP_STATEMENT = new Perl6ElementType("LOOP_STATEMENT");
    IElementType METHOD_CALL = new Perl6ElementType("METHOD_CALL");
    IElementType PREFIX = new Perl6ElementType("PREFIX");
    IElementType EXPR = new Perl6ElementType("EXPR");
    IElementType STRING_LITERAL = new Perl6ElementType("STRING_LITERAL");
    IElementType SIGNATURE = new Perl6ElementType("SIGNATURE");
    IElementType WHILE_STATEMENT = new Perl6ElementType("WHILE_STATEMENT");
    IElementType UNLESS_STATEMENT = new Perl6ElementType("UNLESS_STATEMENT");
    IElementType STATEMENT = new Perl6ElementType("STATEMENT");
    IElementType RAT_LITERAL = new Perl6ElementType("RAT_LITERAL");
    IElementType SUB_CALL = new Perl6ElementType("SUB_CALL");
    IElementType USE_STATEMENT = new Perl6ElementType("USE_STATEMENT");
    IElementType STATEMENT_LIST = new Perl6ElementType("STATEMENT_LIST");
    IElementType MULTI_DECLARATION = new Perl6ElementType("MULTI_DECLARATION");
    IElementType WHATEVER = new Perl6ElementType("WHATEVER");
    IElementType IF_STATEMENT = new Perl6ElementType("IF_STATEMENT");
}
