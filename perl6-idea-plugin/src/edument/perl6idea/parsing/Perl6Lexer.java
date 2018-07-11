package edument.perl6idea.parsing;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Perl6Lexer extends LexerBase {
    private CursorStack stack;
    private List<CursorStack> resumePoints;
    private int lastTokenStart;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        if (initialState == 0) {
            resumePoints = new ArrayList<>();
            stack = new CursorStack(buffer);
            stack.push(new MAINBraid().initialize(stack));
            lastTokenStart = 0;
            advance();
        } else {
            int resumeIndex = initialState - 1;
            stack = resumePoints.get(resumeIndex).resume(buffer);
            while (resumePoints.size() >= resumeIndex)
                resumePoints.remove(resumePoints.size() - 1);
            lastTokenStart = stack.tokenStart;
            advance();
        }
    }

    @Override
    public int getState() {
        return resumePoints.size();
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return stack.token;
    }

    @Override
    public int getTokenStart() {
        return stack.tokenStart;
    }

    @Override
    public int getTokenEnd() {
        return stack.peek().pos;
    }

    @Override
    public void advance() {
        while (true) {
            stack.ensureTopNotFrozen();
            int outcome = stack.peek().runRule();
            switch (outcome) {
                case -1: {
                    Cursor<MAINBraid> c = stack.pop();
                    c.passed = true;
                    if (stack.isEmpty()) {
                        if (c.pos != stack.target.length())
                            System.err.println("Perl 6 lexer: failed to lex the whole file (passed early)");
                        stack.token = null;
                        return;
                    }
                    continue;
                }
                case -2:
                    stack.pop();
                    if (stack.isEmpty()) {
                        System.err.println("Perl 6 lexer: failed to lex the whole file (failed to match)");
                        stack.token = null;
                        return;
                    }
                    continue;
                case -3:
                    if (stack.tokenStart < lastTokenStart)
                        System.err.println("Perl 6 lexer went backwards (from " + lastTokenStart +
                            " to " + stack.tokenStart + ") over text '" +
                            stack.target.subSequence(stack.tokenStart, lastTokenStart) + "'");
                    else
                        lastTokenStart = stack.tokenStart;
                    resumePoints.add(stack.snapshot());
                    return;
                default:
                    stack.push(stack.peek().start(outcome));
            }
        }
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return stack.target;
    }

    @Override
    public int getBufferEnd() {
        return stack.target.length(); // XXX
    }
}
