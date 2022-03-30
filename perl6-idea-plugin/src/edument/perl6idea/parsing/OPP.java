package edument.perl6idea.parsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

/* Implementation of operator precedence parsing. A given instance of this tracks the
 * state needed to do the precedence parse. */
public class OPP {
    private static class Op {
        public String prec;
        public String subPrec;
        public String assoc;
        public boolean fake;
        public PsiBuilder.Marker startMarker; // For prefixes
        public PsiBuilder.Marker endMarker;   // For postfixes
        public int startOffset = -1;
        public int endOffset = -1;
    }

    private static class Term {
        public PsiBuilder.Marker startMarker;
        public PsiBuilder.Marker endMarker;
    }

    private final PsiBuilder builder;

    private boolean regexMode;
    private PsiBuilder.Marker curPrefixStartMarker;
    private int curInfixStartPosition;
    private PsiBuilder.Marker termStartMarker;
    private PsiBuilder.Marker termEndMarker;
    private final List<Op> prefixes = new ArrayList<>();
    private final List<Op> postfixes = new ArrayList<>();
    private final List<Op> opStack = new ArrayList<>();
    private final List<Term> termStack = new ArrayList<>();
    private String opPrec = "";

    public OPP(PsiBuilder builder) {
        this.builder = builder;
        this.regexMode = false;
    }

    public void regexMode() {
        regexMode = true;
    }

    public void startExpr() {
        // Nothing to do
    }

    public void startPrefixes() {
        curPrefixStartMarker = builder.mark();
    }

    public void pushPrefix() {
        PrecInfoToken precInfo = getPrecInfoToken();
        Op prefix = new Op();
        prefix.prec = precInfo.prec();
        prefix.subPrec = precInfo.subPrec();
        prefix.assoc = precInfo.assoc();
        prefix.startMarker = curPrefixStartMarker;
        prefixes.add(prefix);
        curPrefixStartMarker = builder.mark();
    }

    public void endPrefixes() {
        curPrefixStartMarker.drop(); // There were no more prefixes
        termStartMarker = builder.mark();
    }

    public void startPostfixes() {
        termEndMarker = builder.mark();
    }

    public void pushPostfix() {
        PrecInfoToken precInfo = getPrecInfoToken();
        Op postfix = new Op();
        postfix.prec = precInfo.prec();
        postfix.subPrec = precInfo.subPrec();
        postfix.assoc = precInfo.assoc();
        postfix.endMarker = builder.mark();
        postfixes.add(postfix);
    }

    public void endPostfixes() {
        // Interleave prefix and postfix operators onto the op stack.
        while (prefixes.size() > 0 && postfixes.size() > 0) {
            Op preO = prefixes.get(0);
            Op postO = postfixes.get(postfixes.size() - 1);
            String prePrec = preO.subPrec.length() > 0 ? preO.subPrec : preO.prec;
            String postPrec = postO.subPrec.length() > 0 ? postO.subPrec : postO.prec;

            if (gt(postPrec, prePrec)) {
                opStack.add(prefixes.remove(0));
            }
            else if (lt(postPrec, prePrec)) {
                opStack.add(postfixes.remove(postfixes.size() - 1));
            }
            else if (postO.assoc.contentEquals("right")) {
                opStack.add(prefixes.remove(0));
            }
            else if (postO.assoc.contentEquals("left")) {
                opStack.add(postfixes.remove(postfixes.size() - 1));
            }
            else {
                // Non-assoc error; pretend left, parse for now.
                opStack.add(postfixes.remove(postfixes.size() - 1));
            }
        }
        while (prefixes.size() > 0)
            opStack.add(prefixes.remove(0));
        while (postfixes.size() > 0)
            opStack.add(postfixes.remove(postfixes.size() - 1));

        // Push term onto the term stack.
        Term term = new Term();
        term.startMarker = termStartMarker;
        term.endMarker = termEndMarker;
        termStack.add(term);

        // Clear up prefix start marker.
        curPrefixStartMarker = null;
    }

    public void startInfix() {
        curInfixStartPosition = builder.getCurrentOffset();
    }

    public void endInfix() {
        PrecInfoToken precInfo = getPrecInfoToken();
        String inPrec = precInfo.prec();

        // Reduce anything tighter than the infix.
        while (opStack.size() > 0) {
            Op opO = opStack.get(opStack.size() - 1);
            opPrec = opO.subPrec.length() > 0 ? opO.subPrec : opO.prec;
            if (!gt(opPrec, inPrec))
                break;
            reduce();
        }

        // If it's marked fake, that means it's an operator adverb. These we will
        // reduce immediately.
        if (precInfo.fake()) {
            opStack.add(createInfixOp(precInfo));
            reduce();
        }

        // Otherwise, it's a real infix.
        else {
            // If equal precedence...
            if (opPrec.contentEquals(inPrec)) {
                // Reduce immediately if left associative.
                if (precInfo.assoc().contentEquals("left"))
                    reduce();
            }

            // Push infix onto opstack.
            opStack.add(createInfixOp(precInfo));
        }
    }

    private Op createInfixOp(PrecInfoToken precInfo) {
        Op op = new Op();
        op.prec = precInfo.prec();
        op.subPrec = precInfo.subPrec();
        op.assoc = precInfo.assoc();
        op.fake = precInfo.fake();
        op.startOffset = curInfixStartPosition;
        op.endOffset = builder.getCurrentOffset();
        return op;
    }

