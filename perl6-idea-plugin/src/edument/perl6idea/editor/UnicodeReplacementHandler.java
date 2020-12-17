package edument.perl6idea.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6HyperMetaOp;
import edument.perl6idea.psi.Perl6Infix;
import edument.perl6idea.utils.Perl6OperatorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnicodeReplacementHandler extends TypedHandlerDelegate {
    public static final Key<Pair<Pair<Integer, Integer>, String>> UNICODE_REPLACEMENT_POS = Key.create("perl6.unicodeReplacementPos");

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
                    if (curToken == Perl6TokenTypes.INFIX) {
                        if (m.ascii.equals(">>") || m.ascii.equals("<<"))
                            return Result.CONTINUE;
                        // It's a match! Replace.
                        editor.getDocument().setText(text.substring(0, start) + m.unicode + text.substring(offset));
                        editor.putUserData(UNICODE_REPLACEMENT_POS, Pair.create(Pair.create(offset - m.ascii.length(), 0), m.ascii));
                        return Result.STOP;
                    } else if (curToken == Perl6TokenTypes.METAOP) {
                        PsiElement el = file.findElementAt(start - 1);
                        Perl6HyperMetaOp hyperMeta = PsiTreeUtil.getParentOfType(el, Perl6HyperMetaOp.class);
                        // For cases like `@a>>`, but `@a >>` is a hyper meta start...
                        if (hyperMeta == null) {
                            editor.getDocument().setText(text.substring(0, start) + m.unicode + text.substring(offset));
                            editor.putUserData(UNICODE_REPLACEMENT_POS, Pair.create(Pair.create(offset - m.ascii.length(), 0), m.ascii));
                            return Result.STOP;
                        } else if (hyperMeta != null) {
                            String op = text.substring(start, offset);
                            processHyperPart(editor, offset, text, start, op);
                            text = editor.getDocument().getText();
                            PsiElement hyperStarter = hyperMeta.getFirstChild();
                            processHyperPart(editor, hyperStarter.getTextOffset() + 2, text, hyperStarter.getTextOffset(), hyperStarter.getText());
                            String originalHyperText = hyperMeta.getText();
                            if (originalHyperText.endsWith(String.valueOf(c))) {
                                originalHyperText += c;
                            } else {
                                originalHyperText = originalHyperText + c + c;
                            }
                            editor.putUserData(UNICODE_REPLACEMENT_POS, Pair.create(Pair.create(offset - m.ascii.length() - 1, hyperMeta.getTextOffset()), originalHyperText));
                            return Result.STOP;
                        }
                        return Result.STOP;
                    }
                }
            }
        }
        editor.putUserData(UNICODE_REPLACEMENT_POS, null);
        return Result.CONTINUE;
    }

    protected void processHyperPart(@NotNull Editor editor, int offset, String text, int start, String op) {
        if (op.equals(">>"))
            editor.getDocument().setText(text.substring(0, start) + "»" + text.substring(offset));
        else if (op.equals("<<"))
            editor.getDocument().setText(text.substring(0, start) + "«" + text.substring(offset));
    }
}
