package edument.perl6idea.parsing;

import com.intellij.lang.cacheBuilder.VersionedWordsScanner;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

// A straightforward copy of `DefaultWordsScanner` class,
// tweaked to handle Raku attributes
public class Perl6WordsScanner extends VersionedWordsScanner {
    private final Lexer myLexer;
    private final TokenSet myIdentifierTokenSet;
    private final TokenSet myCommentTokenSet;
    private final TokenSet myLiteralTokenSet;

    public Perl6WordsScanner() {
        myLexer = new Perl6Lexer();
        myIdentifierTokenSet = TokenSet.create(
            Perl6TokenTypes.NAME, Perl6TokenTypes.SUB_CALL_NAME,
            Perl6TokenTypes.METHOD_CALL_NAME, Perl6TokenTypes.VARIABLE,
            Perl6TokenTypes.ROUTINE_NAME, Perl6TokenTypes.PACKAGE_NAME
        );
        myCommentTokenSet = TokenSet.create(Perl6TokenTypes.COMMENT, Perl6TokenTypes.POD_TEXT, Perl6TokenTypes.POD_FINISH_TEXT);
        myLiteralTokenSet = TokenSet.create(
            Perl6TokenTypes.INTEGER_LITERAL, Perl6TokenTypes.COMPLEX_LITERAL,
            Perl6TokenTypes.NUMBER_LITERAL, Perl6TokenTypes.RAT_LITERAL);
    }

    @Override
    public void processWords(CharSequence fileText, Processor<WordOccurrence> processor) {
        myLexer.start(fileText);
        WordOccurrence occurrence = new WordOccurrence(fileText, 0, 0, null); // shared occurrence

        IElementType type;
        while ((type = myLexer.getTokenType()) != null) {
            if (myIdentifierTokenSet.contains(type)) {
                String text = myLexer.getTokenText();
                if (type == Perl6TokenTypes.VARIABLE) {
                    if (text.length() > 1) {
                        String sigilLess = text.substring(Character.isLetter(text.charAt(1)) ? 1 : 2);
                        if (indexByDash(processor, occurrence, sigilLess)) return;
                    }
                    if (text.startsWith("$")) {
                        occurrence.init("$", 0, 1, WordOccurrence.Kind.CODE);
                        if (!processor.process(occurrence)) return;
                        occurrence.init(text, 0, text.length(), WordOccurrence.Kind.CODE);
                        if (!processor.process(occurrence)) return;
                    }
                } else if (type == Perl6TokenTypes.NAME) {
                    String fixedName = text;
                    // If it is nested NAME(`Foo` inside of `Bar` gives `Bar::Foo`), provide a last part
                    // version so that usages like `Bar::Foo` could we matched with `Foo` in `class Foo`
                    if (fixedName.indexOf(':') != -1) {
                        int lastPartIndex = fixedName.lastIndexOf(':') + 1;
                        fixedName = fixedName.substring(lastPartIndex);
                    }
                    occurrence.init(fixedName, 0, fixedName.length(), WordOccurrence.Kind.CODE);
                    if (!processor.process(occurrence)) return;
                } else if (type == Perl6TokenTypes.METHOD_CALL_NAME || type == Perl6TokenTypes.ROUTINE_NAME ||
                  type == Perl6TokenTypes.SUB_CALL_NAME) {
                    if (text.indexOf('-') == -1) {
                        occurrence.init(text, 0, text.length(), WordOccurrence.Kind.CODE);
                        if (!processor.process(occurrence)) return;
                    } else {
                        if (indexByDash(processor, occurrence, text)) return;
                    }
                } else {
                    occurrence.init(text, 0, text.length(), WordOccurrence.Kind.CODE);
                    if (!processor.process(occurrence)) return;
                }
            }
            else if (myCommentTokenSet.contains(type)) {
                if (!stripWords(processor, fileText, myLexer.getTokenStart(), myLexer.getTokenEnd(), WordOccurrence.Kind.COMMENTS, occurrence)) return;
            }
            else if (myLiteralTokenSet.contains(type)) {
                if (!stripWords(processor, fileText, myLexer.getTokenStart(), myLexer.getTokenEnd(), WordOccurrence.Kind.LITERALS, occurrence)) return;
            }
            myLexer.advance();
        }
    }

    private static boolean indexByDash(Processor<WordOccurrence> processor, WordOccurrence occurrence, String text) {
        String[] splitted = text.split("-");
        for (int i = 0; i < splitted.length; i++) {
            occurrence.init(splitted[i], 0, splitted[i].length(), WordOccurrence.Kind.CODE);
            if (!processor.process(occurrence)) return true;
            if (i + 1 != splitted.length) {
                occurrence.init("-", 0, 1, WordOccurrence.Kind.CODE);
                if (!processor.process(occurrence)) return true;
            }
        }
        return false;
    }

    protected static boolean stripWords(final Processor<? super WordOccurrence> processor,
                                        final CharSequence tokenText,
                                        int from,
                                        int to,
                                        final WordOccurrence.Kind kind,
                                        @NotNull WordOccurrence occurrence
    ) {
        // This code seems strange but it is more effective as Character.isJavaIdentifier_xxx_ is quite costly operation due to unicode
        int index = from;

        ScanWordsLoop:
        while (true) {
            while (true) {
                if (index == to) break ScanWordsLoop;
                char c = tokenText.charAt(index);
                if (isAsciiIdentifierPart(c) || Character.isJavaIdentifierStart(c)) {
                    break;
                }
                index++;
            }
            int wordStart = index;
            while (true) {
                index++;
                if (index == to) break;
                char c = tokenText.charAt(index);
                if (isAsciiIdentifierPart(c)) continue;
                if (!Character.isJavaIdentifierPart(c)) break;
            }
            int wordEnd = index;
            occurrence.init(tokenText, wordStart, wordEnd, kind);

            if (!processor.process(occurrence)) return false;
        }
        return true;
    }

    private static boolean isAsciiIdentifierPart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '$';
    }
}
