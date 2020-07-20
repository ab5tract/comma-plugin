package edument.perl6idea.parsing;

import com.intellij.psi.tree.IElementType;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;

import java.util.*;

/**
 * This works much like the Cursor class in Raku: it is the base class for grammars, and contains the state of an
 * ongoing rule parse along with having a bunch of methods used to implement parsing.
 */
public abstract class Cursor<TCursor extends Cursor> {
    /* The cursor stack we're part of. */
    public CursorStack stack;

    /* The number of the rule the cursor is currently in. */
    public int ruleNumber;

    /* The current state of the rule (this is updated by rule parsers). */
    public int state;

    /* The current position in that string. */
    public int pos;

    /* The result of the last subrule call this Cursor made. */
    public Cursor lastResult;

    /* Flag that is set when the rule passes. */
    public boolean passed;

    /* The backtrack points stack, used to decide where to go upon a fail. Created lazily on
    * first need for it. Contains groups of 4 integers, which are:
     * 0. The location in the rule to backtrack to
     * 1. The position to reset pos to
     * 2. A repetition count, or zero (for quantifiers)
     * 3. The height of the capture stack at the point the mark was made
     */
    public List<Integer> backtrackStack;

    /* Dynamic variables declared by this Cursor, if any. */
    public Map<String, Object> dynamicVariables;

    /* If the cusror has been frozen (and so we need a clone if we'll mutate it). */
    private boolean frozen;

