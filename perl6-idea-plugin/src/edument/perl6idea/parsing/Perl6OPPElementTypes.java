package edument.perl6idea.parsing;

import com.intellij.psi.tree.IElementType;

/* Element types produced by operator precedence parsing. */
public interface Perl6OPPElementTypes {
    IElementType PREFIX_APPLICATION = new Perl6ElementType("PREFIX_APPLICATION");
    IElementType POSTFIX_APPLICATION = new Perl6ElementType("POSTFIX_APPLICATION");
    IElementType INFIX_APPLICATION = new Perl6ElementType("INFIX_APPLICATION");
    IElementType ADVERB_APPLICATION = new Perl6ElementType("ADVERB_APPLICATION");
}
