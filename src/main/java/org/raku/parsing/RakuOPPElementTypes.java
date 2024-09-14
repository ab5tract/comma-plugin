package org.raku.parsing;

import com.intellij.psi.tree.IElementType;

/* Element types produced by operator precedence parsing. */
public interface RakuOPPElementTypes {
    IElementType PREFIX_APPLICATION = new RakuElementType("PREFIX_APPLICATION");
    IElementType POSTFIX_APPLICATION = new RakuElementType("POSTFIX_APPLICATION");
    IElementType INFIX_APPLICATION = new RakuElementType("INFIX_APPLICATION");
    IElementType ADVERB_APPLICATION = new RakuElementType("ADVERB_APPLICATION");
    IElementType REGEX_INFIX_APPLICATION = new RakuElementType("REGEX_INFIX_APPLICATION");
}