    public void endExpr() {
        // Reduce everything on the stack.
        while (opStack.size() > 0)
            reduce();

        // Should be left with only a single term that has a start and end marker.
        // These were only for position, so drop them, meaning we should have either
        // used or dropped all the markers.
        Term term = termStack.get(0);
        term.startMarker.drop();
        term.endMarker.drop();
    }

    private void reduce() {
        Op op = opStack.remove(opStack.size() - 1);
        if (op.fake) {
            // Fake infix, which means an operator adverb. We build an adverb application
            // node here, which starts at the start marker of the term and ends just before
            // a marker M taken at the current location. The resultant composite term has
            // a start preceding the current term's start marker and the marker M as its end.
            Term term = termStack.remove(termStack.size() - 1);
            PsiBuilder.Marker adverbMarker = term.startMarker;
            term.endMarker.drop();
            PsiBuilder.Marker here = builder.mark();
            adverbMarker.doneBefore(Perl6OPPElementTypes.ADVERB_APPLICATION, here);
            Term composite = new Term();
            composite.startMarker = adverbMarker.precede();
            composite.endMarker = here;
            termStack.add(composite);
        }
        else if (op.startMarker != null) {
            // Prefix operator. We drop the term's start marker, since we will no longer
            // need to refer to that point. The prefix's own start marker is taken and
            // used, terminated before the term end marker. The resulting composite term
            // has a new start marker that precedes the prefix marker, and carries the
            // term end marker.
            Term term = termStack.remove(termStack.size() - 1);
            term.startMarker.drop();
            PsiBuilder.Marker prefixMarker = op.startMarker;
            prefixMarker.doneBefore(Perl6OPPElementTypes.PREFIX_APPLICATION, term.endMarker);
            Term composite = new Term();
            composite.startMarker = prefixMarker.precede();
            composite.endMarker = term.endMarker;
            termStack.add(composite);
        }
        else if (op.endMarker != null) {
            // Postfix operator. We drop the term's end marker, since we will no longer
            // need to refer to that point. The term's start marker is taken and used,
            // and terminated before the postfix end marker. The resulting composite term
            // has a new start marker that precedes the term's start marker, and carries
            // the postfix end marker.
            Term term = termStack.remove(termStack.size() - 1);
            term.endMarker.drop();
            PsiBuilder.Marker postfixMarker = term.startMarker;
            postfixMarker.doneBefore(Perl6OPPElementTypes.POSTFIX_APPLICATION, op.endMarker);
            Term composite = new Term();
            composite.startMarker = postfixMarker.precede();
            composite.endMarker = op.endMarker;
            termStack.add(composite);
        }
        else if (op.assoc.contentEquals("list")) {
            // List-associative infix operator. For these, we want to reduce all of the
            // terms sharing the operator into a single node. We work down the op stack.
            // Any terms that we determine to be completely inner to the expression have
            // their start and end markers dropped. Ditto for the end marker of the
            // leftmost and start marker of the rightmost term. Make the leftmost term's
            // start marker be done before the rightmost term's end marker, thus
            // building a single tree node for all in the list. The composite term has a
            // new start marker that precedes the leftmost term's start marker, and carries
            // the rightmost term's end marker.
            String sym = sym(op);
            Term right = termStack.remove(termStack.size() - 1);
            while (opStack.size() > 0) {
                if (!sym.contentEquals(sym(opStack.get(opStack.size() - 1))))
                    break;
                Term inner = termStack.remove(termStack.size() - 1);
                inner.startMarker.drop();
                inner.endMarker.drop();
                opStack.remove(opStack.size() - 1);
            }
            Term left = termStack.remove(termStack.size() - 1);
            right.startMarker.drop();
            left.endMarker.drop();
            PsiBuilder.Marker infixMarker = left.startMarker;
            infixMarker.doneBefore(regexMode ? Perl6OPPElementTypes.REGEX_INFIX_APPLICATION : Perl6OPPElementTypes.INFIX_APPLICATION, right.endMarker);
            Term composite = new Term();
            composite.startMarker = infixMarker.precede();
            composite.endMarker = right.endMarker;
            termStack.add(composite);
        }
        else {
            // Infix operator. The end marker of the left term and the start marker of the
            // right marker are inner and so dropped. The term's start marker is taken and
            // used, and terminated before the right term's end marker. The composite term
            // has a new start marker that precedes the left term's start marker, and carries
            // the second term's end marker.
            Term right = termStack.remove(termStack.size() - 1);
            Term left = termStack.remove(termStack.size() - 1);
            left.endMarker.drop();
            right.startMarker.drop();
            PsiBuilder.Marker infixMarker = left.startMarker;
            infixMarker.doneBefore(regexMode ? Perl6OPPElementTypes.REGEX_INFIX_APPLICATION : Perl6OPPElementTypes.INFIX_APPLICATION, right.endMarker);
            Term composite = new Term();
            composite.startMarker = infixMarker.precede();
            composite.endMarker = right.endMarker;
            termStack.add(composite);
        }
    }

    private PrecInfoToken getPrecInfoToken() {
        IElementType token = builder.getTokenType();
        if (token instanceof PrecInfoToken) {
            builder.advanceLexer();
            return (PrecInfoToken)token;
        }
        else {
            throw new RuntimeException("Missing precedence info token (got " + (token == null ? "null" : token.toString()) + ")");
        }
    }

    private static boolean lt(String a, String b) {
        return a.compareTo(b) < 0;
    }

    private static boolean gt(String a, String b) {
        return a.compareTo(b) > 0;
    }

    private String sym(Op op) {
        return op.startOffset < 0 || op.endOffset < 0
            ? ""
            : builder.getOriginalText().subSequence(op.startOffset, op.endOffset).toString();
    }
}
