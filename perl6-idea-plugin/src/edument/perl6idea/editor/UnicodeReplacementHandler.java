package edument.perl6idea.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.utils.Perl6OperatorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnicodeReplacementHandler extends TypedHandlerDelegate {
    public static final Key<Integer> UNICODE_REPLACEMENT_POS = Key.create("perl6.unicodeReplacementPos");

    private static class Mapping {
        public String ascii;
        public char unicode;

        private Mapping(String ascii, char unicode) {
            this.ascii = ascii;
            this.unicode = unicode;
        }
    }

    private static final Map<Character, List<Mapping>> mappings = new HashMap<>();

    private static void registerMapping(char unicode, String ascii) {
        Mapping m = new Mapping(ascii, unicode);
        Character finalChar = m.ascii.charAt(ascii.length() - 1);
        List<Mapping> charMappings;
        if (mappings.containsKey(finalChar)) {
            charMappings = mappings.get(finalChar);
        }
        else {
            charMappings = new ArrayList<>();
            mappings.put(finalChar, charMappings);
        }
        charMappings.add(m);
        charMappings.sort((a, b) -> b.ascii.length() - a.ascii.length());
    }

    static {
        for (Pair<Character, String> op : ContainerUtil.zip(Perl6OperatorUtils.unicodeOperators, Perl6OperatorUtils.asciiOperators)) {
            registerMapping(op.getFirst(), op.getSecond());
        }
    }

    /**
     * Called after the specified character typed by the user has been inserted in the editor.
     * We then check if it's an ASCII form of something we might be able to replace with a more
     * elegant Unicode form. We first do a quick check if the character is one ending something
     * we might consider replacing, then look back at the recent chars, and finally check if it
     * is of the correct element type before doing the replacement.
     */
    @Override
    @NotNull
    public Result charTyped(char c, @NotNull Project project, @NotNull final Editor editor, @NotNull final PsiFile file) {
        if (!Perl6OperatorUtils.isUnicodeConversionEnabled(editor))
            return Result.CONTINUE;

        List<Mapping> charMappings = mappings.get(c);
        if (charMappings != null) {
            int offset = editor.getCaretModel().getOffset();
            String text = editor.getDocument().getText();
            for (Mapping m : charMappings) {
                int start = offset - m.ascii.length();
                if (start < 0)
                    continue;
                if (text.substring(start, offset).equals(m.ascii)) {
                    // Text matches, but what about token type?
                    HighlighterIterator iterator = ((EditorEx)editor).getHighlighter().createIterator(start);
                    IElementType curToken = iterator.getTokenType();
                    if (curToken == Perl6TokenTypes.METAOP || curToken == Perl6TokenTypes.INFIX) {
                        // It's a match! Replace.
                        editor.getDocument().setText(
                                text.substring(0, start) +
                                m.unicode +
                                text.substring(offset)
                        );
                        editor.putUserData(UNICODE_REPLACEMENT_POS, start + 1);
                        return Result.STOP;
                    }
                }
            }
        }
        editor.putUserData(UNICODE_REPLACEMENT_POS, null);
        return Result.CONTINUE;
    }
}