    public TCursor initialize(CursorStack stack) {
        TCursor cursor = null;
        try {
            cursor = createInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        cursor.stack = stack;
        this.ruleNumber = 0;
        cursor.state = 0;
        cursor.pos = 0;
        return cursor;
    }

    public TCursor start(int ruleNumber) {
        TCursor cursor = null;
        try {
            cursor = createInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        cursor.stack = stack;
        cursor.ruleNumber = ruleNumber;
        cursor.state = 0;
        cursor.pos = this.pos;
        return cursor;
    }

    public void freeze() {
        this.frozen = true;
    }

    public Cursor<TCursor> copyForStack(CursorStack stack) {
        if (!frozen && this.stack == stack)
            return this;
        TCursor copy = null;
        try {
            copy = createInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        copy.stack = stack;
        copy.ruleNumber = this.ruleNumber;
        copy.state = this.state;
        copy.pos = this.pos;
        copy.lastResult = this.lastResult;
        copy.passed = this.passed;
        if (this.backtrackStack != null)
            copy.backtrackStack = new ArrayList<>(this.backtrackStack);
        if (this.dynamicVariables != null)
            copy.dynamicVariables = new HashMap<>(this.dynamicVariables);
        return copy;
    }

    public boolean isFailed() {
        return !passed;
    }

    public int getPos() {
        return pos;
    }

    public void bsMark(int state) {
        pushBsMark(state, pos, 0);
    }

    public void bsMark(int state, int rep) {
        pushBsMark(state, pos, rep);
    }

    public void bsFailMark(int state) {
        pushBsMark(state, -1, 0);
    }

    private void pushBsMark(int state, int pos, int rep) {
        if (backtrackStack == null)
            backtrackStack = new ArrayList<>();
        backtrackStack.add(state);
        backtrackStack.add(pos);
        backtrackStack.add(rep);
        backtrackStack.add(0);
    }

    public int peekRep(int state) {
        int cursor = backtrackStack.size() - 4;
        while (true) {
            if (backtrackStack.get(cursor) == state)
                return backtrackStack.get(cursor + 2);
            cursor -= 4;
        }
    }

    public void bsCommit(int state) {
        while (true) {
            int removeState = backtrackStack.get(backtrackStack.size() - 4);
            for (int i = 0; i < 4; i++)
                backtrackStack.remove(backtrackStack.size() - 1);
            if (state == removeState)
                return;
        }
    }

    public boolean backtrack() {
        if (backtrackStack == null)
            return false;
        while (backtrackStack.size() > 0) {
            int toPos = backtrackStack.get(backtrackStack.size() - 3);
            int toState = backtrackStack.get(backtrackStack.size() - 4);
            if (toPos < 0) {
                /* Just remove this mark and continue backtracking. */
                for (int i = 0; i < 4; i++)
                    backtrackStack.remove(backtrackStack.size() - 1);
            }
            else {
                /* Restore state and position, remove mark, and we're done. */
                state = toState;
                pos = toPos;
                for (int i = 0; i < 4; i++)
                    backtrackStack.remove(backtrackStack.size() - 1);
                return true;
            }
        }
        return false;
    }

    public void startToken(IElementType token) {
        stack.token = token;
        stack.tokenStart = pos;
    }

    public boolean literal(String value) {
        int end = pos + value.length();
        if (end <= stack.target.length()) {
            if (value.contentEquals(stack.target.subSequence(pos, end))) {
                pos += value.length();
                return true;
            }
        }
        return false;
    }

    public boolean inCharList(String chars) {
        if (pos < stack.target.length()) {
            if (chars.contains(stack.target.subSequence(pos, pos + 1))) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean notInCharList(String chars) {
        if (pos < stack.target.length()) {
            CharSequence checkChar = stack.target.subSequence(pos, pos + 1);
            if (!chars.contains(checkChar)) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean anyChar() {
        if (pos < stack.target.length()) {
            pos++;
            return true;
        }
        return false;
    }

    public boolean wordChar() {
        if (pos < stack.target.length()) {
            char test = stack.target.charAt(pos);
            if (Character.isLetterOrDigit(test) || test == '_') {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean notWordChar() {
        if (pos < stack.target.length()) {
            char test = stack.target.charAt(pos);
            if (!(Character.isLetterOrDigit(test) || test == '_')) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean digitChar() {
        if (pos < stack.target.length()) {
            if (Character.isDigit(stack.target.charAt(pos))) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean notDigitChar() {
        if (pos < stack.target.length()) {
            if (!Character.isDigit(stack.target.charAt(pos))) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean spaceChar() {
        if (pos < stack.target.length()) {
            if (Character.isWhitespace(stack.target.charAt(pos))) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean notSpaceChar() {
        if (pos < stack.target.length()) {
            if (!Character.isWhitespace(stack.target.charAt(pos))) {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean newlineChar() {
        if (pos < stack.target.length()) {
            if (stack.target.charAt(pos) == '\n') {
                pos++;
                return true;
            }
            else if (stack.target.charAt(pos) == '\r') {
                pos++;
                if (pos < stack.target.length() && stack.target.charAt(pos) == '\n')
                    pos++;
                return true;
            }
        }
        return false;
    }

    public boolean notNewlineChar() {
        if (pos < stack.target.length()) {
            char c = stack.target.charAt(pos);
            if (c != '\r' && c != '\n') {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean alphaChar() {
        if (pos < stack.target.length()) {
            char test = stack.target.charAt(pos);
            if (Character.isLetter(test) || test == '_') {
                pos++;
                return true;
            }
        }
        return false;
    }

    public boolean ww() {
        return pos > 0 && pos != stack.target.length() &&
                Character.isLetterOrDigit(stack.target.charAt(pos)) &&
                Character.isLetterOrDigit(stack.target.charAt(pos - 1));
    }

    public boolean rightWordBoundary() {
        // Always a boundary at the end; never a boundary at the start.
        if (pos == stack.target.length())
            return true;
        if (pos == 0)
            return false;

        // Otherwise, char before current position should be a word char
        // and char afterwards should not be.
        return Character.isLetterOrDigit(stack.target.charAt(pos - 1)) &&
                !Character.isLetterOrDigit(stack.target.charAt(pos));
    }

    public boolean endOfLine() {
        // True if we match a newline here
        int origPos = pos;
        boolean isNewline = newlineChar();
        pos = origPos;
        if (isNewline)
            return true;

        // Otherwise, certainly false if we're not at the end of the string.
        if (pos != stack.target.length())
            return false;

        // True if we're also at the start of the string.
        if (pos == 0)
            return true;

        // Otherwise, false if we're just after a newline.
        pos--;
        isNewline = newlineChar();
        this.pos = origPos;
        return !isNewline;
    }

    public boolean endOfString() {
        return pos == stack.target.length();
    }

    public boolean startOfLine() {
        if (pos == 0)
            return true;
        if (pos >= stack.target.length())
            return false;

        int origPos = pos;
        pos--;
        Boolean isNewline = newlineChar();
        this.pos = origPos;
        return isNewline;
    }

    public boolean lookahead(int ruleNumber) {
        // Save original token and token start; note we're in lookahead.
        int origTokenStart = stack.tokenStart;
        IElementType origToken = stack.token;
        List<Heredoc> origHeredocs = stack.heredocs.isEmpty() ? null : new ArrayList<>(stack.heredocs);
        int origHeredocDelimStart = stack.heredocDelimStart;
        stack.inLookahead++;

        // Run ignoring any tokens, until we leave the base.
        Cursor<TCursor> base = stack.peek().start(ruleNumber);
        stack.push(base);
        boolean poppedBase = false;
        boolean result = false;
        while (!poppedBase) {
            int outcome = stack.peek().runRule();
            switch (outcome) {
                case -1: {
                    Cursor<TCursor> popped = stack.pop();
                    popped.passed = true;
                    if (popped == base) {
                        result = true;
                        poppedBase = true;
                    }
                    continue;
                }
                case -2: {
                    Cursor<TCursor> popped = stack.pop();
                    if (popped == base)
                        poppedBase = true;
                }
                case -3:
                    continue;
                default:
                    stack.push(stack.peek().start(outcome));
            }
        }

        // Restore original token and token start; note we left lookahead.
        stack.token = origToken;
        stack.tokenStart = origTokenStart;
        if (origHeredocs != null)
            stack.heredocs = origHeredocs;
        stack.heredocDelimStart = origHeredocDelimStart;
        stack.inLookahead--;

        // Return lookahead result.
        return result;
    }

    public void setArgs(Object ...args) {
        stack.args = args;
    }

    public void checkArgs(int wanted) {
        int got = stack.args == null ? 0 : stack.args.length;
        if (got != wanted)
            throw new RuntimeException("Wrong number of arguments; got " + got + ", but wanted " + wanted);
    }

    public Object getArg(int idx) {
        return stack.args[idx];
    }

    public boolean interpolate(String variableName) {
        Object found = stack.findDynamicVariable(variableName);
        return found != null && literal(found.toString());
    }

    public Object getDynamicVariable(String name) {
        return dynamicVariables == null ? null : dynamicVariables.get(name);
    }

    public void setDynamicVariable(String name, Object value) {
        if (dynamicVariables == null)
            dynamicVariables = new HashMap<>();
        dynamicVariables.put(name, value);
    }

    public void declareDynamicVariable(String name, Object value) {
        setDynamicVariable(name, value);
    }

    public void assignDynamicVariable(String name, Object value) {
        stack.assignDynamicVariable(name, value);
    }

    public Object findDynamicVariable(String name) {
        return stack.findDynamicVariable(name);
    }

    public Integer testStrEQ(Object left, Object right) {
        return left.toString().compareTo(right.toString()) == 0 ? 1 : 0;
    }

    public Integer testStrNE(Object left, Object right) {
        return left.toString().compareTo(right.toString()) != 0 ? 1 : 0;
    }

    public Integer testStrLE(Object left, Object right) {
        return left.toString().compareTo(right.toString()) <= 0 ? 1 : 0;
    }

    public boolean isValueTruthy(Object value) {
        if (value instanceof String)
            return !((String)value).isEmpty();
        if (value instanceof Integer)
            return (Integer)value != 0;
        return false;
    }

    public void marker(String name) {
        stack.setMarker(name, pos);
    }

    public boolean marked(String name) {
        return stack.getMarker(name) == pos;
    }

    private static final String BRACKETS = "<>[](){}\u0028\u0029\u003C\u003E\u005B\u005D" +
            "\u007B\u007D\u00AB\u00BB\u0F3A\u0F3B\u0F3C\u0F3D\u169B\u169C" +
            "\u2018\u2019\u201A\u2019\u201B\u2019\u201C\u201D\u201E\u201D" +
            "\u201F\u201D\u2039\u203A\u2045\u2046\u207D\u207E\u208D\u208E" +
            "\u2208\u220B\u2209\u220C\u220A\u220D\u2215\u29F5\u223C\u223D" +
            "\u2243\u22CD\u2252\u2253\u2254\u2255\u2264\u2265\u2266\u2267" +
            "\u2268\u2269\u226A\u226B\u226E\u226F\u2270\u2271\u2272\u2273" +
            "\u2274\u2275\u2276\u2277\u2278\u2279\u227A\u227B\u227C\u227D" +
            "\u227E\u227F\u2280\u2281\u2282\u2283\u2284\u2285\u2286\u2287" +
            "\u2288\u2289\u228A\u228B\u228F\u2290\u2291\u2292\u2298\u29B8" +
            "\u22A2\u22A3\u22A6\u2ADE\u22A8\u2AE4\u22A9\u2AE3\u22AB\u2AE5" +
            "\u22B0\u22B1\u22B2\u22B3\u22B4\u22B5\u22B6\u22B7\u22C9\u22CA" +
            "\u22CB\u22CC\u22D0\u22D1\u22D6\u22D7\u22D8\u22D9\u22DA\u22DB" +
            "\u22DC\u22DD\u22DE\u22DF\u22E0\u22E1\u22E2\u22E3\u22E4\u22E5" +
            "\u22E6\u22E7\u22E8\u22E9\u22EA\u22EB\u22EC\u22ED\u22F0\u22F1" +
            "\u22F2\u22FA\u22F3\u22FB\u22F4\u22FC\u22F6\u22FD\u22F7\u22FE" +
            "\u2308\u2309\u230A\u230B\u2329\u232A\u23B4\u23B5\u2768\u2769" +
            "\u276A\u276B\u276C\u276D\u276E\u276F\u2770\u2771\u2772\u2773" +
            "\u2774\u2775\u27C3\u27C4\u27C5\u27C6\u27D5\u27D6\u27DD\u27DE" +
            "\u27E2\u27E3\u27E4\u27E5\u27E6\u27E7\u27E8\u27E9\u27EA\u27EB" +
            "\u2983\u2984\u2985\u2986\u2987\u2988\u2989\u298A\u298B\u298C" +
            "\u298D\u2990\u298F\u298E\u2991\u2992\u2993\u2994\u2995\u2996" +
            "\u2997\u2998\u29C0\u29C1\u29C4\u29C5\u29CF\u29D0\u29D1\u29D2" +
            "\u29D4\u29D5\u29D8\u29D9\u29DA\u29DB\u29F8\u29F9\u29FC\u29FD" +
            "\u2A2B\u2A2C\u2A2D\u2A2E\u2A34\u2A35\u2A3C\u2A3D\u2A64\u2A65" +
            "\u2A79\u2A7A\u2A7D\u2A7E\u2A7F\u2A80\u2A81\u2A82\u2A83\u2A84" +
            "\u2A8B\u2A8C\u2A91\u2A92\u2A93\u2A94\u2A95\u2A96\u2A97\u2A98" +
            "\u2A99\u2A9A\u2A9B\u2A9C\u2AA1\u2AA2\u2AA6\u2AA7\u2AA8\u2AA9" +
            "\u2AAA\u2AAB\u2AAC\u2AAD\u2AAF\u2AB0\u2AB3\u2AB4\u2ABB\u2ABC" +
            "\u2ABD\u2ABE\u2ABF\u2AC0\u2AC1\u2AC2\u2AC3\u2AC4\u2AC5\u2AC6" +
            "\u2ACD\u2ACE\u2ACF\u2AD0\u2AD1\u2AD2\u2AD3\u2AD4\u2AD5\u2AD6" +
            "\u2AEC\u2AED\u2AF7\u2AF8\u2AF9\u2AFA\u2E02\u2E03\u2E04\u2E05" +
            "\u2E09\u2E0A\u2E0C\u2E0D\u2E1C\u2E1D\u2E20\u2E21\u2E28\u2E29" +
            "\u3008\u3009\u300A\u300B\u300C\u300D\u300E\u300F\u3010\u3011" +
            "\u3014\u3015\u3016\u3017\u3018\u3019\u301A\u301B\u301D\u301E" +
            "\uFE17\uFE18\uFE35\uFE36\uFE37\uFE38\uFE39\uFE3A\uFE3B\uFE3C" +
            "\uFE3D\uFE3E\uFE3F\uFE40\uFE41\uFE42\uFE43\uFE44\uFE47\uFE48" +
            "\uFE59\uFE5A\uFE5B\uFE5C\uFE5D\uFE5E\uFF08\uFF09\uFF1C\uFF1E" +
            "\uFF3B\uFF3D\uFF5B\uFF5D\uFF5F\uFF60\uFF62\uFF63\u27EE\u27EF" +
            "\u2E24\u2E25\u27EC\u27ED\u2E22\u2E23\u2E26\u2E27";

    public boolean peekDelimiters() {
        // No delimiter at end of string.
        CharSequence target = stack.target;
        int pos = this.pos;
        if (pos >= target.length())
            return false;

        // Delimiter may not be colon, word or whitespace.
        String start = target.subSequence(pos, pos + 1).toString();
        if (start.contentEquals(":"))
            return false;
        if (start.contentEquals("_"))
            return false;
        if (Character.isLetterOrDigit(start.codePointAt(0)))
            return false;
        if (Character.isWhitespace(start.codePointAt(0)))
            return false;

        // Starter is stopper unless it's a bracket char.
        String stop = start;
        int brac = BRACKETS.indexOf(start);
        if (brac >= 0) {
            // Ensure not a closing bracket.
            if (brac % 2 != 0)
                return false;

            // Stopper is the closing bracket.
            stop = BRACKETS.substring(brac + 1, brac + 2);

            // See if there are any repetitions.
            int len = 1;
            while (++pos < target.length() && start.contentEquals(target.subSequence(pos, pos + 1)))
                len++;
            if (len > 1) {
                start = repeat(start, len);
                stop = repeat(stop, len);
            }
        }

        assignDynamicVariable("$*STARTER", start);
        assignDynamicVariable("$*STOPPER", stop);
        assignDynamicVariable("$*ALT_STOPPER", stop);
        return true;
    }

    public boolean opener() {
        // No opener at end of string.
        CharSequence target = stack.target;
        int pos = this.pos;
        if (pos >= target.length())
            return false;

        // Check if it's in the bracket pairs list.
        String maybeOpener = target.subSequence(pos, pos + 1).toString();
        int brac = BRACKETS.indexOf(maybeOpener);
        return brac >= 0 && brac % 2 == 0;
    }

    private String repeat(String in, int n) {
        String out = "";
        for (int i = 0; i < n; i++)
            out = out.concat(in);
        return out;
    }

    public boolean bracketEnding() {
        if (pos == 0)
            return false;
        String prev = stack.target.subSequence(pos - 1, pos).toString();
        return prev.contentEquals(")") || prev.contentEquals("}") ||
                prev.contentEquals("]") || prev.contentEquals(">");
    }

    private static final String[] saveLang = new String[] {
            "$*Q_Q", "$*Q_QQ", "$*Q_BACKSLASH", "$*Q_QBACKSLASH",
            "$*Q_QQBACKSLASH", "$*Q_CLOSURES", "$*Q_SCALARS",
            "$*Q_ARRAYS", "$*Q_HASHES", "$*Q_FUNCTIONS"
    };

    public void startQueueHeredoc() {
        Heredoc h = new Heredoc();
        for (String save : saveLang) {
            h.language.put(save, findDynamicVariable(save));
            assignDynamicVariable(save, 0);
        }
        stack.heredocs.add(h);
        stack.heredocDelimStart = pos;
    }

    public void endQueueHeredoc() {
        if (stack.heredocDelimStart < 0)
            throw new RuntimeException("End queue heredoc without matching start");
        String delim = stack.target.subSequence(stack.heredocDelimStart, pos).toString();
        stack.heredocs.get(stack.heredocs.size() - 1).delim = delim;
    }

    public boolean dequeueHeredoc() {
        if (stack.heredocs.isEmpty())
            return false;
        Heredoc h = stack.heredocs.remove(0);
        for (String restore : saveLang)
            assignDynamicVariable(restore, h.language.get(restore));
        assignDynamicVariable("$*DELIM", h.delim);
        return true;
    }

    public boolean hasHeredoc() {
        return !stack.heredocs.isEmpty();
    }

    public void precInfoToken() {
        stack.tokenStart = pos;
        stack.token = PrecInfoToken.tokenFor(
                getDynamicVariable("$*PREC").toString(),
                getDynamicVariable("$*SUB_PREC").toString(),
                getDynamicVariable("$*ASSOC").toString(),
                isValueTruthy(getDynamicVariable("$*FAKE")));
    }

    public void scopePush() {
        stack.symbols.add(new HashMap<>());
    }

    public void scopePop() {
        stack.symbols.remove(stack.symbols.size() - 1);
    }

    public void startSymbol() {
        stack.symbolStart = pos;
    }

    public void endSymbol(String type) {
        String symbol = stack.target.subSequence(stack.symbolStart, pos).toString();
        Map<String, Perl6SymbolKind> st = stack.symbols.get(stack.symbols.size() - 1);
        if (type.equals("term") || type.equals("type"))
            st.put(symbol, Perl6SymbolKind.TypeOrConstant);
        else if (type.equals("routine"))
            st.put(symbol, Perl6SymbolKind.Routine);
    }

    private static final Set<String> knownRoutines = new HashSet<>(Arrays.asList(
        "EVAL", "EVALFILE"
    ));
    private static final Set<String> knownTypes = new HashSet<>(Arrays.asList(
        "int", "int8", "int16", "int32", "int64",
        "uint", "uint8", "uint16", "uint32", "uint64",
        "num", "num32", "num64",
        "num", "num32", "num64",
        "blob", "blob8", "blob16", "blob32", "blob64",
        "buf", "buf8", "buf16", "buf32", "buf64",
        "utf8", "utf16", "utf32",
        "str", "array"
    ));

    public boolean isName() {
        // First try and resolve type on the stack and see if it matches.
        String symbol = stack.target.subSequence(stack.symbolStart, pos).toString();
        List<Map<String, Perl6SymbolKind>> symbolTables = stack.symbols;
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            Map<String, Perl6SymbolKind> st = symbolTables.get(i);
            Perl6SymbolKind foundKind = st.get(symbol);
            if (foundKind != null)
                return foundKind == Perl6SymbolKind.TypeOrConstant;
        }

        // If that fails, then we go on heuristics based on what is in the setting.
        if (knownTypes.contains(symbol))
            return true;
        if (knownRoutines.contains(symbol))
            return false;
        return Character.isUpperCase(symbol.charAt(0));
    }

    public abstract int runRule();

    public abstract TCursor createInstance();
}
