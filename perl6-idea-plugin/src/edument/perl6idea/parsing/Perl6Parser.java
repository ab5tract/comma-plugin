package edument.perl6idea.parsing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;

public class Perl6Parser implements PsiParser {

    public ASTNode parse(IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker;
        rootMarker = builder.mark();
        this.TOP_4(builder);
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private boolean ENDSTMT_1_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ENDSTMT_1_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ENDSTMT_1_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ENDSTMT_1_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ENDSTMT_1_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean ENDSTMT_1(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.ENDSTMT_1_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean EXPR_2_alt_1(PsiBuilder builder, OPP opp) {
        opp.endPrefixes();
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean EXPR_2_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.prefixish_113(builder))) {
            return false;
        }
        opp.pushPrefix();
        return true;
    }

    private boolean EXPR_2_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean EXPR_2_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.EXPR_2_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.EXPR_2_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        opp.endPrefixes();
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.EXPR_2_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean EXPR_2_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.postfixish_110(builder))) {
            return false;
        }
        opp.pushPostfix();
        return true;
    }

    private boolean EXPR_2_alt_6(PsiBuilder builder, OPP opp) {
        opp.startPostfixes();
        return true;
    }

    private boolean EXPR_2_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.postfixish_110(builder))) {
            return false;
        }
        opp.pushPostfix();
        return true;
    }

    private boolean EXPR_2_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.nextterm_79(builder))) {
            return false;
        }
        opp.startPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker8;;
            quantMarker8 = builder.mark();
            if (this.EXPR_2_quant_7(builder, opp)) {
                quantMarker8.drop();
            } else {
                quantMarker8.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean EXPR_2_alt_9(PsiBuilder builder, OPP opp) {
        opp.endPrefixes();
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.EXPR_2_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.EXPR_2_alt_6(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean EXPR_2_quant_10(PsiBuilder builder, OPP opp) {
        if (!(this.prefixish_113(builder))) {
            return false;
        }
        opp.pushPrefix();
        return true;
    }

    private boolean EXPR_2_alt_11(PsiBuilder builder, OPP opp) {
        opp.startPostfixes();
        return true;
    }

    private boolean EXPR_2_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.postfixish_110(builder))) {
            return false;
        }
        opp.pushPostfix();
        return true;
    }

    private boolean EXPR_2_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.nextterm_79(builder))) {
            return false;
        }
        opp.startPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker14;;
            quantMarker14 = builder.mark();
            if (this.EXPR_2_quant_12(builder, opp)) {
                quantMarker14.drop();
            } else {
                quantMarker14.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean EXPR_2_alt_14(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.EXPR_2_quant_10(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker12;;
            quantMarker12 = builder.mark();
            if (this.EXPR_2_quant_10(builder, opp)) {
                quantMarker12.drop();
            } else {
                quantMarker12.rollbackTo();
                break;
            }
        }
        opp.endPrefixes();
        PsiBuilder.Marker altMarker15;
        altMarker15 = builder.mark();
        if (this.EXPR_2_alt_13(builder, opp)) {
            altMarker15.drop();
        } else {
            altMarker15.rollbackTo();
            PsiBuilder.Marker altMarker13;;
            altMarker13 = builder.mark();
            if (this.EXPR_2_alt_11(builder, opp)) {
                altMarker13.drop();
            } else {
                altMarker13.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean EXPR_2_alt_15(PsiBuilder builder, OPP opp) {
        opp.startPrefixes();
        PsiBuilder.Marker altMarker16;
        altMarker16 = builder.mark();
        if (this.EXPR_2_alt_14(builder, opp)) {
            altMarker16.drop();
        } else {
            altMarker16.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.EXPR_2_alt_9(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        opp.endPostfixes();
        return true;
    }

    private boolean EXPR_2_alt_16(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.FAKE_INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean EXPR_2_quant_17(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        opp.startInfix();
        if (!(this.infixish_57(builder))) {
            return false;
        }
        opp.endInfix();
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker18;
        altMarker18 = builder.mark();
        if (this.EXPR_2_alt_16(builder, opp)) {
            altMarker18.drop();
        } else {
            altMarker18.rollbackTo();
            PsiBuilder.Marker altMarker17;;
            altMarker17 = builder.mark();
            if (this.EXPR_2_alt_15(builder, opp)) {
                altMarker17.drop();
            } else {
                altMarker17.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean EXPR_2_alt_18(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean EXPR_2_alt_19(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_EXPR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean EXPR_2(PsiBuilder builder) {
        OPP opp;
        opp = new OPP(builder);
        opp.startExpr();
        opp.startPrefixes();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.EXPR_2_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.EXPR_2_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        opp.startPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.EXPR_2_quant_5(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        opp.endPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker19;;
            quantMarker19 = builder.mark();
            if (this.EXPR_2_quant_17(builder, opp)) {
                quantMarker19.drop();
            } else {
                quantMarker19.rollbackTo();
                break;
            }
        }
        opp.endExpr();
        PsiBuilder.Marker altMarker21;
        altMarker21 = builder.mark();
        if (this.EXPR_2_alt_19(builder, opp)) {
            altMarker21.drop();
        } else {
            altMarker21.rollbackTo();
            PsiBuilder.Marker altMarker20;;
            altMarker20 = builder.mark();
            if (this.EXPR_2_alt_18(builder, opp)) {
                altMarker20.drop();
            } else {
                altMarker20.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean SIGOK_3_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean SIGOK_3(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.SIGOK_3_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean TOP_4_quant_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BAD_CHARACTER) && (tt1.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        if (!(this.statementlist_212(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_4_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.bogus_end_18(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_4_alt_3(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.TOP_4_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.TOP_4_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean TOP_4_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean TOP_4(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.statementlist_212(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.TOP_4_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.TOP_4_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean apostrophe_5(PsiBuilder builder) {
        OPP opp;
        opp = null;
        return true;
    }

    private boolean arglist_6_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean arglist_6_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.arglist_6_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean arglist_6_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ARGLIST_EMPTY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean arglist_6(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.ARGLIST_START) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.arglist_6_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.arglist_6_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.ARGLIST_END) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean args_7_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NO_ARGS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean args_7_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean args_7_quant_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean args_7_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semiarglist_158(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.args_7_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean args_7_quant_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt4.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean args_7_alt_6(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt3.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semiarglist_158(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.args_7_quant_5(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean args_7(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.args_7_alt_6(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.args_7_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.args_7_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.args_7_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean assertion_8_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MISSING_ASSERTION) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.cclass_elem_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.assertion_8_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.assertion_8_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        marker2.done(Perl6ElementTypes.REGEX_CCLASS);
        return true;
    }

    private boolean assertion_8_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_ASSERTION_END) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.assertion_8_alt_6(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.assertion_8_alt_5(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker6;;
                altMarker6 = builder.mark();
                if (this.assertion_8_alt_4(builder, opp)) {
                    altMarker6.drop();
                } else {
                    altMarker6.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean assertion_8_alt_8(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt1.equals("~~"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.assertion_8_quant_7(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean assertion_8_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_quant_10(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_11(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt2.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxarglist_148(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.assertion_8_quant_10(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean assertion_8_alt_12(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INVOCANT_MARKER) && (tt4.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxarglist_148(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_quant_13(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker14;
        altMarker14 = builder.mark();
        if (this.assertion_8_alt_12(builder, opp)) {
            altMarker14.drop();
        } else {
            altMarker14.rollbackTo();
            PsiBuilder.Marker altMarker13;;
            altMarker13 = builder.mark();
            if (this.assertion_8_alt_11(builder, opp)) {
                altMarker13.drop();
            } else {
                altMarker13.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean assertion_8_alt_14(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker15;
        quantMarker15 = builder.mark();
        if (this.assertion_8_quant_13(builder, opp)) {
            quantMarker15.drop();
        } else {
            quantMarker15.rollbackTo();
        }
        return true;
    }

    private boolean assertion_8_alt_15(PsiBuilder builder, OPP opp) {
        if (!(this.rxcodeblock_149(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_16(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_17(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_18(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_19(PsiBuilder builder, OPP opp) {
        if (!(this.normspace_80(builder))) {
            return false;
        }
        if (!(this.regex_nibbler_142(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_quant_20(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt6.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_21(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt5.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxarglist_148(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker22;
        quantMarker22 = builder.mark();
        if (this.assertion_8_quant_20(builder, opp)) {
            quantMarker22.drop();
        } else {
            quantMarker22.rollbackTo();
        }
        return true;
    }

    private boolean assertion_8_alt_22(PsiBuilder builder, OPP opp) {
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INVOCANT_MARKER) && (tt7.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxarglist_148(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_23(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt8.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.assertion_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_24(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_ASSERTION_END) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_quant_25(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker26;
        altMarker26 = builder.mark();
        if (this.assertion_8_alt_24(builder, opp)) {
            altMarker26.drop();
        } else {
            altMarker26.rollbackTo();
            PsiBuilder.Marker altMarker25;;
            altMarker25 = builder.mark();
            if (this.assertion_8_alt_23(builder, opp)) {
                altMarker25.drop();
            } else {
                altMarker25.rollbackTo();
                PsiBuilder.Marker altMarker24;;
                altMarker24 = builder.mark();
                if (this.assertion_8_alt_22(builder, opp)) {
                    altMarker24.drop();
                } else {
                    altMarker24.rollbackTo();
                    PsiBuilder.Marker altMarker23;;
                    altMarker23 = builder.mark();
                    if (this.assertion_8_alt_21(builder, opp)) {
                        altMarker23.drop();
                    } else {
                        altMarker23.rollbackTo();
                        PsiBuilder.Marker altMarker21;;
                        altMarker21 = builder.mark();
                        if (this.assertion_8_alt_19(builder, opp)) {
                            altMarker21.drop();
                        } else {
                            altMarker21.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean assertion_8_alt_26(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.assertion_8_alt_18(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker19;;
            altMarker19 = builder.mark();
            if (this.assertion_8_alt_17(builder, opp)) {
                altMarker19.drop();
            } else {
                altMarker19.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker27;
        quantMarker27 = builder.mark();
        if (this.assertion_8_quant_25(builder, opp)) {
            quantMarker27.drop();
        } else {
            quantMarker27.rollbackTo();
        }
        return true;
    }

    private boolean assertion_8_alt_27(PsiBuilder builder, OPP opp) {
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_LOOKAROUND) && (tt9.equals("!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.assertion_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_28(PsiBuilder builder, OPP opp) {
        String tt10;
        tt10 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_LOOKAROUND) && (tt10.equals("?"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.assertion_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_29(PsiBuilder builder, OPP opp) {
        String tt11;
        tt11 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_OPERATOR) && (tt11.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.assertion_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_30(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8_alt_31(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean assertion_8(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker33;
        altMarker33 = builder.mark();
        if (this.assertion_8_alt_31(builder, opp)) {
            altMarker33.drop();
        } else {
            altMarker33.rollbackTo();
            PsiBuilder.Marker altMarker32;;
            altMarker32 = builder.mark();
            if (this.assertion_8_alt_30(builder, opp)) {
                altMarker32.drop();
            } else {
                altMarker32.rollbackTo();
                PsiBuilder.Marker altMarker31;;
                altMarker31 = builder.mark();
                if (this.assertion_8_alt_29(builder, opp)) {
                    altMarker31.drop();
                } else {
                    altMarker31.rollbackTo();
                    PsiBuilder.Marker altMarker30;;
                    altMarker30 = builder.mark();
                    if (this.assertion_8_alt_28(builder, opp)) {
                        altMarker30.drop();
                    } else {
                        altMarker30.rollbackTo();
                        PsiBuilder.Marker altMarker29;;
                        altMarker29 = builder.mark();
                        if (this.assertion_8_alt_27(builder, opp)) {
                            altMarker29.drop();
                        } else {
                            altMarker29.rollbackTo();
                            PsiBuilder.Marker altMarker28;;
                            altMarker28 = builder.mark();
                            if (this.assertion_8_alt_26(builder, opp)) {
                                altMarker28.drop();
                            } else {
                                altMarker28.rollbackTo();
                                PsiBuilder.Marker altMarker18;;
                                altMarker18 = builder.mark();
                                if (this.assertion_8_alt_16(builder, opp)) {
                                    altMarker18.drop();
                                } else {
                                    altMarker18.rollbackTo();
                                    PsiBuilder.Marker altMarker17;;
                                    altMarker17 = builder.mark();
                                    if (this.assertion_8_alt_15(builder, opp)) {
                                        altMarker17.drop();
                                    } else {
                                        altMarker17.rollbackTo();
                                        PsiBuilder.Marker altMarker16;;
                                        altMarker16 = builder.mark();
                                        if (this.assertion_8_alt_14(builder, opp)) {
                                            altMarker16.drop();
                                        } else {
                                            altMarker16.rollbackTo();
                                            PsiBuilder.Marker altMarker11;;
                                            altMarker11 = builder.mark();
                                            if (this.assertion_8_alt_9(builder, opp)) {
                                                altMarker11.drop();
                                            } else {
                                                altMarker11.rollbackTo();
                                                PsiBuilder.Marker altMarker10;;
                                                altMarker10 = builder.mark();
                                                if (this.assertion_8_alt_8(builder, opp)) {
                                                    altMarker10.drop();
                                                } else {
                                                    altMarker10.rollbackTo();
                                                    PsiBuilder.Marker altMarker5;;
                                                    altMarker5 = builder.mark();
                                                    if (this.assertion_8_alt_3(builder, opp)) {
                                                        altMarker5.drop();
                                                    } else {
                                                        altMarker5.rollbackTo();
                                                        PsiBuilder.Marker altMarker1;;
                                                        altMarker1 = builder.mark();
                                                        if (this.assertion_8_alt_1(builder, opp)) {
                                                            altMarker1.drop();
                                                        } else {
                                                            altMarker1.rollbackTo();
                                                            return false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean atom_9_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.metachar_68(builder))) {
            return false;
        }
        return true;
    }

    private boolean atom_9_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_CHAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker2.done(Perl6ElementTypes.REGEX_LITERAL);
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean atom_9(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.atom_9_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.atom_9_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean backmod_10_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean backmod_10_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean backmod_10_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean backmod_10_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean backmod_10(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.backmod_10_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.backmod_10_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.backmod_10_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.backmod_10_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean backslash_11_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_BACKSLASH_BAD) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean backslash_11_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_BUILTIN_CCLASS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean backslash_11(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.backslash_11_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.backslash_11_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.REGEX_BUILTIN_CCLASS);
        return true;
    }

    private boolean bare_complex_number_12(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.signed_number_167(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.COMPLEX_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signed_number_167(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.COMPLEX_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean bare_rat_number_13(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.signed_integer_166(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RAT_LITERAL) && (tt1.equals("/"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.integer_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean binint_14_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean binint_14_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.binint_14_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.binint_14_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean binint_14(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.binint_14_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.binint_14_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean block_15(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.BLOCK);
        return true;
    }

    private boolean blockoid_16_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.unv_246(builder))) {
            return false;
        }
        return true;
    }

    private boolean blockoid_16_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.blockoid_16_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean blockoid_16_quant_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean blockoid_16(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BLOCK_CURLY_BRACKET_OPEN) && (tt1.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.blockoid_16_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.statementlist_212(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.blockoid_16_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.BLOCKOID);
        return true;
    }

    private boolean blorst_17_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.MISSING_BLORST) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean blorst_17_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.statement_170(builder))) {
            return false;
        }
        return true;
    }

    private boolean blorst_17_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean blorst_17(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.blorst_17_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.blorst_17_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.blorst_17_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean bogus_end_18(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.BAD_CHARACTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean bogus_statement_19(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.BAD_CHARACTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean capterm_20_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.CAPTURE_INVALID) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean capterm_20_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.termish_234(builder))) {
            return false;
        }
        return true;
    }

    private boolean capterm_20_quant_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.CAPTURE_TERM) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean capterm_20_alt_4(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.CAPTURE_TERM) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semiarglist_158(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.capterm_20_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean capterm_20(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.CAPTURE_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.capterm_20_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.capterm_20_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.capterm_20_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        marker1.done(Perl6ElementTypes.CAPTURE);
        return true;
    }

    private boolean cclass_backslash_21(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_BUILTIN_CCLASS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.REGEX_BUILTIN_CCLASS);
        return true;
    }

    private boolean cclass_elem_22_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_SYNTAX) && (tt1.equals("-"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_SYNTAX) && (tt2.equals("+"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.cclass_elem_22_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.cclass_elem_22_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean cclass_elem_22_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.normspace_80(builder))) {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_BUILTIN_CCLASS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_9(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_CHAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.cclass_backslash_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_quant_11(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_12(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_quant_13(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_14(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_CHAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_15(PsiBuilder builder, OPP opp) {
        if (!(this.cclass_backslash_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_16(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker14;
        quantMarker14 = builder.mark();
        if (this.cclass_elem_22_quant_13(builder, opp)) {
            quantMarker14.drop();
        } else {
            quantMarker14.rollbackTo();
        }
        PsiBuilder.Marker altMarker16;
        altMarker16 = builder.mark();
        if (this.cclass_elem_22_alt_15(builder, opp)) {
            altMarker16.drop();
        } else {
            altMarker16.rollbackTo();
            PsiBuilder.Marker altMarker15;;
            altMarker15 = builder.mark();
            if (this.cclass_elem_22_alt_14(builder, opp)) {
                altMarker15.drop();
            } else {
                altMarker15.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean cclass_elem_22_quant_17(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.cclass_elem_22_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_SYNTAX) && (tt4.equals(".."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker17;
        altMarker17 = builder.mark();
        if (this.cclass_elem_22_alt_16(builder, opp)) {
            altMarker17.drop();
        } else {
            altMarker17.rollbackTo();
            PsiBuilder.Marker altMarker13;;
            altMarker13 = builder.mark();
            if (this.cclass_elem_22_alt_12(builder, opp)) {
                altMarker13.drop();
            } else {
                altMarker13.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean cclass_elem_22_quant_18(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_ATOM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.cclass_elem_22_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.cclass_elem_22_alt_10(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.cclass_elem_22_alt_9(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.cclass_elem_22_quant_17(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
        }
        return true;
    }

    private boolean cclass_elem_22_quant_19(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_20(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_21(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_SYNTAX) && (tt5.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22_alt_22(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CCLASS_SYNTAX) && (tt3.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker19;;
            quantMarker19 = builder.mark();
            if (this.cclass_elem_22_quant_18(builder, opp)) {
                quantMarker19.drop();
            } else {
                quantMarker19.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker20;
        quantMarker20 = builder.mark();
        if (this.cclass_elem_22_quant_19(builder, opp)) {
            quantMarker20.drop();
        } else {
            quantMarker20.rollbackTo();
        }
        PsiBuilder.Marker altMarker22;
        altMarker22 = builder.mark();
        if (this.cclass_elem_22_alt_21(builder, opp)) {
            altMarker22.drop();
        } else {
            altMarker22.rollbackTo();
            PsiBuilder.Marker altMarker21;;
            altMarker21 = builder.mark();
            if (this.cclass_elem_22_alt_20(builder, opp)) {
                altMarker21.drop();
            } else {
                altMarker21.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean cclass_elem_22_quant_23(PsiBuilder builder, OPP opp) {
        if (!(this.normspace_80(builder))) {
            return false;
        }
        return true;
    }

    private boolean cclass_elem_22(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.cclass_elem_22_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.cclass_elem_22_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker altMarker23;
        altMarker23 = builder.mark();
        if (this.cclass_elem_22_alt_22(builder, opp)) {
            altMarker23.drop();
        } else {
            altMarker23.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.cclass_elem_22_alt_7(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                PsiBuilder.Marker altMarker7;;
                altMarker7 = builder.mark();
                if (this.cclass_elem_22_alt_6(builder, opp)) {
                    altMarker7.drop();
                } else {
                    altMarker7.rollbackTo();
                    PsiBuilder.Marker altMarker6;;
                    altMarker6 = builder.mark();
                    if (this.cclass_elem_22_alt_5(builder, opp)) {
                        altMarker6.drop();
                    } else {
                        altMarker6.rollbackTo();
                        return false;
                    }
                }
            }
        }
        PsiBuilder.Marker quantMarker24;
        quantMarker24 = builder.mark();
        if (this.cclass_elem_22_quant_23(builder, opp)) {
            quantMarker24.drop();
        } else {
            quantMarker24.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.REGEX_CCLASS_ELEM);
        return true;
    }

    private boolean ccstate_23_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ccstate_23_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ccstate_23(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ccstate_23_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ccstate_23_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean charname_24_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean charname_24_alt_2(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.charname_24_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean charname_24_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.integer_lex_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean charname_24(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.charname_24_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.charname_24_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean charnames_25_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean charnames_25_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean charnames_25_quant_3(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.charnames_25_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        if (!(this.charname_24(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.charnames_25_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean charnames_25(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.charnames_25_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.charnames_25_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean charspec_26_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean charspec_26_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean charspec_26_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean charspec_26_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.charspec_26_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.charspec_26_quant_3(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean charspec_26_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.charspec_26_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.charspec_26_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.charspec_26_quant_4(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean charspec_26_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.charnames_25(builder))) {
            return false;
        }
        return true;
    }

    private boolean charspec_26(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.charspec_26_alt_6(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.charspec_26_alt_5(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.charspec_26_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean circumfix_27_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean circumfix_27_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.circumfix_27_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean circumfix_27_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.circumfix_27_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean circumfix_27_quant_4(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt4.equals("\u00BB"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean circumfix_27_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.circumfix_27_quant_4(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean circumfix_27_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt3.equals("\u00AB"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.circumfix_27_quant_5(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        marker5.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean circumfix_27_quant_7(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt6.equals(">>"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean circumfix_27_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.circumfix_27_quant_7(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        return true;
    }

    private boolean circumfix_27_alt_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker9;
        marker9 = builder.mark();
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt5.equals("<<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.circumfix_27_quant_8(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        marker9.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean circumfix_27_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.BARE_BLOCK) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker13;
        marker13 = builder.mark();
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        marker13.done(Perl6ElementTypes.BLOCK_OR_HASH);
        return true;
    }

    private boolean circumfix_27_quant_11(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.ARRAY_COMPOSER_CLOSE) && (tt8.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean circumfix_27_alt_12(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker15;
        marker15 = builder.mark();
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.ARRAY_COMPOSER_OPEN) && (tt7.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker16;
        quantMarker16 = builder.mark();
        if (this.circumfix_27_quant_11(builder, opp)) {
            quantMarker16.drop();
        } else {
            quantMarker16.rollbackTo();
        }
        marker15.done(Perl6ElementTypes.ARRAY_COMPOSER);
        return true;
    }

    private boolean circumfix_27_quant_13(PsiBuilder builder, OPP opp) {
        String tt10;
        tt10 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt10.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean circumfix_27_alt_14(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker18;
        marker18 = builder.mark();
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt9.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker19;
        quantMarker19 = builder.mark();
        if (this.circumfix_27_quant_13(builder, opp)) {
            quantMarker19.drop();
        } else {
            quantMarker19.rollbackTo();
        }
        marker18.done(Perl6ElementTypes.PARENTHESIZED_EXPRESSION);
        return true;
    }

    private boolean circumfix_27(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.circumfix_27_alt_14(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker17;;
            altMarker17 = builder.mark();
            if (this.circumfix_27_alt_12(builder, opp)) {
                altMarker17.drop();
            } else {
                altMarker17.rollbackTo();
                PsiBuilder.Marker altMarker14;;
                altMarker14 = builder.mark();
                if (this.circumfix_27_alt_10(builder, opp)) {
                    altMarker14.drop();
                } else {
                    altMarker14.rollbackTo();
                    PsiBuilder.Marker altMarker12;;
                    altMarker12 = builder.mark();
                    if (this.circumfix_27_alt_9(builder, opp)) {
                        altMarker12.drop();
                    } else {
                        altMarker12.rollbackTo();
                        PsiBuilder.Marker altMarker8;;
                        altMarker8 = builder.mark();
                        if (this.circumfix_27_alt_6(builder, opp)) {
                            altMarker8.drop();
                        } else {
                            altMarker8.rollbackTo();
                            PsiBuilder.Marker altMarker4;;
                            altMarker4 = builder.mark();
                            if (this.circumfix_27_alt_3(builder, opp)) {
                                altMarker4.drop();
                            } else {
                                altMarker4.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean coloncircumfix_28(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.circumfix_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.coloncircumfix_28(builder))) {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR_HAS_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.colonpair_29_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.coloncircumfix_28(builder))) {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_alt_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt2.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.colonpair_29_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean colonpair_29_alt_5(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt3.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.colonpair_variable_30(builder))) {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_quant_6(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt5.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_alt_7(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt4.equals(":("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker7;
        marker7 = builder.mark();
        if (!(this.signature_165(builder))) {
            return false;
        }
        marker7.done(Perl6ElementTypes.SIGNATURE);
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.colonpair_29_quant_6(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean colonpair_29_alt_8(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt6.equals(":!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean colonpair_29_alt_9(PsiBuilder builder, OPP opp) {
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) && (tt7.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.INTEGER_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean colonpair_29(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.colonpair_29_alt_9(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.colonpair_29_alt_8(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                PsiBuilder.Marker altMarker9;;
                altMarker9 = builder.mark();
                if (this.colonpair_29_alt_7(builder, opp)) {
                    altMarker9.drop();
                } else {
                    altMarker9.rollbackTo();
                    PsiBuilder.Marker altMarker6;;
                    altMarker6 = builder.mark();
                    if (this.colonpair_29_alt_5(builder, opp)) {
                        altMarker6.drop();
                    } else {
                        altMarker6.rollbackTo();
                        PsiBuilder.Marker altMarker5;;
                        altMarker5 = builder.mark();
                        if (this.colonpair_29_alt_4(builder, opp)) {
                            altMarker5.drop();
                        } else {
                            altMarker5.rollbackTo();
                            PsiBuilder.Marker altMarker2;;
                            altMarker2 = builder.mark();
                            if (this.colonpair_29_alt_1(builder, opp)) {
                                altMarker2.drop();
                            } else {
                                altMarker2.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.COLON_PAIR);
        return true;
    }

    private boolean colonpair_variable_30_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean colonpair_variable_30_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean colonpair_variable_30(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.colonpair_variable_30_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.colonpair_variable_30_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean comment_31(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.COMMENT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean complex_number_32(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COMPLEX_LITERAL) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.bare_complex_number_12(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.COMPLEX_LITERAL) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.COMPLEX_LITERAL);
        return true;
    }

    private boolean contextualizer_33_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.CONTEXTUALIZER) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean contextualizer_33_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.CONTEXTUALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.CONTEXTUALIZER) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.contextualizer_33_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean contextualizer_33_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.CIRCUMFIX_CONTEXTUALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.CONTEXTUALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.circumfix_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean contextualizer_33(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.contextualizer_33_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.contextualizer_33_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.CONTEXTUALIZER);
        return true;
    }

    private boolean dec_number_34_alt_1(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.RAT_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.RAT_LITERAL);
        return true;
    }

    private boolean dec_number_34_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.NUMBER_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker3.done(Perl6ElementTypes.NUMBER_LITERAL);
        return true;
    }

    private boolean dec_number_34(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.dec_number_34_alt_2(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.dec_number_34_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean decint_35_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean decint_35_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.decint_35_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.decint_35_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean decint_35(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.decint_35_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.decint_35_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean declarator_36_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.type_declarator_243(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.regex_declarator_140(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.routine_declarator_144(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.initializer_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.declarator_36_quant_5(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean declarator_36_quant_7(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.declarator_36_quant_4(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.declarator_36_quant_6(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean declarator_36_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if (!(this.signature_165(builder))) {
            return false;
        }
        marker5.done(Perl6ElementTypes.SIGNATURE);
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.declarator_36_quant_7(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker4.done(Perl6ElementTypes.VARIABLE_DECLARATION);
        return true;
    }

    private boolean declarator_36_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.initializer_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_quant_10(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.declarator_36_quant_9(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean declarator_36_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker11;
        marker11 = builder.mark();
        if (!(this.variable_declarator_249(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.declarator_36_quant_10(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
        }
        marker11.done(Perl6ElementTypes.VARIABLE_DECLARATION);
        return true;
    }

    private boolean declarator_36_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.initializer_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean declarator_36_quant_13(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker16;
        quantMarker16 = builder.mark();
        if (this.declarator_36_quant_12(builder, opp)) {
            quantMarker16.drop();
        } else {
            quantMarker16.rollbackTo();
        }
        return true;
    }

    private boolean declarator_36_alt_14(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker15;
        marker15 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM_DECLARATION_BACKSLASH) && (tt3.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.defterm_38(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker17;
        quantMarker17 = builder.mark();
        if (this.declarator_36_quant_13(builder, opp)) {
            quantMarker17.drop();
        } else {
            quantMarker17.rollbackTo();
        }
        marker15.done(Perl6ElementTypes.VARIABLE_DECLARATION);
        return true;
    }

    private boolean declarator_36(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker18;
        altMarker18 = builder.mark();
        if (this.declarator_36_alt_14(builder, opp)) {
            altMarker18.drop();
        } else {
            altMarker18.rollbackTo();
            PsiBuilder.Marker altMarker14;;
            altMarker14 = builder.mark();
            if (this.declarator_36_alt_11(builder, opp)) {
                altMarker14.drop();
            } else {
                altMarker14.rollbackTo();
                PsiBuilder.Marker altMarker10;;
                altMarker10 = builder.mark();
                if (this.declarator_36_alt_8(builder, opp)) {
                    altMarker10.drop();
                } else {
                    altMarker10.rollbackTo();
                    PsiBuilder.Marker altMarker3;;
                    altMarker3 = builder.mark();
                    if (this.declarator_36_alt_3(builder, opp)) {
                        altMarker3.drop();
                    } else {
                        altMarker3.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.declarator_36_alt_2(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            PsiBuilder.Marker altMarker1;;
                            altMarker1 = builder.mark();
                            if (this.declarator_36_alt_1(builder, opp)) {
                                altMarker1.drop();
                            } else {
                                altMarker1.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean default_value_37_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean default_value_37_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.default_value_37_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean default_value_37(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.default_value_37_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.PARAMETER_DEFAULT);
        return true;
    }

    private boolean defterm_38(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.TERM_DEFINITION);
        return true;
    }

    private boolean desigilname_39(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.name_77(builder))) {
            return false;
        }
        return true;
    }

    private boolean dotty_40(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_OPERATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.dottyop_41(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.METHOD_CALL);
        return true;
    }

    private boolean dottyop_41_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean dottyop_41_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.methodop_71(builder))) {
            return false;
        }
        return true;
    }

    private boolean dottyop_41(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.dottyop_41_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.dottyop_41_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean eat_terminator_42_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_quant_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.EAT_TERMINATOR_HAS_HEREDOC) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.eat_terminator_42_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean eat_terminator_42_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.unv_246(builder))) {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_STATEMENT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_alt_6(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_TERMINATOR) && (tt1.equals(";"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_alt_7(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.eat_terminator_42_quant_4(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        marker5.done(Perl6ElementTypes.UNTERMINATED_STATEMENT);
        if (!(this.bogus_statement_19(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.eat_terminator_42_alt_6(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.eat_terminator_42_alt_5(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean eat_terminator_42_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.unv_246(builder))) {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_alt_9(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_STATEMENT_STOPPER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker9;;
            quantMarker9 = builder.mark();
            if (this.eat_terminator_42_quant_8(builder, opp)) {
                quantMarker9.drop();
            } else {
                quantMarker9.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker marker10;
        marker10 = builder.mark();
        marker10.done(Perl6ElementTypes.UNTERMINATED_STATEMENT);
        return true;
    }

    private boolean eat_terminator_42_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_STATEMENT_MARK) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_TERMINATOR) && (tt2.equals(";"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_alt_12(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_TERMINATOR) && (tt3.equals(";"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean eat_terminator_42_quant_13(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker14;
        altMarker14 = builder.mark();
        if (this.eat_terminator_42_alt_12(builder, opp)) {
            altMarker14.drop();
        } else {
            altMarker14.rollbackTo();
            PsiBuilder.Marker altMarker13;;
            altMarker13 = builder.mark();
            if (this.eat_terminator_42_alt_11(builder, opp)) {
                altMarker13.drop();
            } else {
                altMarker13.rollbackTo();
                PsiBuilder.Marker altMarker12;;
                altMarker12 = builder.mark();
                if (this.eat_terminator_42_alt_10(builder, opp)) {
                    altMarker12.drop();
                } else {
                    altMarker12.rollbackTo();
                    PsiBuilder.Marker altMarker11;;
                    altMarker11 = builder.mark();
                    if (this.eat_terminator_42_alt_9(builder, opp)) {
                        altMarker11.drop();
                    } else {
                        altMarker11.rollbackTo();
                        PsiBuilder.Marker altMarker8;;
                        altMarker8 = builder.mark();
                        if (this.eat_terminator_42_alt_7(builder, opp)) {
                            altMarker8.drop();
                        } else {
                            altMarker8.rollbackTo();
                            PsiBuilder.Marker altMarker3;;
                            altMarker3 = builder.mark();
                            if (this.eat_terminator_42_alt_3(builder, opp)) {
                                altMarker3.drop();
                            } else {
                                altMarker3.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean eat_terminator_42(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.eat_terminator_42_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker15;
        quantMarker15 = builder.mark();
        if (this.eat_terminator_42_quant_13(builder, opp)) {
            quantMarker15.drop();
        } else {
            quantMarker15.rollbackTo();
        }
        return true;
    }

    private boolean end_keyword_43(PsiBuilder builder) {
        OPP opp;
        opp = null;
        return true;
    }

    private boolean end_prefix_44(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.end_keyword_43(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean enter_regex_nibbler_45(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.regex_nibbler_142(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.REGEX);
        return true;
    }

    private boolean escale_46(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.sign_164(builder))) {
            return false;
        }
        if (!(this.decint_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean fatarrow_47_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean fatarrow_47(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.PAIR_KEY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt1.equals("=>"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.fatarrow_47_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.FATARROW);
        return true;
    }

    private boolean hasdelimiter_48(PsiBuilder builder) {
        OPP opp;
        opp = null;
        return true;
    }

    private boolean heredoc_49_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean heredoc_49_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.HEREDOC) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.heredoc_49_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.HEREDOC);
        return true;
    }

    private boolean heredoc_49(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.heredoc_49_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean hexint_50_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean hexint_50_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean hexint_50_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.hexint_50_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.hexint_50_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean hexint_50_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.hexint_50_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.hexint_50_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean hexint_50(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.hexint_50_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.hexint_50_quant_4(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean hexints_51_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean hexints_51_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean hexints_51_quant_3(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.hexints_51_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        if (!(this.hexint_50(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.hexints_51_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean hexints_51(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.hexints_51_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.hexints_51_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean ident_52_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_52_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_52_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_52(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ident_52_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ident_52_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.ident_52_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean identifier_53_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.apostrophe_5(builder))) {
            return false;
        }
        if (!(this.ident_52(builder))) {
            return false;
        }
        return true;
    }

    private boolean identifier_53(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ident_52(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.identifier_53_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean infix_54_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_54_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.COND_OP_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_54_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.COND_OP_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_54_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt2.equals("!!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_54_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.infix_54_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.infix_54_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infix_54_alt_6(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt1.equals("??"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.infix_54_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.infix_54_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infix_54(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.infix_54_alt_6(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.infix_54_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infix_circumfix_meta_operator_55_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.HYPER_METAOP_MISSING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_circumfix_meta_operator_55_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_circumfix_meta_operator_55_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.infix_circumfix_meta_operator_55_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.infix_circumfix_meta_operator_55_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.HYPER_METAOP);
        return true;
    }

    private boolean infix_circumfix_meta_operator_55_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.HYPER_METAOP_MISSING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_circumfix_meta_operator_55_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infix_circumfix_meta_operator_55_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.infix_circumfix_meta_operator_55_alt_5(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.infix_circumfix_meta_operator_55_alt_4(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                return false;
            }
        }
        marker5.done(Perl6ElementTypes.HYPER_METAOP);
        return true;
    }

    private boolean infix_circumfix_meta_operator_55(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.infix_circumfix_meta_operator_55_alt_6(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.infix_circumfix_meta_operator_55_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_1(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt1.equals("Z"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.ZIP_METAOP);
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt2.equals("X"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        marker3.done(Perl6ElementTypes.CROSS_METAOP);
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt3.equals("S"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        marker5.done(Perl6ElementTypes.SEQUENTIAL_METAOP);
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker7;
        marker7 = builder.mark();
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt4.equals("R"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        marker7.done(Perl6ElementTypes.REVERSE_METAOP);
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.infix_prefix_meta_operator_56_alt_6(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.infix_prefix_meta_operator_56_alt_5(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.infix_prefix_meta_operator_56_alt_4(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean infix_prefix_meta_operator_56_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker12;
        marker12 = builder.mark();
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt5.equals("!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        marker12.done(Perl6ElementTypes.NEGATION_METAOP);
        return true;
    }

    private boolean infix_prefix_meta_operator_56(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.infix_prefix_meta_operator_56_alt_8(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker11;;
            altMarker11 = builder.mark();
            if (this.infix_prefix_meta_operator_56_alt_7(builder, opp)) {
                altMarker11.drop();
            } else {
                altMarker11.rollbackTo();
                PsiBuilder.Marker altMarker6;;
                altMarker6 = builder.mark();
                if (this.infix_prefix_meta_operator_56_alt_3(builder, opp)) {
                    altMarker6.drop();
                } else {
                    altMarker6.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.infix_prefix_meta_operator_56_alt_2(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.infix_prefix_meta_operator_56_alt_1(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean infixish_57_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.infixish_non_assignment_meta_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean infixish_57_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infixish_57_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.ASSIGN_METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_non_assignment_meta_58(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker2.done(Perl6ElementTypes.ASSIGN_METAOP);
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.infixish_57_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean infixish_57_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if (!(this.colonpair_29(builder))) {
            return false;
        }
        marker5.done(Perl6ElementTypes.OPERATOR_ADVERB);
        return true;
    }

    private boolean infixish_57(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.infixish_57_alt_4(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.infixish_57_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.infixish_57_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_1(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.infix_54(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.INFIX);
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.infix_circumfix_meta_operator_55(builder))) {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.infix_prefix_meta_operator_56(builder))) {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.BRACKETED_INFIX_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.BRACKETED_INFIX_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_6(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt2.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.infixish_57(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.infixish_non_assignment_meta_58_alt_6(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.infixish_non_assignment_meta_58_alt_5(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.BRACKETED_INFIX_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_9(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt3.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INFIX_FUNCTION) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.infixish_non_assignment_meta_58_alt_9(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.infixish_non_assignment_meta_58_alt_8(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infixish_non_assignment_meta_58_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt1.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker12;
        altMarker12 = builder.mark();
        if (this.infixish_non_assignment_meta_58_alt_10(builder, opp)) {
            altMarker12.drop();
        } else {
            altMarker12.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.infixish_non_assignment_meta_58_alt_7(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker6;;
                altMarker6 = builder.mark();
                if (this.infixish_non_assignment_meta_58_alt_4(builder, opp)) {
                    altMarker6.drop();
                } else {
                    altMarker6.rollbackTo();
                    return false;
                }
            }
        }
        marker5.done(Perl6ElementTypes.BRACKETED_INFIX);
        return true;
    }

    private boolean infixish_non_assignment_meta_58(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.infixish_non_assignment_meta_58_alt_11(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.infixish_non_assignment_meta_58_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.infixish_non_assignment_meta_58_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.infixish_non_assignment_meta_58_alt_1(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean infixstopper_59_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infixstopper_59_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infixstopper_59_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.infixstopper_59_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.infixstopper_59_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean infixstopper_59_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean infixstopper_59(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.infixstopper_59_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.infixstopper_59_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean initializer_60_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt1.equals(".="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean initializer_60_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt2.equals("::="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean initializer_60_alt_3(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt3.equals(":="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean initializer_60_quant_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean initializer_60_alt_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt4.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.initializer_60_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean initializer_60_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INITIALIZER_MISSING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean initializer_60_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean initializer_60_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NOT_DOTTY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.initializer_60_alt_7(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.initializer_60_alt_6(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean initializer_60_alt_9(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INITIALIZER_MISSING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean initializer_60_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.dottyop_41(builder))) {
            return false;
        }
        return true;
    }

    private boolean initializer_60_alt_11(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.IS_DOTTY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.initializer_60_alt_10(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.initializer_60_alt_9(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean initializer_60(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.PARSING_INITIALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.initializer_60_alt_5(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.initializer_60_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.initializer_60_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.initializer_60_alt_1(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        return false;
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.INFIX);
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker12;
        altMarker12 = builder.mark();
        if (this.initializer_60_alt_11(builder, opp)) {
            altMarker12.drop();
        } else {
            altMarker12.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.initializer_60_alt_8(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean integer_61(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.INTEGER_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.INTEGER_LITERAL);
        return true;
    }

    private boolean integer_lex_62_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.decint_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean integer_lex_62_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.decint_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean integer_lex_62_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean integer_lex_62_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.integer_lex_62_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.decint_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean integer_lex_62_quant_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean integer_lex_62_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.integer_lex_62_quant_5(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        if (!(this.hexint_50(builder))) {
            return false;
        }
        return true;
    }

    private boolean integer_lex_62_quant_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean integer_lex_62_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.integer_lex_62_quant_7(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        if (!(this.octint_83(builder))) {
            return false;
        }
        return true;
    }

    private boolean integer_lex_62_quant_9(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean integer_lex_62_alt_10(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.integer_lex_62_quant_9(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        if (!(this.binint_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean integer_lex_62_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.integer_lex_62_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.integer_lex_62_alt_8(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                PsiBuilder.Marker altMarker6;;
                altMarker6 = builder.mark();
                if (this.integer_lex_62_alt_6(builder, opp)) {
                    altMarker6.drop();
                } else {
                    altMarker6.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.integer_lex_62_alt_4(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.integer_lex_62_alt_2(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean integer_lex_62(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.integer_lex_62_alt_11(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.integer_lex_62_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean interpolation_opener_63_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean interpolation_opener_63_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.quote_119(builder))) {
            return false;
        }
        return true;
    }

    private boolean interpolation_opener_63_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.name_77(builder))) {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean interpolation_opener_63_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.interpolation_opener_63_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.interpolation_opener_63_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.interpolation_opener_63_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean interpolation_opener_63_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean interpolation_opener_63(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.interpolation_opener_63_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.interpolation_opener_63_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean kok_64(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.end_keyword_43(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean label_65(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.LABEL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.LABEL_COLON) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.LABEL);
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean lambda_66_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean lambda_66_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean lambda_66(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.lambda_66_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.lambda_66_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean longname_colonpairs_67_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.LONGNAME_COLONPAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.colonpair_29(builder))) {
            return false;
        }
        return true;
    }

    private boolean longname_colonpairs_67(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.longname_colonpairs_67_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean metachar_68_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.mod_internal_73(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.quantified_atom_115(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.quantified_atom_115(builder))) {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.metachar_68_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean metachar_68_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt1.equals("~"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.metachar_68_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker2.done(Perl6ElementTypes.REGEX_GOAL);
        return true;
    }

    private boolean metachar_68_alt_5(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SCOPE_DECLARATOR) && (tt2.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.statement_170(builder))) {
            return false;
        }
        if (!(this.eat_terminator_42(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.rxcodeblock_149(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.rxqq_152(builder))) {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.rxq_151(builder))) {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.rxQ_147(builder))) {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_10(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ASSERTION_ANGLE_CLOSE) && (tt4.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker11;
        marker11 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ASSERTION_ANGLE_OPEN) && (tt3.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.assertion_8(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.metachar_68_quant_10(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        marker11.done(Perl6ElementTypes.REGEX_ASSERTION);
        return true;
    }

    private boolean metachar_68_alt_12(PsiBuilder builder, OPP opp) {
        if (!(this.rxqw_153(builder))) {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.backslash_11(builder))) {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_14(PsiBuilder builder, OPP opp) {
        if (!(this.regex_nibbler_fresh_rx_143(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_15(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE) && (tt6.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_16(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker16;
        marker16 = builder.mark();
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN) && (tt5.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker17;
        quantMarker17 = builder.mark();
        if (this.metachar_68_quant_14(builder, opp)) {
            quantMarker17.drop();
        } else {
            quantMarker17.rollbackTo();
        }
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.metachar_68_quant_15(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
        }
        marker16.done(Perl6ElementTypes.REGEX_CAPTURE_POSITIONAL);
        return true;
    }

    private boolean metachar_68_quant_17(PsiBuilder builder, OPP opp) {
        if (!(this.regex_nibbler_fresh_rx_143(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_18(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_GROUP_BRACKET_CLOSE) && (tt8.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_19(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker20;
        marker20 = builder.mark();
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_GROUP_BRACKET_OPEN) && (tt7.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker21;
        quantMarker21 = builder.mark();
        if (this.metachar_68_quant_17(builder, opp)) {
            quantMarker21.drop();
        } else {
            quantMarker21.rollbackTo();
        }
        PsiBuilder.Marker quantMarker22;
        quantMarker22 = builder.mark();
        if (this.metachar_68_quant_18(builder, opp)) {
            quantMarker22.drop();
        } else {
            quantMarker22.rollbackTo();
        }
        marker20.done(Perl6ElementTypes.REGEX_GROUP);
        return true;
    }

    private boolean metachar_68_alt_20(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker24;
        marker24 = builder.mark();
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_BUILTIN_CCLASS) && (tt9.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker24.done(Perl6ElementTypes.REGEX_BUILTIN_CCLASS);
        return true;
    }

    private boolean metachar_68_alt_21(PsiBuilder builder, OPP opp) {
        String tt10;
        tt10 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt10.equals(")>"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_22(PsiBuilder builder, OPP opp) {
        String tt11;
        tt11 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt11.equals("<("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_23(PsiBuilder builder, OPP opp) {
        String tt12;
        tt12 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt12.equals("\u00BB"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_24(PsiBuilder builder, OPP opp) {
        String tt13;
        tt13 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt13.equals(">>"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_25(PsiBuilder builder, OPP opp) {
        String tt14;
        tt14 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt14.equals("\u00AB"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_26(PsiBuilder builder, OPP opp) {
        String tt15;
        tt15 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt15.equals("<<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_27(PsiBuilder builder, OPP opp) {
        String tt16;
        tt16 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt16.equals("$"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_28(PsiBuilder builder, OPP opp) {
        String tt17;
        tt17 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt17.equals("$$"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_29(PsiBuilder builder, OPP opp) {
        String tt18;
        tt18 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt18.equals("^"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_30(PsiBuilder builder, OPP opp) {
        String tt19;
        tt19 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_ANCHOR) && (tt19.equals("^^"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_31(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker26;
        marker26 = builder.mark();
        PsiBuilder.Marker altMarker36;
        altMarker36 = builder.mark();
        if (this.metachar_68_alt_30(builder, opp)) {
            altMarker36.drop();
        } else {
            altMarker36.rollbackTo();
            PsiBuilder.Marker altMarker35;;
            altMarker35 = builder.mark();
            if (this.metachar_68_alt_29(builder, opp)) {
                altMarker35.drop();
            } else {
                altMarker35.rollbackTo();
                PsiBuilder.Marker altMarker34;;
                altMarker34 = builder.mark();
                if (this.metachar_68_alt_28(builder, opp)) {
                    altMarker34.drop();
                } else {
                    altMarker34.rollbackTo();
                    PsiBuilder.Marker altMarker33;;
                    altMarker33 = builder.mark();
                    if (this.metachar_68_alt_27(builder, opp)) {
                        altMarker33.drop();
                    } else {
                        altMarker33.rollbackTo();
                        PsiBuilder.Marker altMarker32;;
                        altMarker32 = builder.mark();
                        if (this.metachar_68_alt_26(builder, opp)) {
                            altMarker32.drop();
                        } else {
                            altMarker32.rollbackTo();
                            PsiBuilder.Marker altMarker31;;
                            altMarker31 = builder.mark();
                            if (this.metachar_68_alt_25(builder, opp)) {
                                altMarker31.drop();
                            } else {
                                altMarker31.rollbackTo();
                                PsiBuilder.Marker altMarker30;;
                                altMarker30 = builder.mark();
                                if (this.metachar_68_alt_24(builder, opp)) {
                                    altMarker30.drop();
                                } else {
                                    altMarker30.rollbackTo();
                                    PsiBuilder.Marker altMarker29;;
                                    altMarker29 = builder.mark();
                                    if (this.metachar_68_alt_23(builder, opp)) {
                                        altMarker29.drop();
                                    } else {
                                        altMarker29.rollbackTo();
                                        PsiBuilder.Marker altMarker28;;
                                        altMarker28 = builder.mark();
                                        if (this.metachar_68_alt_22(builder, opp)) {
                                            altMarker28.drop();
                                        } else {
                                            altMarker28.rollbackTo();
                                            PsiBuilder.Marker altMarker27;;
                                            altMarker27 = builder.mark();
                                            if (this.metachar_68_alt_21(builder, opp)) {
                                                altMarker27.drop();
                                            } else {
                                                altMarker27.rollbackTo();
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        marker26.done(Perl6ElementTypes.REGEX_ANCHOR);
        return true;
    }

    private boolean metachar_68_alt_32(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_VARIABLE_BINDING_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean metachar_68_alt_33(PsiBuilder builder, OPP opp) {
        if (!(this.quantified_atom_115(builder))) {
            return false;
        }
        return true;
    }

    private boolean metachar_68_quant_34(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_VARIABLE_BINDING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt20;
        tt20 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt20.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker40;
        altMarker40 = builder.mark();
        if (this.metachar_68_alt_33(builder, opp)) {
            altMarker40.drop();
        } else {
            altMarker40.rollbackTo();
            PsiBuilder.Marker altMarker39;;
            altMarker39 = builder.mark();
            if (this.metachar_68_alt_32(builder, opp)) {
                altMarker39.drop();
            } else {
                altMarker39.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean metachar_68_alt_35(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker38;
        marker38 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker41;
        quantMarker41 = builder.mark();
        if (this.metachar_68_quant_34(builder, opp)) {
            quantMarker41.drop();
        } else {
            quantMarker41.rollbackTo();
        }
        marker38.done(Perl6ElementTypes.REGEX_VARIABLE);
        return true;
    }

    private boolean metachar_68(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker42;
        altMarker42 = builder.mark();
        if (this.metachar_68_alt_35(builder, opp)) {
            altMarker42.drop();
        } else {
            altMarker42.rollbackTo();
            PsiBuilder.Marker altMarker37;;
            altMarker37 = builder.mark();
            if (this.metachar_68_alt_31(builder, opp)) {
                altMarker37.drop();
            } else {
                altMarker37.rollbackTo();
                PsiBuilder.Marker altMarker25;;
                altMarker25 = builder.mark();
                if (this.metachar_68_alt_20(builder, opp)) {
                    altMarker25.drop();
                } else {
                    altMarker25.rollbackTo();
                    PsiBuilder.Marker altMarker23;;
                    altMarker23 = builder.mark();
                    if (this.metachar_68_alt_19(builder, opp)) {
                        altMarker23.drop();
                    } else {
                        altMarker23.rollbackTo();
                        PsiBuilder.Marker altMarker19;;
                        altMarker19 = builder.mark();
                        if (this.metachar_68_alt_16(builder, opp)) {
                            altMarker19.drop();
                        } else {
                            altMarker19.rollbackTo();
                            PsiBuilder.Marker altMarker15;;
                            altMarker15 = builder.mark();
                            if (this.metachar_68_alt_13(builder, opp)) {
                                altMarker15.drop();
                            } else {
                                altMarker15.rollbackTo();
                                PsiBuilder.Marker altMarker14;;
                                altMarker14 = builder.mark();
                                if (this.metachar_68_alt_12(builder, opp)) {
                                    altMarker14.drop();
                                } else {
                                    altMarker14.rollbackTo();
                                    PsiBuilder.Marker altMarker13;;
                                    altMarker13 = builder.mark();
                                    if (this.metachar_68_alt_11(builder, opp)) {
                                        altMarker13.drop();
                                    } else {
                                        altMarker13.rollbackTo();
                                        PsiBuilder.Marker altMarker10;;
                                        altMarker10 = builder.mark();
                                        if (this.metachar_68_alt_9(builder, opp)) {
                                            altMarker10.drop();
                                        } else {
                                            altMarker10.rollbackTo();
                                            PsiBuilder.Marker altMarker9;;
                                            altMarker9 = builder.mark();
                                            if (this.metachar_68_alt_8(builder, opp)) {
                                                altMarker9.drop();
                                            } else {
                                                altMarker9.rollbackTo();
                                                PsiBuilder.Marker altMarker8;;
                                                altMarker8 = builder.mark();
                                                if (this.metachar_68_alt_7(builder, opp)) {
                                                    altMarker8.drop();
                                                } else {
                                                    altMarker8.rollbackTo();
                                                    PsiBuilder.Marker altMarker7;;
                                                    altMarker7 = builder.mark();
                                                    if (this.metachar_68_alt_6(builder, opp)) {
                                                        altMarker7.drop();
                                                    } else {
                                                        altMarker7.rollbackTo();
                                                        PsiBuilder.Marker altMarker6;;
                                                        altMarker6 = builder.mark();
                                                        if (this.metachar_68_alt_5(builder, opp)) {
                                                            altMarker6.drop();
                                                        } else {
                                                            altMarker6.rollbackTo();
                                                            PsiBuilder.Marker altMarker5;;
                                                            altMarker5 = builder.mark();
                                                            if (this.metachar_68_alt_4(builder, opp)) {
                                                                altMarker5.drop();
                                                            } else {
                                                                altMarker5.rollbackTo();
                                                                PsiBuilder.Marker altMarker1;;
                                                                altMarker1 = builder.mark();
                                                                if (this.metachar_68_alt_1(builder, opp)) {
                                                                    altMarker1.drop();
                                                                } else {
                                                                    altMarker1.rollbackTo();
                                                                    return false;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean method_def_69_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.method_name_70(builder))) {
            return false;
        }
        return true;
    }

    private boolean method_def_69_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean method_def_69_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.method_def_69_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker2.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean method_def_69_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean method_def_69_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean method_def_69_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean method_def_69_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean method_def_69_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.onlystar_85(builder))) {
            return false;
        }
        return true;
    }

    private boolean method_def_69(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.method_def_69_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.method_def_69_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.method_def_69_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.method_def_69_quant_5(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.method_def_69_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.method_def_69_alt_7(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                PsiBuilder.Marker altMarker7;;
                altMarker7 = builder.mark();
                if (this.method_def_69_alt_6(builder, opp)) {
                    altMarker7.drop();
                } else {
                    altMarker7.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean method_name_70(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.ROUTINE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.LONG_NAME);
        return true;
    }

    private boolean methodop_71_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean methodop_71_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean methodop_71_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.methodop_71_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.methodop_71_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        if (!(this.quote_119(builder))) {
            return false;
        }
        return true;
    }

    private boolean methodop_71_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        return true;
    }

    private boolean methodop_71_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker5.done(Perl6ElementTypes.LONG_NAME);
        return true;
    }

    private boolean methodop_71_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean methodop_71_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean methodop_71_alt_8(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INVOCANT_MARKER) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean methodop_71_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.args_7(builder))) {
            return false;
        }
        return true;
    }

    private boolean methodop_71_alt_10(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.methodop_71_alt_9(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.methodop_71_alt_8(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean methodop_71_quant_11(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean methodop_71(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.methodop_71_alt_5(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.methodop_71_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.methodop_71_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.methodop_71_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.methodop_71_alt_10(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.methodop_71_alt_7(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.methodop_71_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean mod_ident_72_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_ident_72_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_ident_72_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.mod_ident_72_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean mod_ident_72_quant_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_ident_72_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.mod_ident_72_quant_4(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean mod_ident_72_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_ident_72_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_ident_72_quant_8(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_ident_72_alt_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.mod_ident_72_quant_8(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean mod_ident_72(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.mod_ident_72_alt_9(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.mod_ident_72_alt_7(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker6;;
                altMarker6 = builder.mark();
                if (this.mod_ident_72_alt_6(builder, opp)) {
                    altMarker6.drop();
                } else {
                    altMarker6.rollbackTo();
                    PsiBuilder.Marker altMarker5;;
                    altMarker5 = builder.mark();
                    if (this.mod_ident_72_alt_5(builder, opp)) {
                        altMarker5.drop();
                    } else {
                        altMarker5.rollbackTo();
                        PsiBuilder.Marker altMarker3;;
                        altMarker3 = builder.mark();
                        if (this.mod_ident_72_alt_3(builder, opp)) {
                            altMarker3.drop();
                        } else {
                            altMarker3.rollbackTo();
                            PsiBuilder.Marker altMarker1;;
                            altMarker1 = builder.mark();
                            if (this.mod_ident_72_alt_1(builder, opp)) {
                                altMarker1.drop();
                            } else {
                                altMarker1.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean mod_internal_73_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_internal_73_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean mod_internal_73_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.mod_internal_73_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.mod_internal_73_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean mod_internal_73_quant_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean mod_internal_73_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.value_247(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.mod_internal_73_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean mod_internal_73_quant_6(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) && (tt2.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.mod_internal_73_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean mod_internal_73_alt_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.mod_internal_73_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean mod_internal_73_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL_NUMERIC) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean mod_internal_73_alt_9(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) && (tt4.equals("!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean mod_internal_73(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.mod_internal_73_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_MOD_INTERNAL) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.mod_internal_73_alt_9(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.mod_internal_73_alt_8(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.mod_internal_73_alt_7(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    return false;
                }
            }
        }
        marker4.done(Perl6ElementTypes.REGEX_MOD_INTERNAL);
        return true;
    }

    private boolean module_name_74(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.LONG_NAME);
        marker1.done(Perl6ElementTypes.MODULE_NAME);
        return true;
    }

    private boolean morename_75_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.identifier_53(builder))) {
            return false;
        }
        return true;
    }

    private boolean morename_75(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.morename_75_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean multi_declarator_76_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.declarator_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean multi_declarator_76_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean multi_declarator_76_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        if (!(this.routine_def_145(builder))) {
            return false;
        }
        marker4.done(Perl6ElementTypes.ROUTINE_DECLARATION);
        return true;
    }

    private boolean multi_declarator_76_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.declarator_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean multi_declarator_76_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.MULTI_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.multi_declarator_76_alt_4(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.multi_declarator_76_alt_3(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.multi_declarator_76_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    return false;
                }
            }
        }
        marker2.done(Perl6ElementTypes.MULTI_DECLARATION);
        return true;
    }

    private boolean multi_declarator_76(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.multi_declarator_76_alt_5(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.multi_declarator_76_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean name_77_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.morename_75(builder))) {
            return false;
        }
        return true;
    }

    private boolean name_77_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.name_77_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.name_77_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean name_77_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.morename_75(builder))) {
            return false;
        }
        return true;
    }

    private boolean name_77_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.identifier_53(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.name_77_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean name_77(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.name_77_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.name_77_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean named_param_78_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.param_var_91(builder))) {
            return false;
        }
        return true;
    }

    private boolean named_param_78_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.param_var_91(builder))) {
            return false;
        }
        return true;
    }

    private boolean named_param_78_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.named_param_78(builder))) {
            return false;
        }
        return true;
    }

    private boolean named_param_78_quant_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.NAMED_PARAMETER_SYNTAX) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean named_param_78_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.named_param_78_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean named_param_78_quant_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.named_param_78_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.named_param_78_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.named_param_78_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean named_param_78_quant_7(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.NAMED_PARAMETER_SYNTAX) && (tt2.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.named_param_78_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean named_param_78_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAMED_PARAMETER_NAME_ALIAS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.named_param_78_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean named_param_78_quant_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.named_param_78_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.named_param_78_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean named_param_78(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.NAMED_PARAMETER_SYNTAX) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.named_param_78_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.NAMED_PARAMETER);
        return true;
    }

    private boolean nextterm_79_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean nextterm_79_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.DOTTY_NEXT_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.dottyop_41(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.METHOD_CALL);
        return true;
    }

    private boolean nextterm_79_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.NULL_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker4.done(Perl6ElementTypes.NULL_TERM);
        return true;
    }

    private boolean nextterm_79_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean nextterm_79_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.nextterm_79_alt_4(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.nextterm_79_alt_3(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean nextterm_79(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.nextterm_79_alt_5(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.nextterm_79_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.nextterm_79_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean normspace_80(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean number_81(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.numish_82(builder))) {
            return false;
        }
        return true;
    }

    private boolean numish_82_alt_1(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.NUMBER_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.NUMBER_LITERAL);
        return true;
    }

    private boolean numish_82_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.integer_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean numish_82_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.dec_number_34(builder))) {
            return false;
        }
        return true;
    }

    private boolean numish_82_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.rad_number_137(builder))) {
            return false;
        }
        return true;
    }

    private boolean numish_82_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.rat_number_139(builder))) {
            return false;
        }
        return true;
    }

    private boolean numish_82_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.complex_number_32(builder))) {
            return false;
        }
        return true;
    }

    private boolean numish_82(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.numish_82_alt_6(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.numish_82_alt_5(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.numish_82_alt_4(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.numish_82_alt_3(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker3;;
                        altMarker3 = builder.mark();
                        if (this.numish_82_alt_2(builder, opp)) {
                            altMarker3.drop();
                        } else {
                            altMarker3.rollbackTo();
                            PsiBuilder.Marker altMarker2;;
                            altMarker2 = builder.mark();
                            if (this.numish_82_alt_1(builder, opp)) {
                                altMarker2.drop();
                            } else {
                                altMarker2.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean octint_83_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean octint_83_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.octint_83_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.octint_83_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean octint_83(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.octint_83_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.octint_83_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean octints_84_quant_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean octints_84_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean octints_84_quant_3(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.octints_84_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        if (!(this.octint_83(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.octints_84_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean octints_84(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.octints_84_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.octints_84_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean onlystar_85(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BLOCK_CURLY_BRACKET_OPEN) && (tt1.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.ONLY_STAR) && (tt2.equals("*"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE) && (tt3.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean package_declarator_86(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.PACKAGE_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.package_def_87(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.PACKAGE_DECLARATION);
        return true;
    }

    private boolean package_def_87_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean package_def_87_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean package_def_87_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_def_87_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.statementlist_212(builder))) {
            return false;
        }
        return true;
    }

    private boolean package_def_87_alt_5(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_TERMINATOR) && (tt1.equals(";"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.package_def_87_quant_4(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean package_def_87_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean package_def_87(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.package_def_87_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.package_def_87_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.package_def_87_alt_6(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.package_def_87_alt_5(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.package_def_87_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean package_kind_88_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_8(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_9(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88_alt_10(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean package_kind_88(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.package_kind_88_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.package_kind_88_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.package_kind_88_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    PsiBuilder.Marker altMarker7;;
                    altMarker7 = builder.mark();
                    if (this.package_kind_88_alt_7(builder, opp)) {
                        altMarker7.drop();
                    } else {
                        altMarker7.rollbackTo();
                        PsiBuilder.Marker altMarker6;;
                        altMarker6 = builder.mark();
                        if (this.package_kind_88_alt_6(builder, opp)) {
                            altMarker6.drop();
                        } else {
                            altMarker6.rollbackTo();
                            PsiBuilder.Marker altMarker5;;
                            altMarker5 = builder.mark();
                            if (this.package_kind_88_alt_5(builder, opp)) {
                                altMarker5.drop();
                            } else {
                                altMarker5.rollbackTo();
                                PsiBuilder.Marker altMarker4;;
                                altMarker4 = builder.mark();
                                if (this.package_kind_88_alt_4(builder, opp)) {
                                    altMarker4.drop();
                                } else {
                                    altMarker4.rollbackTo();
                                    PsiBuilder.Marker altMarker3;;
                                    altMarker3 = builder.mark();
                                    if (this.package_kind_88_alt_3(builder, opp)) {
                                        altMarker3.drop();
                                    } else {
                                        altMarker3.rollbackTo();
                                        PsiBuilder.Marker altMarker2;;
                                        altMarker2 = builder.mark();
                                        if (this.package_kind_88_alt_2(builder, opp)) {
                                            altMarker2.drop();
                                        } else {
                                            altMarker2.rollbackTo();
                                            PsiBuilder.Marker altMarker1;;
                                            altMarker1 = builder.mark();
                                            if (this.package_kind_88_alt_1(builder, opp)) {
                                                altMarker1.drop();
                                            } else {
                                                altMarker1.rollbackTo();
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean param_sep_89(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_SEPARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean param_term_90_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.defterm_38(builder))) {
            return false;
        }
        return true;
    }

    private boolean param_term_90(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.param_term_90_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean param_var_91_quant_1(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if (!(this.postcircumfix_108(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.ARRAY_SHAPE);
        return true;
    }

    private boolean param_var_91_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.param_var_91_quant_1(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.PARAMETER_VARIABLE);
        return true;
    }

    private boolean param_var_91_quant_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean param_var_91_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.param_var_91_quant_3(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker5.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean param_var_91_quant_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SIGNATURE_BRACKET_CLOSE) && (tt4.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean param_var_91_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker8;
        marker8 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SIGNATURE_BRACKET_OPEN) && (tt3.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.param_var_91_quant_5(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker8.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean param_var_91(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.param_var_91_alt_6(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.param_var_91_alt_4(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker4;;
                altMarker4 = builder.mark();
                if (this.param_var_91_alt_2(builder, opp)) {
                    altMarker4.drop();
                } else {
                    altMarker4.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean parameter_92_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.named_param_78(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.param_var_91(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.parameter_92_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.parameter_92_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.parameter_92_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean parameter_92_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.param_var_91(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.parameter_92_alt_6(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.parameter_92_alt_5(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean parameter_92_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.IS_PARAM_TERM_QUANT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) && (tt1.equals("+"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_9(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) && (tt2.equals("|"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_10(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) && (tt3.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.parameter_92_alt_10(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.parameter_92_alt_9(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                PsiBuilder.Marker altMarker9;;
                altMarker9 = builder.mark();
                if (this.parameter_92_alt_8(builder, opp)) {
                    altMarker9.drop();
                } else {
                    altMarker9.rollbackTo();
                    return false;
                }
            }
        }
        if (!(this.param_term_90(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.type_constraint_242(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_13(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_ANON) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_14(PsiBuilder builder, OPP opp) {
        if (!(this.named_param_78(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_15(PsiBuilder builder, OPP opp) {
        if (!(this.param_var_91(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_quant_16(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_17(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker17;
        altMarker17 = builder.mark();
        if (this.parameter_92_alt_15(builder, opp)) {
            altMarker17.drop();
        } else {
            altMarker17.rollbackTo();
            PsiBuilder.Marker altMarker16;;
            altMarker16 = builder.mark();
            if (this.parameter_92_alt_14(builder, opp)) {
                altMarker16.drop();
            } else {
                altMarker16.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.parameter_92_quant_16(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
        }
        return true;
    }

    private boolean parameter_92_alt_18(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_19(PsiBuilder builder, OPP opp) {
        if (!(this.param_var_91(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_20(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker21;
        altMarker21 = builder.mark();
        if (this.parameter_92_alt_19(builder, opp)) {
            altMarker21.drop();
        } else {
            altMarker21.rollbackTo();
            PsiBuilder.Marker altMarker20;;
            altMarker20 = builder.mark();
            if (this.parameter_92_alt_18(builder, opp)) {
                altMarker20.drop();
            } else {
                altMarker20.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean parameter_92_alt_21(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.IS_PARAM_TERM_QUANT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) && (tt4.equals("+"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_22(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARAMETER_QUANTIFIER) && (tt5.equals("|"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_23(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM_DECLARATION_BACKSLASH) && (tt6.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_24(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker25;
        altMarker25 = builder.mark();
        if (this.parameter_92_alt_23(builder, opp)) {
            altMarker25.drop();
        } else {
            altMarker25.rollbackTo();
            PsiBuilder.Marker altMarker24;;
            altMarker24 = builder.mark();
            if (this.parameter_92_alt_22(builder, opp)) {
                altMarker24.drop();
            } else {
                altMarker24.rollbackTo();
                PsiBuilder.Marker altMarker23;;
                altMarker23 = builder.mark();
                if (this.parameter_92_alt_21(builder, opp)) {
                    altMarker23.drop();
                } else {
                    altMarker23.rollbackTo();
                    return false;
                }
            }
        }
        if (!(this.param_term_90(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_alt_25(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.parameter_92_quant_12(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker14;;
            quantMarker14 = builder.mark();
            if (this.parameter_92_quant_12(builder, opp)) {
                quantMarker14.drop();
            } else {
                quantMarker14.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker altMarker26;
        altMarker26 = builder.mark();
        if (this.parameter_92_alt_24(builder, opp)) {
            altMarker26.drop();
        } else {
            altMarker26.rollbackTo();
            PsiBuilder.Marker altMarker22;;
            altMarker22 = builder.mark();
            if (this.parameter_92_alt_20(builder, opp)) {
                altMarker22.drop();
            } else {
                altMarker22.rollbackTo();
                PsiBuilder.Marker altMarker19;;
                altMarker19 = builder.mark();
                if (this.parameter_92_alt_17(builder, opp)) {
                    altMarker19.drop();
                } else {
                    altMarker19.rollbackTo();
                    PsiBuilder.Marker altMarker15;;
                    altMarker15 = builder.mark();
                    if (this.parameter_92_alt_13(builder, opp)) {
                        altMarker15.drop();
                    } else {
                        altMarker15.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean parameter_92_quant_26(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_quant_27(PsiBuilder builder, OPP opp) {
        if (!(this.post_constraint_107(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92_quant_28(PsiBuilder builder, OPP opp) {
        if (!(this.default_value_37(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_92(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker27;
        altMarker27 = builder.mark();
        if (this.parameter_92_alt_25(builder, opp)) {
            altMarker27.drop();
        } else {
            altMarker27.rollbackTo();
            PsiBuilder.Marker altMarker12;;
            altMarker12 = builder.mark();
            if (this.parameter_92_alt_11(builder, opp)) {
                altMarker12.drop();
            } else {
                altMarker12.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.parameter_92_alt_7(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    PsiBuilder.Marker altMarker5;;
                    altMarker5 = builder.mark();
                    if (this.parameter_92_alt_4(builder, opp)) {
                        altMarker5.drop();
                    } else {
                        altMarker5.rollbackTo();
                        return false;
                    }
                }
            }
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker28;;
            quantMarker28 = builder.mark();
            if (this.parameter_92_quant_26(builder, opp)) {
                quantMarker28.drop();
            } else {
                quantMarker28.rollbackTo();
                break;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker29;;
            quantMarker29 = builder.mark();
            if (this.parameter_92_quant_27(builder, opp)) {
                quantMarker29.drop();
            } else {
                quantMarker29.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker30;
        quantMarker30 = builder.mark();
        if (this.parameter_92_quant_28(builder, opp)) {
            quantMarker30.drop();
        } else {
            quantMarker30.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.PARAMETER);
        return true;
    }

    private boolean pblock_93_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.MISSING_BLOCK) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pblock_93_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.BLOCK);
        return true;
    }

    private boolean pblock_93_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean pblock_93_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.LAMBDA) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if (!(this.signature_165(builder))) {
            return false;
        }
        marker5.done(Perl6ElementTypes.SIGNATURE);
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.pblock_93_quant_3(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker4.done(Perl6ElementTypes.POINTY_BLOCK);
        return true;
    }

    private boolean pblock_93(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.pblock_93_alt_4(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.pblock_93_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.pblock_93_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean phaser_name_94_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_8(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_9(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_10(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_11(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_12(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_13(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_14(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_15(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94_alt_16(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean phaser_name_94(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker16;
        altMarker16 = builder.mark();
        if (this.phaser_name_94_alt_16(builder, opp)) {
            altMarker16.drop();
        } else {
            altMarker16.rollbackTo();
            PsiBuilder.Marker altMarker15;;
            altMarker15 = builder.mark();
            if (this.phaser_name_94_alt_15(builder, opp)) {
                altMarker15.drop();
            } else {
                altMarker15.rollbackTo();
                PsiBuilder.Marker altMarker14;;
                altMarker14 = builder.mark();
                if (this.phaser_name_94_alt_14(builder, opp)) {
                    altMarker14.drop();
                } else {
                    altMarker14.rollbackTo();
                    PsiBuilder.Marker altMarker13;;
                    altMarker13 = builder.mark();
                    if (this.phaser_name_94_alt_13(builder, opp)) {
                        altMarker13.drop();
                    } else {
                        altMarker13.rollbackTo();
                        PsiBuilder.Marker altMarker12;;
                        altMarker12 = builder.mark();
                        if (this.phaser_name_94_alt_12(builder, opp)) {
                            altMarker12.drop();
                        } else {
                            altMarker12.rollbackTo();
                            PsiBuilder.Marker altMarker11;;
                            altMarker11 = builder.mark();
                            if (this.phaser_name_94_alt_11(builder, opp)) {
                                altMarker11.drop();
                            } else {
                                altMarker11.rollbackTo();
                                PsiBuilder.Marker altMarker10;;
                                altMarker10 = builder.mark();
                                if (this.phaser_name_94_alt_10(builder, opp)) {
                                    altMarker10.drop();
                                } else {
                                    altMarker10.rollbackTo();
                                    PsiBuilder.Marker altMarker9;;
                                    altMarker9 = builder.mark();
                                    if (this.phaser_name_94_alt_9(builder, opp)) {
                                        altMarker9.drop();
                                    } else {
                                        altMarker9.rollbackTo();
                                        PsiBuilder.Marker altMarker8;;
                                        altMarker8 = builder.mark();
                                        if (this.phaser_name_94_alt_8(builder, opp)) {
                                            altMarker8.drop();
                                        } else {
                                            altMarker8.rollbackTo();
                                            PsiBuilder.Marker altMarker7;;
                                            altMarker7 = builder.mark();
                                            if (this.phaser_name_94_alt_7(builder, opp)) {
                                                altMarker7.drop();
                                            } else {
                                                altMarker7.rollbackTo();
                                                PsiBuilder.Marker altMarker6;;
                                                altMarker6 = builder.mark();
                                                if (this.phaser_name_94_alt_6(builder, opp)) {
                                                    altMarker6.drop();
                                                } else {
                                                    altMarker6.rollbackTo();
                                                    PsiBuilder.Marker altMarker5;;
                                                    altMarker5 = builder.mark();
                                                    if (this.phaser_name_94_alt_5(builder, opp)) {
                                                        altMarker5.drop();
                                                    } else {
                                                        altMarker5.rollbackTo();
                                                        PsiBuilder.Marker altMarker4;;
                                                        altMarker4 = builder.mark();
                                                        if (this.phaser_name_94_alt_4(builder, opp)) {
                                                            altMarker4.drop();
                                                        } else {
                                                            altMarker4.rollbackTo();
                                                            PsiBuilder.Marker altMarker3;;
                                                            altMarker3 = builder.mark();
                                                            if (this.phaser_name_94_alt_3(builder, opp)) {
                                                                altMarker3.drop();
                                                            } else {
                                                                altMarker3.rollbackTo();
                                                                PsiBuilder.Marker altMarker2;;
                                                                altMarker2 = builder.mark();
                                                                if (this.phaser_name_94_alt_2(builder, opp)) {
                                                                    altMarker2.drop();
                                                                } else {
                                                                    altMarker2.rollbackTo();
                                                                    PsiBuilder.Marker altMarker1;;
                                                                    altMarker1 = builder.mark();
                                                                    if (this.phaser_name_94_alt_1(builder, opp)) {
                                                                        altMarker1.drop();
                                                                    } else {
                                                                        altMarker1.rollbackTo();
                                                                        return false;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean pod_block_95_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_block_abbreviated_96(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_95_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.pod_block_paragraph_100(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_95_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.pod_block_delimited_98(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_95_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.pod_block_finish_99(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_95(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.pod_block_95_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.pod_block_95_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.pod_block_95_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.pod_block_95_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean pod_block_abbreviated_96_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_abbreviated_96_quant_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.pod_para_content_106(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.pod_block_abbreviated_96_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_abbreviated_96(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TYPENAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.pod_block_abbreviated_96_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.POD_BLOCK_ABBREVIATED);
        return true;
    }

    private boolean pod_block_content_97_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_content_97_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.pod_formatting_code_104(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_content_97_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_block_content_97_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.pod_block_content_97_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.pod_block_content_97_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean pod_block_content_97_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_content_97_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.pod_block_content_97_quant_4(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.pod_block_content_97_quant_4(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.pod_block_content_97_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_content_97_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.pod_block_95(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_content_97_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_HAVE_CONTENT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.pod_block_content_97_alt_7(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.pod_block_content_97_alt_6(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.pod_block_content_97_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean pod_block_content_97(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker9;;
            quantMarker9 = builder.mark();
            if (this.pod_block_content_97_quant_8(builder, opp)) {
                quantMarker9.drop();
            } else {
                quantMarker9.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_configuration_101(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TYPENAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.pod_block_delimited_98_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt2.equals("=end"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.pod_block_delimited_98_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.pod_block_content_97(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.pod_block_delimited_98_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.pod_block_delimited_98_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_delimited_98_quant_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TYPENAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.pod_block_delimited_98_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.pod_block_delimited_98_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_delimited_98(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt1.equals("=begin"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.pod_block_delimited_98_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.POD_BLOCK_DELIMITED);
        return true;
    }

    private boolean pod_block_finish_99_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt1.equals("=finish"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_block_finish_99_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt2.equals("=for"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_TYPENAME) && (tt3.equals("finish"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_block_finish_99_alt_3(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt4.equals("=begin"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_TYPENAME) && (tt5.equals("finish"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_block_finish_99_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_finish_99(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.pod_block_finish_99_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.pod_block_finish_99_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.pod_block_finish_99_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.pod_block_finish_99_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_FINISH_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.POD_BLOCK_FINISH);
        return true;
    }

    private boolean pod_block_paragraph_100_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_configuration_101(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_paragraph_100_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_block_paragraph_100_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        if (!(this.pod_para_content_106(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.pod_block_paragraph_100_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_paragraph_100_quant_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TYPENAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.pod_block_paragraph_100_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.pod_block_paragraph_100_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean pod_block_paragraph_100(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POD_DIRECTIVE) && (tt1.equals("=for"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.pod_block_paragraph_100_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.POD_BLOCK_PARAGRAPH);
        return true;
    }

    private boolean pod_configuration_101(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_CONFIGURATION) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.POD_CONFIGURATION);
        return true;
    }

    private boolean pod_content_toplevel_102(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.pod_block_95(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_formatted_text_103_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_formatted_text_103_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_formatted_text_103_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.pod_formatting_code_104(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_formatted_text_103_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.pod_formatted_text_103_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.pod_formatted_text_103_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.pod_formatted_text_103_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean pod_formatted_text_103(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.pod_formatted_text_103_quant_4(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        marker1.done(Perl6ElementTypes.POD_TEXT);
        return true;
    }

    private boolean pod_formatting_code_104_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_FORMAT_STOPPER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_formatting_code_104(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.FORMAT_CODE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_FORMAT_STARTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.pod_formatted_text_103(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.pod_formatting_code_104_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.POD_FORMATTED);
        return true;
    }

    private boolean pod_newline_105(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_NEWLINE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_para_content_106_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_formatting_code_104(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_para_content_106_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.POD_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean pod_para_content_106_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.pod_para_content_106_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.pod_para_content_106_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean pod_para_content_106_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.pod_newline_105(builder))) {
            return false;
        }
        return true;
    }

    private boolean pod_para_content_106_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.pod_para_content_106_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.pod_para_content_106_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.pod_para_content_106_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean pod_para_content_106(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.pod_para_content_106_quant_5(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean post_constraint_107_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean post_constraint_107_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHERE_CONSTRAINT) && (tt1.equals("where"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.post_constraint_107_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.WHERE_CONSTRAINT);
        return true;
    }

    private boolean post_constraint_107_quant_3(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean post_constraint_107_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt2.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.post_constraint_107_quant_3(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        marker4.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean post_constraint_107_quant_5(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SIGNATURE_BRACKET_CLOSE) && (tt5.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean post_constraint_107_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker7;
        marker7 = builder.mark();
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SIGNATURE_BRACKET_OPEN) && (tt4.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.post_constraint_107_quant_5(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker7.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean post_constraint_107_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean post_constraint_107(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.post_constraint_107_alt_6(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.post_constraint_107_alt_4(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.post_constraint_107_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.post_constraint_107_quant_7(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        return true;
    }

    private boolean postcircumfix_108_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postcircumfix_108_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.postcircumfix_108_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.CALL);
        return true;
    }

    private boolean postcircumfix_108_quant_3(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_CLOSE) && (tt4.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postcircumfix_108_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.postcircumfix_108_quant_3(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean postcircumfix_108_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_OPEN) && (tt3.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.postcircumfix_108_quant_4(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker4.done(Perl6ElementTypes.HASH_INDEX);
        return true;
    }

    private boolean postcircumfix_108_quant_6(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_CLOSE) && (tt6.equals("\u00BB"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postcircumfix_108_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.postcircumfix_108_quant_6(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean postcircumfix_108_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker8;
        marker8 = builder.mark();
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_OPEN) && (tt5.equals("\u00AB"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.postcircumfix_108_quant_7(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker8.done(Perl6ElementTypes.HASH_INDEX);
        return true;
    }

    private boolean postcircumfix_108_quant_9(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_CLOSE) && (tt8.equals(">>"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postcircumfix_108_quant_10(PsiBuilder builder, OPP opp) {
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.postcircumfix_108_quant_9(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
        }
        return true;
    }

    private boolean postcircumfix_108_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker12;
        marker12 = builder.mark();
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_OPEN) && (tt7.equals("<<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker14;
        quantMarker14 = builder.mark();
        if (this.postcircumfix_108_quant_10(builder, opp)) {
            quantMarker14.drop();
        } else {
            quantMarker14.rollbackTo();
        }
        marker12.done(Perl6ElementTypes.HASH_INDEX);
        return true;
    }

    private boolean postcircumfix_108_quant_12(PsiBuilder builder, OPP opp) {
        String tt10;
        tt10 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_CLOSE) && (tt10.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postcircumfix_108_quant_13(PsiBuilder builder, OPP opp) {
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker17;
        quantMarker17 = builder.mark();
        if (this.postcircumfix_108_quant_12(builder, opp)) {
            quantMarker17.drop();
        } else {
            quantMarker17.rollbackTo();
        }
        return true;
    }

    private boolean postcircumfix_108_alt_14(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker16;
        marker16 = builder.mark();
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HASH_INDEX_BRACKET_OPEN) && (tt9.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.postcircumfix_108_quant_13(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
        }
        marker16.done(Perl6ElementTypes.HASH_INDEX);
        return true;
    }

    private boolean postcircumfix_108_quant_15(PsiBuilder builder, OPP opp) {
        String tt12;
        tt12 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.ARRAY_INDEX_BRACKET_CLOSE) && (tt12.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postcircumfix_108_quant_16(PsiBuilder builder, OPP opp) {
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker21;
        quantMarker21 = builder.mark();
        if (this.postcircumfix_108_quant_15(builder, opp)) {
            quantMarker21.drop();
        } else {
            quantMarker21.rollbackTo();
        }
        return true;
    }

    private boolean postcircumfix_108_alt_17(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker20;
        marker20 = builder.mark();
        String tt11;
        tt11 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.ARRAY_INDEX_BRACKET_OPEN) && (tt11.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker22;
        quantMarker22 = builder.mark();
        if (this.postcircumfix_108_quant_16(builder, opp)) {
            quantMarker22.drop();
        } else {
            quantMarker22.rollbackTo();
        }
        marker20.done(Perl6ElementTypes.ARRAY_INDEX);
        return true;
    }

    private boolean postcircumfix_108(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker23;
        altMarker23 = builder.mark();
        if (this.postcircumfix_108_alt_17(builder, opp)) {
            altMarker23.drop();
        } else {
            altMarker23.rollbackTo();
            PsiBuilder.Marker altMarker19;;
            altMarker19 = builder.mark();
            if (this.postcircumfix_108_alt_14(builder, opp)) {
                altMarker19.drop();
            } else {
                altMarker19.rollbackTo();
                PsiBuilder.Marker altMarker15;;
                altMarker15 = builder.mark();
                if (this.postcircumfix_108_alt_11(builder, opp)) {
                    altMarker15.drop();
                } else {
                    altMarker15.rollbackTo();
                    PsiBuilder.Marker altMarker11;;
                    altMarker11 = builder.mark();
                    if (this.postcircumfix_108_alt_8(builder, opp)) {
                        altMarker11.drop();
                    } else {
                        altMarker11.rollbackTo();
                        PsiBuilder.Marker altMarker7;;
                        altMarker7 = builder.mark();
                        if (this.postcircumfix_108_alt_5(builder, opp)) {
                            altMarker7.drop();
                        } else {
                            altMarker7.rollbackTo();
                            PsiBuilder.Marker altMarker3;;
                            altMarker3 = builder.mark();
                            if (this.postcircumfix_108_alt_2(builder, opp)) {
                                altMarker3.drop();
                            } else {
                                altMarker3.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean postfix_109(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.POSTFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) && (tt1.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.postfixish_110_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.postfixish_110_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean postfixish_110_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.postfixish_nometa_111(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_quant_6(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt2.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.postfixish_110_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean postfixish_110_alt_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.HYPER_METAOP_MISSING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.postfixish_nometa_111(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_110_alt_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.postfixish_110_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.postfixish_110_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.postfixish_110_alt_7(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                return false;
            }
        }
        marker5.done(Perl6ElementTypes.HYPER_METAOP);
        return true;
    }

    private boolean postfixish_110(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.postfixish_110_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.postfixish_110_alt_9(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.postfixish_110_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean postfixish_nometa_111_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.privop_114(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_nometa_111_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.dotty_40(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_nometa_111_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_OPERATOR) && (tt1.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.postcircumfix_108(builder))) {
            return false;
        }
        marker3.done(Perl6ElementTypes.METHOD_CALL);
        return true;
    }

    private boolean postfixish_nometa_111_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.postcircumfix_108(builder))) {
            return false;
        }
        return true;
    }

    private boolean postfixish_nometa_111_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker6;
        marker6 = builder.mark();
        if (!(this.postfix_109(builder))) {
            return false;
        }
        marker6.done(Perl6ElementTypes.POSTFIX);
        return true;
    }

    private boolean postfixish_nometa_111_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker8;
        marker8 = builder.mark();
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.POSTFIX) && (tt2.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.postfix_109(builder))) {
            return false;
        }
        marker8.done(Perl6ElementTypes.POSTFIX);
        return true;
    }

    private boolean postfixish_nometa_111(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.postfixish_nometa_111_alt_6(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.postfixish_nometa_111_alt_5(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.postfixish_nometa_111_alt_4(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.postfixish_nometa_111_alt_3(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.postfixish_nometa_111_alt_2(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            PsiBuilder.Marker altMarker1;;
                            altMarker1 = builder.mark();
                            if (this.postfixish_nometa_111_alt_1(builder, opp)) {
                                altMarker1.drop();
                            } else {
                                altMarker1.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean prefix_112_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.PREFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean prefix_112_alt_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PREFIX) && (tt1.equals("not"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.end_prefix_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean prefix_112_alt_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PREFIX) && (tt2.equals("so"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.end_prefix_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean prefix_112_alt_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PREFIX) && (tt3.equals("temp"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean prefix_112_alt_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PREFIX) && (tt4.equals("let"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean prefix_112(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.prefix_112_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.prefix_112_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.prefix_112_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.prefix_112_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.prefix_112_alt_1(builder, opp)) {
                            altMarker1.drop();
                        } else {
                            altMarker1.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean prefixish_113_alt_1(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.prefix_112(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.PREFIX);
        return true;
    }

    private boolean prefixish_113_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        if (!(this.prefix_112(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker3.done(Perl6ElementTypes.HYPER_METAOP);
        return true;
    }

    private boolean prefixish_113_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean prefixish_113(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.prefixish_113_alt_2(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.prefixish_113_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.prefixish_113_quant_3(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean privop_114_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.methodop_71(builder))) {
            return false;
        }
        return true;
    }

    private boolean privop_114(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_OPERATOR) && (tt1.equals("!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.privop_114_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.METHOD_CALL);
        return true;
    }

    private boolean quantified_atom_115_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigmaybe_163(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantified_atom_115_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker3.done(Perl6ElementTypes.REGEX_QUANTIFIER);
        return true;
    }

    private boolean quantified_atom_115_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.quantifier_116(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantified_atom_115_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.SIGOK_3(builder))) {
            return false;
        }
        if (!(this.sigmaybe_163(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantified_atom_115_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.rxws_156(builder))) {
            return false;
        }
        if (!(this.separator_160(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantified_atom_115_quant_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.quantified_atom_115_alt_3(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.quantified_atom_115_alt_2(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.quantified_atom_115_quant_4(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.quantified_atom_115_quant_5(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean quantified_atom_115(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.atom_9(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.quantified_atom_115_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.quantified_atom_115_quant_6(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.REGEX_ATOM);
        return true;
    }

    private boolean quantifier_116_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.normspace_80(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.normspace_80(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_QUANTIFIER_MISSING) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_alt_5(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHATEVER) && (tt2.equals("*"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.integer_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.quantifier_116_alt_6(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.quantifier_116_alt_5(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quantifier_116_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.quantifier_116_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean quantifier_116_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.integer_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.quantifier_116_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean quantifier_116_alt_10(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PREFIX) && (tt3.equals("^"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.integer_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.rxcodeblock_149(builder))) {
            return false;
        }
        return true;
    }

    private boolean quantifier_116_alt_12(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_QUANTIFIER) && (tt1.equals("**"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.quantifier_116_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.quantifier_116_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker altMarker12;
        altMarker12 = builder.mark();
        if (this.quantifier_116_alt_11(builder, opp)) {
            altMarker12.drop();
        } else {
            altMarker12.rollbackTo();
            PsiBuilder.Marker altMarker11;;
            altMarker11 = builder.mark();
            if (this.quantifier_116_alt_10(builder, opp)) {
                altMarker11.drop();
            } else {
                altMarker11.rollbackTo();
                PsiBuilder.Marker altMarker10;;
                altMarker10 = builder.mark();
                if (this.quantifier_116_alt_9(builder, opp)) {
                    altMarker10.drop();
                } else {
                    altMarker10.rollbackTo();
                    PsiBuilder.Marker altMarker5;;
                    altMarker5 = builder.mark();
                    if (this.quantifier_116_alt_4(builder, opp)) {
                        altMarker5.drop();
                    } else {
                        altMarker5.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean quantifier_116(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.quantifier_116_alt_12(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.quantifier_116_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.REGEX_QUANTIFIER);
        return true;
    }

    private boolean quibble_117_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quibble_117_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.quibble_117_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean quibble_117_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_Q_133(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quibble_117_quant_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quibble_117_quant_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quibble_117_quant_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quibble_117_quant_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.quibble_117_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.quibble_117_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.quibble_117_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean quibble_117_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.quibble_117_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.quibble_117_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.quibble_117_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean quibble_117(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.quibble_117_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.quibble_117_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quibble_rx_118_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quibble_rx_118_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.enter_regex_nibbler_45(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.quibble_rx_118_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean quibble_rx_118_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_rx_134(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quibble_rx_118_quant_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quibble_rx_118_quant_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.enter_regex_nibbler_45(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.quibble_rx_118_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean quibble_rx_118_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.quibble_rx_118_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.quibble_rx_118_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.quibble_rx_118_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean quibble_rx_118(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.quibble_rx_118_alt_6(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.quibble_rx_118_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_119_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.quote_qlang_127(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_119_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.quote_tr_131(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_119_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.quote_rxlang_130(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_119_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.quote_quasi_129(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_119(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.quote_119_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.quote_119_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.quote_119_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.quote_119_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean quote_Q_120(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_ESCAPE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.BAD_ESCAPE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_ESCAPE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.BAD_ESCAPE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_ESCAPE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.quote_interpolation_postfix_122(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ESCAPE_FUNCTION) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.quote_escape_121_quant_7(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker8;;
            quantMarker8 = builder.mark();
            if (this.quote_escape_121_quant_7(builder, opp)) {
                quantMarker8.drop();
            } else {
                quantMarker8.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean quote_escape_121_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.quote_interpolation_postfix_122(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ESCAPE_HASH) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.quote_escape_121_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker11;;
            quantMarker11 = builder.mark();
            if (this.quote_escape_121_quant_9(builder, opp)) {
                quantMarker11.drop();
            } else {
                quantMarker11.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean quote_escape_121_quant_11(PsiBuilder builder, OPP opp) {
        if (!(this.quote_interpolation_postfix_122(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_12(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ESCAPE_ARRAY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.quote_escape_121_quant_11(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker14;;
            quantMarker14 = builder.mark();
            if (this.quote_escape_121_quant_11(builder, opp)) {
                quantMarker14.drop();
            } else {
                quantMarker14.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean quote_escape_121_alt_13(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BAD_ESCAPE) && (tt1.equals("$"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_14(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BAD_ESCAPE) && (tt2.equals("$"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_quant_15(PsiBuilder builder, OPP opp) {
        if (!(this.quote_interpolation_postfix_122(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_escape_121_alt_16(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker18;;
            quantMarker18 = builder.mark();
            if (this.quote_escape_121_quant_15(builder, opp)) {
                quantMarker18.drop();
            } else {
                quantMarker18.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean quote_escape_121_alt_17(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ESCAPE_SCALAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker19;
        altMarker19 = builder.mark();
        if (this.quote_escape_121_alt_16(builder, opp)) {
            altMarker19.drop();
        } else {
            altMarker19.rollbackTo();
            PsiBuilder.Marker altMarker17;;
            altMarker17 = builder.mark();
            if (this.quote_escape_121_alt_14(builder, opp)) {
                altMarker17.drop();
            } else {
                altMarker17.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_escape_121_alt_18(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.quote_escape_121_alt_17(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker16;;
            altMarker16 = builder.mark();
            if (this.quote_escape_121_alt_13(builder, opp)) {
                altMarker16.drop();
            } else {
                altMarker16.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_escape_121(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker21;
        altMarker21 = builder.mark();
        if (this.quote_escape_121_alt_18(builder, opp)) {
            altMarker21.drop();
        } else {
            altMarker21.rollbackTo();
            PsiBuilder.Marker altMarker15;;
            altMarker15 = builder.mark();
            if (this.quote_escape_121_alt_12(builder, opp)) {
                altMarker15.drop();
            } else {
                altMarker15.rollbackTo();
                PsiBuilder.Marker altMarker12;;
                altMarker12 = builder.mark();
                if (this.quote_escape_121_alt_10(builder, opp)) {
                    altMarker12.drop();
                } else {
                    altMarker12.rollbackTo();
                    PsiBuilder.Marker altMarker9;;
                    altMarker9 = builder.mark();
                    if (this.quote_escape_121_alt_8(builder, opp)) {
                        altMarker9.drop();
                    } else {
                        altMarker9.rollbackTo();
                        PsiBuilder.Marker altMarker6;;
                        altMarker6 = builder.mark();
                        if (this.quote_escape_121_alt_6(builder, opp)) {
                            altMarker6.drop();
                        } else {
                            altMarker6.rollbackTo();
                            PsiBuilder.Marker altMarker5;;
                            altMarker5 = builder.mark();
                            if (this.quote_escape_121_alt_5(builder, opp)) {
                                altMarker5.drop();
                            } else {
                                altMarker5.rollbackTo();
                                PsiBuilder.Marker altMarker4;;
                                altMarker4 = builder.mark();
                                if (this.quote_escape_121_alt_4(builder, opp)) {
                                    altMarker4.drop();
                                } else {
                                    altMarker4.rollbackTo();
                                    PsiBuilder.Marker altMarker3;;
                                    altMarker3 = builder.mark();
                                    if (this.quote_escape_121_alt_3(builder, opp)) {
                                        altMarker3.drop();
                                    } else {
                                        altMarker3.rollbackTo();
                                        PsiBuilder.Marker altMarker2;;
                                        altMarker2 = builder.mark();
                                        if (this.quote_escape_121_alt_2(builder, opp)) {
                                            altMarker2.drop();
                                        } else {
                                            altMarker2.rollbackTo();
                                            PsiBuilder.Marker altMarker1;;
                                            altMarker1 = builder.mark();
                                            if (this.quote_escape_121_alt_1(builder, opp)) {
                                                altMarker1.drop();
                                            } else {
                                                altMarker1.rollbackTo();
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean quote_interpolation_postfix_122(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.POSTFIX_INTERPOLATIN) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.postfixish_110(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_mod_123(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_MOD) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_mod_Q_124_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quote_mod_Q_124_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quote_mod_Q_124_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quote_mod_Q_124_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quote_mod_Q_124_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quote_mod_Q_124_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quote_mod_Q_124_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.quote_mod_Q_124_alt_6(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.quote_mod_Q_124_alt_5(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                PsiBuilder.Marker altMarker4;;
                altMarker4 = builder.mark();
                if (this.quote_mod_Q_124_alt_4(builder, opp)) {
                    altMarker4.drop();
                } else {
                    altMarker4.rollbackTo();
                    PsiBuilder.Marker altMarker3;;
                    altMarker3 = builder.mark();
                    if (this.quote_mod_Q_124_alt_3(builder, opp)) {
                        altMarker3.drop();
                    } else {
                        altMarker3.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.quote_mod_Q_124_alt_2(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            PsiBuilder.Marker altMarker1;;
                            altMarker1 = builder.mark();
                            if (this.quote_mod_Q_124_alt_1(builder, opp)) {
                                altMarker1.drop();
                            } else {
                                altMarker1.rollbackTo();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean quote_mod_Q_124(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.quote_mod_Q_124_quant_7(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        if (!(this.quote_mod_123(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_nibbler_125_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_CHAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_nibbler_125_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.quote_escape_121(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_nibbler_125_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_nibbler_125_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.quote_nibbler_125_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean quote_nibbler_125_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.quote_nibbler_125_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.quote_nibbler_125_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.quote_nibbler_125_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean quote_nibbler_125(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.quote_nibbler_125_quant_5(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean quote_q_126(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt2.equals("\uFF63"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt1.equals("\uFF62"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_Q_120(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.quote_qlang_127_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt3.equals("\u201D"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.quote_qlang_127_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_6(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt4.equals("\u201E"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.quote_qlang_127_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_7(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt6.equals("\u201D"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt5.equals("\u201C"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.quote_qlang_127_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_9(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt8.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_10(PsiBuilder builder, OPP opp) {
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt7.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.quote_qlang_127_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_11(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_12(PsiBuilder builder, OPP opp) {
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt9.equals("\u2019"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.quote_qlang_127_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_13(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_14(PsiBuilder builder, OPP opp) {
        String tt10;
        tt10 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt10.equals("\u201A"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker14;
        quantMarker14 = builder.mark();
        if (this.quote_qlang_127_quant_13(builder, opp)) {
            quantMarker14.drop();
        } else {
            quantMarker14.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_15(PsiBuilder builder, OPP opp) {
        String tt12;
        tt12 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt12.equals("\u2019"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_16(PsiBuilder builder, OPP opp) {
        String tt11;
        tt11 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt11.equals("\u2018"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker16;
        quantMarker16 = builder.mark();
        if (this.quote_qlang_127_quant_15(builder, opp)) {
            quantMarker16.drop();
        } else {
            quantMarker16.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_17(PsiBuilder builder, OPP opp) {
        String tt14;
        tt14 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt14.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_18(PsiBuilder builder, OPP opp) {
        String tt13;
        tt13 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt13.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.quote_qlang_127_quant_17(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
        }
        return true;
    }

    private boolean quote_qlang_127_quant_19(PsiBuilder builder, OPP opp) {
        if (!(this.quote_mod_Q_124(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_20(PsiBuilder builder, OPP opp) {
        String tt15;
        tt15 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_SYNTAX) && (tt15.equals("q"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker20;
        quantMarker20 = builder.mark();
        if (this.quote_qlang_127_quant_19(builder, opp)) {
            quantMarker20.drop();
        } else {
            quantMarker20.rollbackTo();
        }
        if (!(this.quibble_117(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_quant_21(PsiBuilder builder, OPP opp) {
        if (!(this.quote_mod_Q_124(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_22(PsiBuilder builder, OPP opp) {
        String tt16;
        tt16 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_SYNTAX) && (tt16.equals("qq"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker22;
        quantMarker22 = builder.mark();
        if (this.quote_qlang_127_quant_21(builder, opp)) {
            quantMarker22.drop();
        } else {
            quantMarker22.rollbackTo();
        }
        if (!(this.quibble_117(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_quant_23(PsiBuilder builder, OPP opp) {
        if (!(this.quote_mod_Q_124(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127_alt_24(PsiBuilder builder, OPP opp) {
        String tt17;
        tt17 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_SYNTAX) && (tt17.equals("Q"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker24;
        quantMarker24 = builder.mark();
        if (this.quote_qlang_127_quant_23(builder, opp)) {
            quantMarker24.drop();
        } else {
            quantMarker24.rollbackTo();
        }
        if (!(this.quibble_117(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_qlang_127(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker25;
        altMarker25 = builder.mark();
        if (this.quote_qlang_127_alt_24(builder, opp)) {
            altMarker25.drop();
        } else {
            altMarker25.rollbackTo();
            PsiBuilder.Marker altMarker23;;
            altMarker23 = builder.mark();
            if (this.quote_qlang_127_alt_22(builder, opp)) {
                altMarker23.drop();
            } else {
                altMarker23.rollbackTo();
                PsiBuilder.Marker altMarker21;;
                altMarker21 = builder.mark();
                if (this.quote_qlang_127_alt_20(builder, opp)) {
                    altMarker21.drop();
                } else {
                    altMarker21.rollbackTo();
                    PsiBuilder.Marker altMarker19;;
                    altMarker19 = builder.mark();
                    if (this.quote_qlang_127_alt_18(builder, opp)) {
                        altMarker19.drop();
                    } else {
                        altMarker19.rollbackTo();
                        PsiBuilder.Marker altMarker17;;
                        altMarker17 = builder.mark();
                        if (this.quote_qlang_127_alt_16(builder, opp)) {
                            altMarker17.drop();
                        } else {
                            altMarker17.rollbackTo();
                            PsiBuilder.Marker altMarker15;;
                            altMarker15 = builder.mark();
                            if (this.quote_qlang_127_alt_14(builder, opp)) {
                                altMarker15.drop();
                            } else {
                                altMarker15.rollbackTo();
                                PsiBuilder.Marker altMarker13;;
                                altMarker13 = builder.mark();
                                if (this.quote_qlang_127_alt_12(builder, opp)) {
                                    altMarker13.drop();
                                } else {
                                    altMarker13.rollbackTo();
                                    PsiBuilder.Marker altMarker11;;
                                    altMarker11 = builder.mark();
                                    if (this.quote_qlang_127_alt_10(builder, opp)) {
                                        altMarker11.drop();
                                    } else {
                                        altMarker11.rollbackTo();
                                        PsiBuilder.Marker altMarker9;;
                                        altMarker9 = builder.mark();
                                        if (this.quote_qlang_127_alt_8(builder, opp)) {
                                            altMarker9.drop();
                                        } else {
                                            altMarker9.rollbackTo();
                                            PsiBuilder.Marker altMarker7;;
                                            altMarker7 = builder.mark();
                                            if (this.quote_qlang_127_alt_6(builder, opp)) {
                                                altMarker7.drop();
                                            } else {
                                                altMarker7.rollbackTo();
                                                PsiBuilder.Marker altMarker5;;
                                                altMarker5 = builder.mark();
                                                if (this.quote_qlang_127_alt_4(builder, opp)) {
                                                    altMarker5.drop();
                                                } else {
                                                    altMarker5.rollbackTo();
                                                    PsiBuilder.Marker altMarker3;;
                                                    altMarker3 = builder.mark();
                                                    if (this.quote_qlang_127_alt_2(builder, opp)) {
                                                        altMarker3.drop();
                                                    } else {
                                                        altMarker3.rollbackTo();
                                                        return false;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean quote_qq_128(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_quasi_129_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_quasi_129(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUASI) && (tt1.equals("quasi"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.quote_quasi_129_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.QUASI);
        return true;
    }

    private boolean quote_rxlang_130_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_rx_134(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.quote_rxlang_130_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.quote_rxlang_130_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_4(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt1.equals("s"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.quote_rxlang_130_alt_3(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.quote_rxlang_130_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_rx_134(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.quote_rxlang_130_quant_6(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker9;;
            quantMarker9 = builder.mark();
            if (this.quote_rxlang_130_quant_6(builder, opp)) {
                quantMarker9.drop();
            } else {
                quantMarker9.rollbackTo();
                break;
            }
        }
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_8(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt2.equals("S"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.quote_rxlang_130_alt_7(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.quote_rxlang_130_alt_5(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_quant_10(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_rx_134(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.quote_rxlang_130_quant_10(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker14;;
            quantMarker14 = builder.mark();
            if (this.quote_rxlang_130_quant_10(builder, opp)) {
                quantMarker14.drop();
            } else {
                quantMarker14.rollbackTo();
                break;
            }
        }
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_12(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt3.equals("ss"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker15;
        altMarker15 = builder.mark();
        if (this.quote_rxlang_130_alt_11(builder, opp)) {
            altMarker15.drop();
        } else {
            altMarker15.rollbackTo();
            PsiBuilder.Marker altMarker12;;
            altMarker12 = builder.mark();
            if (this.quote_rxlang_130_alt_9(builder, opp)) {
                altMarker12.drop();
            } else {
                altMarker12.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_quant_14(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_rx_134(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_15(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.quote_rxlang_130_quant_14(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker19;;
            quantMarker19 = builder.mark();
            if (this.quote_rxlang_130_quant_14(builder, opp)) {
                quantMarker19.drop();
            } else {
                quantMarker19.rollbackTo();
                break;
            }
        }
        if (!(this.sibble_161(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_16(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt4.equals("Ss"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.quote_rxlang_130_alt_15(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker17;;
            altMarker17 = builder.mark();
            if (this.quote_rxlang_130_alt_13(builder, opp)) {
                altMarker17.drop();
            } else {
                altMarker17.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_17(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt5.equals("m"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quibble_rx_118(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_18(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt6.equals("ms"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quibble_rx_118(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_19(PsiBuilder builder, OPP opp) {
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt7.equals("rx"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quibble_rx_118(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_20(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.MISSING_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_21(PsiBuilder builder, OPP opp) {
        if (!(this.enter_regex_nibbler_45(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_quant_22(PsiBuilder builder, OPP opp) {
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt9.equals("/"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_rxlang_130_alt_23(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt8.equals("/"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker26;
        altMarker26 = builder.mark();
        if (this.quote_rxlang_130_alt_21(builder, opp)) {
            altMarker26.drop();
        } else {
            altMarker26.rollbackTo();
            PsiBuilder.Marker altMarker25;;
            altMarker25 = builder.mark();
            if (this.quote_rxlang_130_alt_20(builder, opp)) {
                altMarker25.drop();
            } else {
                altMarker25.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker27;
        quantMarker27 = builder.mark();
        if (this.quote_rxlang_130_quant_22(builder, opp)) {
            quantMarker27.drop();
        } else {
            quantMarker27.rollbackTo();
        }
        return true;
    }

    private boolean quote_rxlang_130(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker28;
        altMarker28 = builder.mark();
        if (this.quote_rxlang_130_alt_23(builder, opp)) {
            altMarker28.drop();
        } else {
            altMarker28.rollbackTo();
            PsiBuilder.Marker altMarker24;;
            altMarker24 = builder.mark();
            if (this.quote_rxlang_130_alt_19(builder, opp)) {
                altMarker24.drop();
            } else {
                altMarker24.rollbackTo();
                PsiBuilder.Marker altMarker23;;
                altMarker23 = builder.mark();
                if (this.quote_rxlang_130_alt_18(builder, opp)) {
                    altMarker23.drop();
                } else {
                    altMarker23.rollbackTo();
                    PsiBuilder.Marker altMarker22;;
                    altMarker22 = builder.mark();
                    if (this.quote_rxlang_130_alt_17(builder, opp)) {
                        altMarker22.drop();
                    } else {
                        altMarker22.rollbackTo();
                        PsiBuilder.Marker altMarker21;;
                        altMarker21 = builder.mark();
                        if (this.quote_rxlang_130_alt_16(builder, opp)) {
                            altMarker21.drop();
                        } else {
                            altMarker21.rollbackTo();
                            PsiBuilder.Marker altMarker16;;
                            altMarker16 = builder.mark();
                            if (this.quote_rxlang_130_alt_12(builder, opp)) {
                                altMarker16.drop();
                            } else {
                                altMarker16.rollbackTo();
                                PsiBuilder.Marker altMarker11;;
                                altMarker11 = builder.mark();
                                if (this.quote_rxlang_130_alt_8(builder, opp)) {
                                    altMarker11.drop();
                                } else {
                                    altMarker11.rollbackTo();
                                    PsiBuilder.Marker altMarker6;;
                                    altMarker6 = builder.mark();
                                    if (this.quote_rxlang_130_alt_4(builder, opp)) {
                                        altMarker6.drop();
                                    } else {
                                        altMarker6.rollbackTo();
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.QUOTE_REGEX);
        return true;
    }

    private boolean quote_tr_131_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt1.equals("TR"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_tr_131_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) && (tt2.equals("tr"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quote_tr_131_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.tribble_239(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_tr_131_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.quotepair_rx_134(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_tr_131_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.quote_tr_131_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.quote_tr_131_quant_4(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        if (!(this.tribble_239(builder))) {
            return false;
        }
        return true;
    }

    private boolean quote_tr_131(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.quote_tr_131_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.quote_tr_131_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.quote_tr_131_alt_5(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.quote_tr_131_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.TRANSLITERATION);
        return true;
    }

    private boolean quotepair_132_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.COLON_PAIR_HAS_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.circumfix_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean quotepair_132_alt_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_PAIR) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_PAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.quotepair_132_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean quotepair_132_alt_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_PAIR) && (tt2.equals(":!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_PAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quotepair_132_alt_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.QUOTE_PAIR) && (tt3.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.INTEGER_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_PAIR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean quotepair_132(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.quotepair_132_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.quotepair_132_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.quotepair_132_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    return false;
                }
            }
        }
        marker1.done(Perl6ElementTypes.QUOTE_PAIR);
        return true;
    }

    private boolean quotepair_Q_133_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.quotepair_Q_133_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.quotepair_Q_133_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quotepair_Q_133_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_8(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_9(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_10(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_11(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_12(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_13(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_14(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_15(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_alt_16(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_Q_133_quant_17(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker16;
        altMarker16 = builder.mark();
        if (this.quotepair_Q_133_alt_16(builder, opp)) {
            altMarker16.drop();
        } else {
            altMarker16.rollbackTo();
            PsiBuilder.Marker altMarker15;;
            altMarker15 = builder.mark();
            if (this.quotepair_Q_133_alt_15(builder, opp)) {
                altMarker15.drop();
            } else {
                altMarker15.rollbackTo();
                PsiBuilder.Marker altMarker14;;
                altMarker14 = builder.mark();
                if (this.quotepair_Q_133_alt_14(builder, opp)) {
                    altMarker14.drop();
                } else {
                    altMarker14.rollbackTo();
                    PsiBuilder.Marker altMarker13;;
                    altMarker13 = builder.mark();
                    if (this.quotepair_Q_133_alt_13(builder, opp)) {
                        altMarker13.drop();
                    } else {
                        altMarker13.rollbackTo();
                        PsiBuilder.Marker altMarker12;;
                        altMarker12 = builder.mark();
                        if (this.quotepair_Q_133_alt_12(builder, opp)) {
                            altMarker12.drop();
                        } else {
                            altMarker12.rollbackTo();
                            PsiBuilder.Marker altMarker11;;
                            altMarker11 = builder.mark();
                            if (this.quotepair_Q_133_alt_11(builder, opp)) {
                                altMarker11.drop();
                            } else {
                                altMarker11.rollbackTo();
                                PsiBuilder.Marker altMarker10;;
                                altMarker10 = builder.mark();
                                if (this.quotepair_Q_133_alt_10(builder, opp)) {
                                    altMarker10.drop();
                                } else {
                                    altMarker10.rollbackTo();
                                    PsiBuilder.Marker altMarker9;;
                                    altMarker9 = builder.mark();
                                    if (this.quotepair_Q_133_alt_9(builder, opp)) {
                                        altMarker9.drop();
                                    } else {
                                        altMarker9.rollbackTo();
                                        PsiBuilder.Marker altMarker8;;
                                        altMarker8 = builder.mark();
                                        if (this.quotepair_Q_133_alt_8(builder, opp)) {
                                            altMarker8.drop();
                                        } else {
                                            altMarker8.rollbackTo();
                                            PsiBuilder.Marker altMarker7;;
                                            altMarker7 = builder.mark();
                                            if (this.quotepair_Q_133_alt_7(builder, opp)) {
                                                altMarker7.drop();
                                            } else {
                                                altMarker7.rollbackTo();
                                                PsiBuilder.Marker altMarker6;;
                                                altMarker6 = builder.mark();
                                                if (this.quotepair_Q_133_alt_6(builder, opp)) {
                                                    altMarker6.drop();
                                                } else {
                                                    altMarker6.rollbackTo();
                                                    PsiBuilder.Marker altMarker5;;
                                                    altMarker5 = builder.mark();
                                                    if (this.quotepair_Q_133_alt_5(builder, opp)) {
                                                        altMarker5.drop();
                                                    } else {
                                                        altMarker5.rollbackTo();
                                                        PsiBuilder.Marker altMarker4;;
                                                        altMarker4 = builder.mark();
                                                        if (this.quotepair_Q_133_alt_4(builder, opp)) {
                                                            altMarker4.drop();
                                                        } else {
                                                            altMarker4.rollbackTo();
                                                            PsiBuilder.Marker altMarker3;;
                                                            altMarker3 = builder.mark();
                                                            if (this.quotepair_Q_133_alt_3(builder, opp)) {
                                                                altMarker3.drop();
                                                            } else {
                                                                altMarker3.rollbackTo();
                                                                return false;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean quotepair_Q_133(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker17;
        quantMarker17 = builder.mark();
        if (this.quotepair_Q_133_quant_17(builder, opp)) {
            quantMarker17.drop();
        } else {
            quantMarker17.rollbackTo();
        }
        if (!(this.quotepair_132(builder))) {
            return false;
        }
        return true;
    }

    private boolean quotepair_rx_134_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_rx_134_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean quotepair_rx_134_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.quotepair_rx_134_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.quotepair_rx_134_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean quotepair_rx_134(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.quotepair_rx_134_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.quotepair_132(builder))) {
            return false;
        }
        return true;
    }

    private boolean rad_digit_135_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean rad_digit_135_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean rad_digit_135(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.rad_digit_135_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.rad_digit_135_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean rad_digits_136_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.rad_digit_135(builder))) {
            return false;
        }
        return true;
    }

    private boolean rad_digits_136_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.rad_digit_135(builder))) {
            return false;
        }
        return true;
    }

    private boolean rad_digits_136_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.rad_digits_136_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.rad_digits_136_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean rad_digits_136(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.rad_digits_136_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.rad_digits_136_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.rad_digits_136_quant_3(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean rad_number_137_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_quant_2(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_alt_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt2.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.rad_number_137_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean rad_number_137_quant_4(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt5.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_alt_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt4.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.rad_number_137_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean rad_number_137_alt_6(PsiBuilder builder, OPP opp) {
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt7.equals("0b"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_alt_7(PsiBuilder builder, OPP opp) {
        String tt8;
        tt8 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt8.equals("0d"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_alt_8(PsiBuilder builder, OPP opp) {
        String tt9;
        tt9 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt9.equals("0o"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_alt_9(PsiBuilder builder, OPP opp) {
        String tt10;
        tt10 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt10.equals("0x"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_quant_10(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.rad_number_137_alt_9(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.rad_number_137_alt_8(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.rad_number_137_alt_7(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    PsiBuilder.Marker altMarker7;;
                    altMarker7 = builder.mark();
                    if (this.rad_number_137_alt_6(builder, opp)) {
                        altMarker7.drop();
                    } else {
                        altMarker7.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean rad_number_137_quant_11(PsiBuilder builder, OPP opp) {
        String tt11;
        tt11 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt11.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rad_number_137_alt_12(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt6.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.rad_number_137_quant_10(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.rad_number_137_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean rad_number_137(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.RADIX_NUMBER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.rad_number_137_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.rad_number_137_alt_12(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.rad_number_137_alt_5(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker4;;
                altMarker4 = builder.mark();
                if (this.rad_number_137_alt_3(builder, opp)) {
                    altMarker4.drop();
                } else {
                    altMarker4.rollbackTo();
                    return false;
                }
            }
        }
        marker1.done(Perl6ElementTypes.RADIX_NUMBER);
        return true;
    }

    private boolean radint_138(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.integer_lex_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean rat_number_139(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RAT_LITERAL) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.bare_rat_number_13(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RAT_LITERAL) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.RAT_LITERAL);
        return true;
    }

    private boolean regex_declarator_140_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_DECLARATOR) && (tt1.equals("token"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.regex_def_141(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_declarator_140_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_DECLARATOR) && (tt2.equals("rule"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.regex_def_141(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_declarator_140_alt_3(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_DECLARATOR) && (tt3.equals("regex"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.regex_def_141(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_declarator_140(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.regex_declarator_140_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.regex_declarator_140_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.regex_declarator_140_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        marker1.done(Perl6ElementTypes.REGEX_DECLARATION);
        return true;
    }

    private boolean regex_def_141_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.routine_name_146(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.regex_def_141_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker2.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean regex_def_141_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.MISSING_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.enter_regex_nibbler_45(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ONLY_STAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_quant_9(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE) && (tt4.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_def_141_quant_10(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker7;
        marker7 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.BLOCK_CURLY_BRACKET_OPEN) && (tt3.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.regex_def_141_alt_8(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.regex_def_141_alt_7(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.regex_def_141_alt_6(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    return false;
                }
            }
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.regex_def_141_quant_9(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        marker7.done(Perl6ElementTypes.BLOCKOID);
        return true;
    }

    private boolean regex_def_141(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.regex_def_141_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.regex_def_141_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.regex_def_141_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.regex_def_141_quant_5(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.regex_def_141_quant_10(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean regex_nibbler_142_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt1.equals("&"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_nibbler_142_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt2.equals("&&"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_nibbler_142_alt_3(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt3.equals("|"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_nibbler_142_alt_4(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt4.equals("||"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean regex_nibbler_142_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.regex_nibbler_142_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.regex_nibbler_142_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.regex_nibbler_142_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.regex_nibbler_142_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_nibbler_142_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.termseq_235(builder))) {
            return false;
        }
        return true;
    }

    private boolean regex_nibbler_142(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.regex_nibbler_142_quant_5(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.regex_nibbler_142_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean regex_nibbler_fresh_rx_143(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.regex_nibbler_142(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_declarator_144_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ROUTINE_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.method_def_69(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_declarator_144_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ROUTINE_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.method_def_69(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_declarator_144_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ROUTINE_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.method_def_69(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_declarator_144_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ROUTINE_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.routine_def_145(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_declarator_144(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.routine_declarator_144_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.routine_declarator_144_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.routine_declarator_144_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.routine_declarator_144_alt_1(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        return false;
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.ROUTINE_DECLARATION);
        return true;
    }

    private boolean routine_def_145_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.routine_name_146(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_def_145_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean routine_def_145_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.signature_165(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.routine_def_145_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker2.done(Perl6ElementTypes.SIGNATURE);
        return true;
    }

    private boolean routine_def_145_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_def_145_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_def_145_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean routine_def_145_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.blockoid_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_def_145_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.onlystar_85(builder))) {
            return false;
        }
        return true;
    }

    private boolean routine_def_145(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.routine_def_145_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.routine_def_145_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.routine_def_145_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.routine_def_145_quant_5(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.routine_def_145_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.routine_def_145_alt_7(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                PsiBuilder.Marker altMarker7;;
                altMarker7 = builder.mark();
                if (this.routine_def_145_alt_6(builder, opp)) {
                    altMarker7.drop();
                } else {
                    altMarker7.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean routine_name_146(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.ROUTINE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.LONG_NAME);
        return true;
    }

    private boolean rxQ_147_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt2.equals("\uFF63"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxQ_147(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt1.equals("\uFF62"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_Q_120(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.rxQ_147_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean rxarglist_148(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.arglist_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean rxcodeblock_149(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.pblock_93(builder))) {
            return false;
        }
        return true;
    }

    private boolean rxinfixstopper_150_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean rxinfixstopper_150_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean rxinfixstopper_150_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean rxinfixstopper_150(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.rxinfixstopper_150_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.rxinfixstopper_150_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.rxinfixstopper_150_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean rxq_151_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxq_151_alt_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt1.equals("\u2019"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.rxq_151_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean rxq_151_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxq_151_alt_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt2.equals("\u201A"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.rxq_151_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean rxq_151_quant_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt4.equals("\u2019"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxq_151_alt_6(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt3.equals("\u2018"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.rxq_151_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean rxq_151_quant_7(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt6.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxq_151_alt_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt5.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.rxq_151_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean rxq_151(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.rxq_151_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.rxq_151_alt_6(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.rxq_151_alt_4(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker3;;
                    altMarker3 = builder.mark();
                    if (this.rxq_151_alt_2(builder, opp)) {
                        altMarker3.drop();
                    } else {
                        altMarker3.rollbackTo();
                        return false;
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean rxqq_152_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxqq_152_alt_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt1.equals("\u201D"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.rxqq_152_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean rxqq_152_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxqq_152_alt_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt2.equals("\u201E"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.rxqq_152_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean rxqq_152_quant_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt4.equals("\u201D"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxqq_152_alt_6(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt3.equals("\u201C"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.rxqq_152_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean rxqq_152_quant_7(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt6.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxqq_152_alt_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt5.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_qq_128(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.rxqq_152_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean rxqq_152(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.rxqq_152_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.rxqq_152_alt_6(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.rxqq_152_alt_4(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker3;;
                    altMarker3 = builder.mark();
                    if (this.rxqq_152_alt_2(builder, opp)) {
                        altMarker3.drop();
                    } else {
                        altMarker3.rollbackTo();
                        return false;
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean rxqw_153_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean rxqw_153(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.rxqw_153_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean rxstopper_154(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.stopper_214(builder))) {
            return false;
        }
        return true;
    }

    private boolean rxtermish_155_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.quantified_atom_115(builder))) {
            return false;
        }
        return true;
    }

    private boolean rxtermish_155(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.rxtermish_155_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean rxws_156_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.RX_WHITESPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean rxws_156(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.rxws_156_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean scope_declarator_157_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INCOMPLETE_SCOPED_DECLARATION) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean scope_declarator_157_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.multi_declarator_76(builder))) {
            return false;
        }
        return true;
    }

    private boolean scope_declarator_157_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean scope_declarator_157_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.multi_declarator_76(builder))) {
            return false;
        }
        return true;
    }

    private boolean scope_declarator_157_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.scope_declarator_157_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.scope_declarator_157_quant_3(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.scope_declarator_157_quant_4(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean scope_declarator_157_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.package_declarator_86(builder))) {
            return false;
        }
        return true;
    }

    private boolean scope_declarator_157_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.declarator_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean scope_declarator_157(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.SCOPE_DECLARATOR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.scope_declarator_157_alt_7(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.scope_declarator_157_alt_6(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                PsiBuilder.Marker altMarker7;;
                altMarker7 = builder.mark();
                if (this.scope_declarator_157_alt_5(builder, opp)) {
                    altMarker7.drop();
                } else {
                    altMarker7.rollbackTo();
                    PsiBuilder.Marker altMarker3;;
                    altMarker3 = builder.mark();
                    if (this.scope_declarator_157_alt_2(builder, opp)) {
                        altMarker3.drop();
                    } else {
                        altMarker3.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.scope_declarator_157_alt_1(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.SCOPED_DECLARATION);
        return true;
    }

    private boolean semiarglist_158_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean semiarglist_158(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.arglist_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.semiarglist_158_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean semilist_159_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean semilist_159_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if (!(this.statement_170(builder))) {
            return false;
        }
        if (!(this.eat_terminator_42(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.STATEMENT);
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.semilist_159_quant_1(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean semilist_159_alt_3(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.semilist_159_quant_2(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean semilist_159_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SEMI_LIST_END) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean semilist_159(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.semilist_159_alt_4(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.semilist_159_alt_3(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.SEMI_LIST);
        return true;
    }

    private boolean separator_160_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.quantified_atom_115(builder))) {
            return false;
        }
        return true;
    }

    private boolean separator_160(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_QUANTIFIER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.separator_160_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.REGEX_SEPARATOR);
        return true;
    }

    private boolean sibble_161_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sibble_161_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.quote_nibbler_125(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.sibble_161_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean sibble_161_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker3.done(Perl6ElementTypes.INFIX);
        return true;
    }

    private boolean sibble_161_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.ASSIGN_METAOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_non_assignment_meta_58(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt2.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker5.done(Perl6ElementTypes.ASSIGN_METAOP);
        return true;
    }

    private boolean sibble_161_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean sibble_161_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sibble_161_quant_5(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sibble_161_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.sibble_161_alt_4(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.sibble_161_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sibble_161_quant_6(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sibble_161_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SUBST_ASSIGNISH) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sibble_161_quant_7(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sibble_161_quant_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.sibble_161_alt_8(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sibble_161_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sibble_161_quant_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.sibble_161_quant_9(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        return true;
    }

    private boolean sibble_161(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.enter_regex_nibbler_45(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.sibble_161_quant_10(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean sigil_162(PsiBuilder builder) {
        OPP opp;
        opp = null;
        return true;
    }

    private boolean sigmaybe_163_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.normspace_80(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigmaybe_163_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_SIGSPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.normspace_80(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.REGEX_SIGSPACE);
        return true;
    }

    private boolean sigmaybe_163(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.sigmaybe_163_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.sigmaybe_163_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sign_164_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sign_164_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sign_164_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sign_164_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sign_164(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.sign_164_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.sign_164_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.sign_164_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.sign_164_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean signature_165_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean signature_165_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.parameter_92(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_165_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_PARAMETERS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean signature_165_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.signature_165_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.signature_165_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean signature_165_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.param_sep_89(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.signature_165_quant_4(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean signature_165_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.parameter_92(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.signature_165_quant_5(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean signature_165_alt_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_PARAMETERS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean signature_165_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_165_alt_9(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.MISSING_RETURN_CONSTRAINT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean signature_165_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.value_247(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_165_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_165_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_165_alt_13(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker12;
        altMarker12 = builder.mark();
        if (this.signature_165_alt_11(builder, opp)) {
            altMarker12.drop();
        } else {
            altMarker12.rollbackTo();
            PsiBuilder.Marker altMarker11;;
            altMarker11 = builder.mark();
            if (this.signature_165_alt_10(builder, opp)) {
                altMarker11.drop();
            } else {
                altMarker11.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.signature_165_quant_12(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
        }
        return true;
    }

    private boolean signature_165_quant_14(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker9;
        marker9 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.RETURN_ARROW) && (tt1.equals("-->"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker14;
        altMarker14 = builder.mark();
        if (this.signature_165_alt_13(builder, opp)) {
            altMarker14.drop();
        } else {
            altMarker14.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.signature_165_alt_9(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        marker9.done(Perl6ElementTypes.RETURN_CONSTRAINT);
        return true;
    }

    private boolean signature_165(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.signature_165_alt_7(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.signature_165_alt_6(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.signature_165_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.signature_165_quant_8(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        PsiBuilder.Marker quantMarker15;
        quantMarker15 = builder.mark();
        if (this.signature_165_quant_14(builder, opp)) {
            quantMarker15.drop();
        } else {
            quantMarker15.rollbackTo();
        }
        return true;
    }

    private boolean signed_integer_166(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.RAT_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.integer_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean signed_number_167(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.COMPLEX_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.number_81(builder))) {
            return false;
        }
        return true;
    }

    private boolean spacey_168_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean spacey_168_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean spacey_168(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.spacey_168_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.spacey_168_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean starter_169(PsiBuilder builder) {
        OPP opp;
        opp = null;
        return true;
    }

    private boolean statement_170_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.bogus_statement_19(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_170_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_170_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_170_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.statement_170_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.statement_170_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.EMPTY_STATEMENT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_170_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        if (!(this.statement_mod_loop_194(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_170_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        if (!(this.statement_mod_loop_194(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_170_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        if (!(this.statement_mod_cond_192(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.statement_170_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean statement_170_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.END_OF_STATEMENT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_170_quant_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.statement_170_alt_8(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.statement_170_alt_7(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.statement_170_alt_5(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean statement_170_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.statement_170_quant_9(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean statement_170_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_171(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_170_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.statement_170(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_170_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.label_65(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.statement_170_quant_12(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean statement_170(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.statement_170_alt_13(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker11;;
            altMarker11 = builder.mark();
            if (this.statement_170_alt_11(builder, opp)) {
                altMarker11.drop();
            } else {
                altMarker11.rollbackTo();
                PsiBuilder.Marker altMarker10;;
                altMarker10 = builder.mark();
                if (this.statement_170_alt_10(builder, opp)) {
                    altMarker10.drop();
                } else {
                    altMarker10.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.statement_170_alt_4(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.statement_170_alt_1(builder, opp)) {
                            altMarker1.drop();
                        } else {
                            altMarker1.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean statement_control_171_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_QUIT_174(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_CONTROL_173(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_CATCH_172(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_default_175(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_when_188(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_given_177(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_require_184(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_use_187(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_no_182(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_import_179(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_need_181(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_12(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_loop_180(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_whenever_189(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_14(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_for_176(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_15(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_repeat_183(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_16(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_until_186(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_17(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_while_190(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_18(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_without_191(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_19(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_unless_185(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171_alt_20(PsiBuilder builder, OPP opp) {
        if (!(this.statement_control_if_178(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_171(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.statement_control_171_alt_20(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker19;;
            altMarker19 = builder.mark();
            if (this.statement_control_171_alt_19(builder, opp)) {
                altMarker19.drop();
            } else {
                altMarker19.rollbackTo();
                PsiBuilder.Marker altMarker18;;
                altMarker18 = builder.mark();
                if (this.statement_control_171_alt_18(builder, opp)) {
                    altMarker18.drop();
                } else {
                    altMarker18.rollbackTo();
                    PsiBuilder.Marker altMarker17;;
                    altMarker17 = builder.mark();
                    if (this.statement_control_171_alt_17(builder, opp)) {
                        altMarker17.drop();
                    } else {
                        altMarker17.rollbackTo();
                        PsiBuilder.Marker altMarker16;;
                        altMarker16 = builder.mark();
                        if (this.statement_control_171_alt_16(builder, opp)) {
                            altMarker16.drop();
                        } else {
                            altMarker16.rollbackTo();
                            PsiBuilder.Marker altMarker15;;
                            altMarker15 = builder.mark();
                            if (this.statement_control_171_alt_15(builder, opp)) {
                                altMarker15.drop();
                            } else {
                                altMarker15.rollbackTo();
                                PsiBuilder.Marker altMarker14;;
                                altMarker14 = builder.mark();
                                if (this.statement_control_171_alt_14(builder, opp)) {
                                    altMarker14.drop();
                                } else {
                                    altMarker14.rollbackTo();
                                    PsiBuilder.Marker altMarker13;;
                                    altMarker13 = builder.mark();
                                    if (this.statement_control_171_alt_13(builder, opp)) {
                                        altMarker13.drop();
                                    } else {
                                        altMarker13.rollbackTo();
                                        PsiBuilder.Marker altMarker12;;
                                        altMarker12 = builder.mark();
                                        if (this.statement_control_171_alt_12(builder, opp)) {
                                            altMarker12.drop();
                                        } else {
                                            altMarker12.rollbackTo();
                                            PsiBuilder.Marker altMarker11;;
                                            altMarker11 = builder.mark();
                                            if (this.statement_control_171_alt_11(builder, opp)) {
                                                altMarker11.drop();
                                            } else {
                                                altMarker11.rollbackTo();
                                                PsiBuilder.Marker altMarker10;;
                                                altMarker10 = builder.mark();
                                                if (this.statement_control_171_alt_10(builder, opp)) {
                                                    altMarker10.drop();
                                                } else {
                                                    altMarker10.rollbackTo();
                                                    PsiBuilder.Marker altMarker9;;
                                                    altMarker9 = builder.mark();
                                                    if (this.statement_control_171_alt_9(builder, opp)) {
                                                        altMarker9.drop();
                                                    } else {
                                                        altMarker9.rollbackTo();
                                                        PsiBuilder.Marker altMarker8;;
                                                        altMarker8 = builder.mark();
                                                        if (this.statement_control_171_alt_8(builder, opp)) {
                                                            altMarker8.drop();
                                                        } else {
                                                            altMarker8.rollbackTo();
                                                            PsiBuilder.Marker altMarker7;;
                                                            altMarker7 = builder.mark();
                                                            if (this.statement_control_171_alt_7(builder, opp)) {
                                                                altMarker7.drop();
                                                            } else {
                                                                altMarker7.rollbackTo();
                                                                PsiBuilder.Marker altMarker6;;
                                                                altMarker6 = builder.mark();
                                                                if (this.statement_control_171_alt_6(builder, opp)) {
                                                                    altMarker6.drop();
                                                                } else {
                                                                    altMarker6.rollbackTo();
                                                                    PsiBuilder.Marker altMarker5;;
                                                                    altMarker5 = builder.mark();
                                                                    if (this.statement_control_171_alt_5(builder, opp)) {
                                                                        altMarker5.drop();
                                                                    } else {
                                                                        altMarker5.rollbackTo();
                                                                        PsiBuilder.Marker altMarker4;;
                                                                        altMarker4 = builder.mark();
                                                                        if (this.statement_control_171_alt_4(builder, opp)) {
                                                                            altMarker4.drop();
                                                                        } else {
                                                                            altMarker4.rollbackTo();
                                                                            PsiBuilder.Marker altMarker3;;
                                                                            altMarker3 = builder.mark();
                                                                            if (this.statement_control_171_alt_3(builder, opp)) {
                                                                                altMarker3.drop();
                                                                            } else {
                                                                                altMarker3.rollbackTo();
                                                                                PsiBuilder.Marker altMarker2;;
                                                                                altMarker2 = builder.mark();
                                                                                if (this.statement_control_171_alt_2(builder, opp)) {
                                                                                    altMarker2.drop();
                                                                                } else {
                                                                                    altMarker2.rollbackTo();
                                                                                    PsiBuilder.Marker altMarker1;;
                                                                                    altMarker1 = builder.mark();
                                                                                    if (this.statement_control_171_alt_1(builder, opp)) {
                                                                                        altMarker1.drop();
                                                                                    } else {
                                                                                        altMarker1.rollbackTo();
                                                                                        return false;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean statement_control_CATCH_172_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_CATCH_172(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("CATCH"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_CATCH_172_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.CATCH_STATEMENT);
        return true;
    }

    private boolean statement_control_CONTROL_173_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_CONTROL_173(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("CONTROL"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_CONTROL_173_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.CONTROL_STATEMENT);
        return true;
    }

    private boolean statement_control_QUIT_174_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_QUIT_174(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("QUIT"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_QUIT_174_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.QUIT_STATEMENT);
        return true;
    }

    private boolean statement_control_default_175_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_default_175(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("default"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_default_175_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.DEFAULT_STATEMENT);
        return true;
    }

    private boolean statement_control_for_176_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_for_176(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("for"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_for_176_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.FOR_STATEMENT);
        return true;
    }

    private boolean statement_control_given_177_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_given_177(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("given"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_given_177_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.GIVEN_STATEMENT);
        return true;
    }

    private boolean statement_control_if_178_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("with"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt2.equals("if"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_alt_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt3.equals("orwith"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_alt_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt4.equals("elsif"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.statement_control_if_178_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_if_178_quant_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.statement_control_if_178_alt_5(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.statement_control_if_178_alt_4(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                return false;
            }
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.statement_control_if_178_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_if_178_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_if_178_quant_10(PsiBuilder builder, OPP opp) {
        if (!(this.pblock_93(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.statement_control_if_178_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_if_178_quant_11(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt5.equals("else"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.statement_control_if_178_quant_10(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_if_178_quant_12(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker9;;
            quantMarker9 = builder.mark();
            if (this.statement_control_if_178_quant_8(builder, opp)) {
                quantMarker9.drop();
            } else {
                quantMarker9.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.statement_control_if_178_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_if_178_quant_13(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.statement_control_if_178_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.statement_control_if_178_quant_12(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_if_178(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.statement_control_if_178_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.statement_control_if_178_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker14;
        quantMarker14 = builder.mark();
        if (this.statement_control_if_178_quant_13(builder, opp)) {
            quantMarker14.drop();
        } else {
            quantMarker14.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.IF_STATEMENT);
        return true;
    }

    private boolean statement_control_import_179_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.spacey_168(builder))) {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_import_179_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_import_179_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.module_name_74(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_import_179_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.statement_control_import_179_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_import_179(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("import"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.statement_control_import_179_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.IMPORT_STATEMENT);
        return true;
    }

    private boolean statement_control_loop_180_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_5(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_TERMINATOR) && (tt4.equals(";"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.statement_control_loop_180_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.statement_control_loop_180_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.statement_control_loop_180_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_7(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_TERMINATOR) && (tt3.equals(";"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.statement_control_loop_180_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.statement_control_loop_180_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_CLOSE) && (tt5.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.statement_control_loop_180_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.statement_control_loop_180_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_10(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PARENTHESES_OPEN) && (tt2.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_loop_180_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.statement_control_loop_180_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_loop_180_quant_11(PsiBuilder builder, OPP opp) {
        if (!(this.block_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_loop_180(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("loop"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.statement_control_loop_180_quant_10(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.statement_control_loop_180_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.LOOP_STATEMENT);
        return true;
    }

    private boolean statement_control_need_181_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.module_name_74(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_need_181_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.version_250(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_need_181_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.module_name_74(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_need_181_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.version_250(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_need_181_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.statement_control_need_181_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.statement_control_need_181_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean statement_control_need_181_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_need_181_quant_7(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.INFIX) && (tt2.equals(","))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.statement_control_need_181_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.statement_control_need_181_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_need_181_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker8;;
            quantMarker8 = builder.mark();
            if (this.statement_control_need_181_quant_7(builder, opp)) {
                quantMarker8.drop();
            } else {
                quantMarker8.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean statement_control_need_181_quant_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.statement_control_need_181_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.statement_control_need_181_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.statement_control_need_181_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_need_181(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("need"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.statement_control_need_181_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.NEED_STATEMENT);
        return true;
    }

    private boolean statement_control_no_182_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.spacey_168(builder))) {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_no_182_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_no_182_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.module_name_74(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_no_182_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.statement_control_no_182_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_no_182(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("no"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.statement_control_no_182_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.NO_STATEMENT);
        return true;
    }

    private boolean statement_control_repeat_183_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_control_repeat_183_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt2.equals("until"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_alt_3(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt3.equals("while"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_quant_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.statement_control_repeat_183_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.statement_control_repeat_183_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.statement_control_repeat_183_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.statement_control_repeat_183_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_repeat_183_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.statement_control_repeat_183_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_repeat_183_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.pblock_93(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.statement_control_repeat_183_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_repeat_183_alt_9(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt4.equals("until"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_alt_10(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt5.equals("while"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_quant_11(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_repeat_183_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.statement_control_repeat_183_quant_11(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_repeat_183_alt_13(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.statement_control_repeat_183_alt_10(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.statement_control_repeat_183_alt_9(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.statement_control_repeat_183_quant_12(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_repeat_183(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("repeat"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker14;
        altMarker14 = builder.mark();
        if (this.statement_control_repeat_183_alt_13(builder, opp)) {
            altMarker14.drop();
        } else {
            altMarker14.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.statement_control_repeat_183_alt_8(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.statement_control_repeat_183_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        marker1.done(Perl6ElementTypes.REPEAT_STATEMENT);
        return true;
    }

    private boolean statement_control_require_184_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_require_184_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_require_184_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.module_name_74(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_require_184_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_require_184_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.statement_control_require_184_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_require_184_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_require_184_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.statement_control_require_184_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.statement_control_require_184_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.statement_control_require_184_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.statement_control_require_184_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.statement_control_require_184_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_require_184(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("require"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.statement_control_require_184_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.REQUIRE_STATEMENT);
        return true;
    }

    private boolean statement_control_unless_185_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_unless_185(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("unless"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_unless_185_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.UNLESS_STATEMENT);
        return true;
    }

    private boolean statement_control_until_186_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_until_186(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("until"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_until_186_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.UNTIL_STATEMENT);
        return true;
    }

    private boolean statement_control_use_187_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.spacey_168(builder))) {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_use_187_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.module_name_74(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_use_187_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean statement_control_use_187_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.version_250(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_use_187_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.statement_control_use_187_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.statement_control_use_187_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean statement_control_use_187_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_use_187(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("use"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.statement_control_use_187_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.statement_control_use_187_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.USE_STATEMENT);
        return true;
    }

    private boolean statement_control_when_188_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_when_188(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("when"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_when_188_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.WHEN_STATEMENT);
        return true;
    }

    private boolean statement_control_whenever_189_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_whenever_189(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("whenever"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_whenever_189_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.WHENEVER_STATEMENT);
        return true;
    }

    private boolean statement_control_while_190_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_while_190(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("while"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_while_190_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.WHILE_STATEMENT);
        return true;
    }

    private boolean statement_control_without_191_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.xblock_254(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_control_without_191(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_CONTROL) && (tt1.equals("without"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_control_without_191_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.WITHOUT_STATEMENT);
        return true;
    }

    private boolean statement_mod_cond_192_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_mod_cond_192(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_MOD_COND) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_mod_cond_192_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.STATEMENT_MOD_COND);
        return true;
    }

    private boolean statement_mod_cond_keyword_193_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_cond_keyword_193_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_cond_keyword_193_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_cond_keyword_193_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_cond_keyword_193_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_cond_keyword_193(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.statement_mod_cond_keyword_193_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.statement_mod_cond_keyword_193_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.statement_mod_cond_keyword_193_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.statement_mod_cond_keyword_193_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.statement_mod_cond_keyword_193_alt_1(builder, opp)) {
                            altMarker1.drop();
                        } else {
                            altMarker1.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean statement_mod_loop_194_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_mod_loop_194(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_MOD_LOOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_mod_loop_194_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.STATEMENT_MOD_LOOP);
        return true;
    }

    private boolean statement_mod_loop_keyword_195_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_loop_keyword_195_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_loop_keyword_195_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_loop_keyword_195_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statement_mod_loop_keyword_195(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.statement_mod_loop_keyword_195_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.statement_mod_loop_keyword_195_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.statement_mod_loop_keyword_195_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.statement_mod_loop_keyword_195_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean statement_prefix_196_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_do_198(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_react_207(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_supply_210(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_start_209(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_once_203(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_gather_200(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_quietly_205(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_try_211(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_sink_208(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_eager_199(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_lazy_202(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_12(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_hyper_201(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_race_206(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_14(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_phaser_204(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196_alt_15(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_DOC_197(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_196(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker15;
        altMarker15 = builder.mark();
        if (this.statement_prefix_196_alt_15(builder, opp)) {
            altMarker15.drop();
        } else {
            altMarker15.rollbackTo();
            PsiBuilder.Marker altMarker14;;
            altMarker14 = builder.mark();
            if (this.statement_prefix_196_alt_14(builder, opp)) {
                altMarker14.drop();
            } else {
                altMarker14.rollbackTo();
                PsiBuilder.Marker altMarker13;;
                altMarker13 = builder.mark();
                if (this.statement_prefix_196_alt_13(builder, opp)) {
                    altMarker13.drop();
                } else {
                    altMarker13.rollbackTo();
                    PsiBuilder.Marker altMarker12;;
                    altMarker12 = builder.mark();
                    if (this.statement_prefix_196_alt_12(builder, opp)) {
                        altMarker12.drop();
                    } else {
                        altMarker12.rollbackTo();
                        PsiBuilder.Marker altMarker11;;
                        altMarker11 = builder.mark();
                        if (this.statement_prefix_196_alt_11(builder, opp)) {
                            altMarker11.drop();
                        } else {
                            altMarker11.rollbackTo();
                            PsiBuilder.Marker altMarker10;;
                            altMarker10 = builder.mark();
                            if (this.statement_prefix_196_alt_10(builder, opp)) {
                                altMarker10.drop();
                            } else {
                                altMarker10.rollbackTo();
                                PsiBuilder.Marker altMarker9;;
                                altMarker9 = builder.mark();
                                if (this.statement_prefix_196_alt_9(builder, opp)) {
                                    altMarker9.drop();
                                } else {
                                    altMarker9.rollbackTo();
                                    PsiBuilder.Marker altMarker8;;
                                    altMarker8 = builder.mark();
                                    if (this.statement_prefix_196_alt_8(builder, opp)) {
                                        altMarker8.drop();
                                    } else {
                                        altMarker8.rollbackTo();
                                        PsiBuilder.Marker altMarker7;;
                                        altMarker7 = builder.mark();
                                        if (this.statement_prefix_196_alt_7(builder, opp)) {
                                            altMarker7.drop();
                                        } else {
                                            altMarker7.rollbackTo();
                                            PsiBuilder.Marker altMarker6;;
                                            altMarker6 = builder.mark();
                                            if (this.statement_prefix_196_alt_6(builder, opp)) {
                                                altMarker6.drop();
                                            } else {
                                                altMarker6.rollbackTo();
                                                PsiBuilder.Marker altMarker5;;
                                                altMarker5 = builder.mark();
                                                if (this.statement_prefix_196_alt_5(builder, opp)) {
                                                    altMarker5.drop();
                                                } else {
                                                    altMarker5.rollbackTo();
                                                    PsiBuilder.Marker altMarker4;;
                                                    altMarker4 = builder.mark();
                                                    if (this.statement_prefix_196_alt_4(builder, opp)) {
                                                        altMarker4.drop();
                                                    } else {
                                                        altMarker4.rollbackTo();
                                                        PsiBuilder.Marker altMarker3;;
                                                        altMarker3 = builder.mark();
                                                        if (this.statement_prefix_196_alt_3(builder, opp)) {
                                                            altMarker3.drop();
                                                        } else {
                                                            altMarker3.rollbackTo();
                                                            PsiBuilder.Marker altMarker2;;
                                                            altMarker2 = builder.mark();
                                                            if (this.statement_prefix_196_alt_2(builder, opp)) {
                                                                altMarker2.drop();
                                                            } else {
                                                                altMarker2.rollbackTo();
                                                                PsiBuilder.Marker altMarker1;;
                                                                altMarker1 = builder.mark();
                                                                if (this.statement_prefix_196_alt_1(builder, opp)) {
                                                                    altMarker1.drop();
                                                                } else {
                                                                    altMarker1.rollbackTo();
                                                                    return false;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean statement_prefix_DOC_197_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_196(builder))) {
            return false;
        }
        return true;
    }

    private boolean statement_prefix_DOC_197(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.PHASER) && (tt1.equals("DOC"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.statement_prefix_DOC_197_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.PHASER);
        return true;
    }

    private boolean statement_prefix_do_198(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("do"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.DO);
        return true;
    }

    private boolean statement_prefix_eager_199(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("eager"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.EAGER);
        return true;
    }

    private boolean statement_prefix_gather_200(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("gather"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.GATHER);
        return true;
    }

    private boolean statement_prefix_hyper_201(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("hyper"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.HYPER);
        return true;
    }

    private boolean statement_prefix_lazy_202(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("lazy"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.LAZY);
        return true;
    }

    private boolean statement_prefix_once_203(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("once"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.ONCE);
        return true;
    }

    private boolean statement_prefix_phaser_204(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.PHASER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.PHASER);
        return true;
    }

    private boolean statement_prefix_quietly_205(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("quietly"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.QUIETLY);
        return true;
    }

    private boolean statement_prefix_race_206(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("race"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.RACE);
        return true;
    }

    private boolean statement_prefix_react_207(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("react"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.REACT);
        return true;
    }

    private boolean statement_prefix_sink_208(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("sink"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.SINK);
        return true;
    }

    private boolean statement_prefix_start_209(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("start"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.START);
        return true;
    }

    private boolean statement_prefix_supply_210(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("supply"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.SUPPLY);
        return true;
    }

    private boolean statement_prefix_try_211(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STATEMENT_PREFIX) && (tt1.equals("try"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        if (!(this.blorst_17(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.TRY);
        return true;
    }

    private boolean statementlist_212_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean statementlist_212_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statementlist_212_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean statementlist_212_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        if (!(this.statement_170(builder))) {
            return false;
        }
        if (!(this.eat_terminator_42(builder))) {
            return false;
        }
        marker4.done(Perl6ElementTypes.STATEMENT);
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.statementlist_212_quant_3(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean statementlist_212(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.statementlist_212_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.statementlist_212_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.statementlist_212_quant_4(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        marker3.done(Perl6ElementTypes.STATEMENT_LIST);
        return true;
    }

    private boolean stdstopper_213_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stdstopper_213_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stdstopper_213_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stdstopper_213(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.stdstopper_213_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.stdstopper_213_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.stdstopper_213_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean stopper_214_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stopper_214_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stopper_214_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.stopper_214_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.stopper_214_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean stopper_214_quant_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stopper_214_quant_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stopper_214_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stopper_214_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean stopper_214_quant_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.stopper_214_alt_7(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.stopper_214_alt_6(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean stopper_214_alt_9(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.stopper_214_quant_4(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.stopper_214_quant_5(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.stopper_214_quant_8(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean stopper_214(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.stopper_214_alt_9(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.stopper_214_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean term_215_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.capterm_20(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.term_name_219(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.term_type_const_227(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.term_hyperwhatever_217(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.term_whatever_228(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.term_rand_222(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.term_empty_set_216(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.term_time_226(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.term_now_220(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.pblock_93(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.dotty_40(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_12(PsiBuilder builder, OPP opp) {
        if (!(this.term_stub_code_225(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.circumfix_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_14(PsiBuilder builder, OPP opp) {
        if (!(this.term_reduce_223(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_15(PsiBuilder builder, OPP opp) {
        if (!(this.term_onlystar_221(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_16(PsiBuilder builder, OPP opp) {
        if (!(this.package_declarator_86(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_17(PsiBuilder builder, OPP opp) {
        if (!(this.statement_prefix_196(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_18(PsiBuilder builder, OPP opp) {
        if (!(this.type_declarator_243(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_19(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TERM_IS_MULTI) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.multi_declarator_76(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_20(PsiBuilder builder, OPP opp) {
        if (!(this.regex_declarator_140(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_21(PsiBuilder builder, OPP opp) {
        if (!(this.routine_declarator_144(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_22(PsiBuilder builder, OPP opp) {
        if (!(this.scope_declarator_157(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_23(PsiBuilder builder, OPP opp) {
        if (!(this.term_ident_218(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_24(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_25(PsiBuilder builder, OPP opp) {
        if (!(this.term_self_224(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_26(PsiBuilder builder, OPP opp) {
        if (!(this.colonpair_29(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_27(PsiBuilder builder, OPP opp) {
        if (!(this.fatarrow_47(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215_alt_28(PsiBuilder builder, OPP opp) {
        if (!(this.value_247(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_215(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker28;
        altMarker28 = builder.mark();
        if (this.term_215_alt_28(builder, opp)) {
            altMarker28.drop();
        } else {
            altMarker28.rollbackTo();
            PsiBuilder.Marker altMarker27;;
            altMarker27 = builder.mark();
            if (this.term_215_alt_27(builder, opp)) {
                altMarker27.drop();
            } else {
                altMarker27.rollbackTo();
                PsiBuilder.Marker altMarker26;;
                altMarker26 = builder.mark();
                if (this.term_215_alt_26(builder, opp)) {
                    altMarker26.drop();
                } else {
                    altMarker26.rollbackTo();
                    PsiBuilder.Marker altMarker25;;
                    altMarker25 = builder.mark();
                    if (this.term_215_alt_25(builder, opp)) {
                        altMarker25.drop();
                    } else {
                        altMarker25.rollbackTo();
                        PsiBuilder.Marker altMarker24;;
                        altMarker24 = builder.mark();
                        if (this.term_215_alt_24(builder, opp)) {
                            altMarker24.drop();
                        } else {
                            altMarker24.rollbackTo();
                            PsiBuilder.Marker altMarker23;;
                            altMarker23 = builder.mark();
                            if (this.term_215_alt_23(builder, opp)) {
                                altMarker23.drop();
                            } else {
                                altMarker23.rollbackTo();
                                PsiBuilder.Marker altMarker22;;
                                altMarker22 = builder.mark();
                                if (this.term_215_alt_22(builder, opp)) {
                                    altMarker22.drop();
                                } else {
                                    altMarker22.rollbackTo();
                                    PsiBuilder.Marker altMarker21;;
                                    altMarker21 = builder.mark();
                                    if (this.term_215_alt_21(builder, opp)) {
                                        altMarker21.drop();
                                    } else {
                                        altMarker21.rollbackTo();
                                        PsiBuilder.Marker altMarker20;;
                                        altMarker20 = builder.mark();
                                        if (this.term_215_alt_20(builder, opp)) {
                                            altMarker20.drop();
                                        } else {
                                            altMarker20.rollbackTo();
                                            PsiBuilder.Marker altMarker19;;
                                            altMarker19 = builder.mark();
                                            if (this.term_215_alt_19(builder, opp)) {
                                                altMarker19.drop();
                                            } else {
                                                altMarker19.rollbackTo();
                                                PsiBuilder.Marker altMarker18;;
                                                altMarker18 = builder.mark();
                                                if (this.term_215_alt_18(builder, opp)) {
                                                    altMarker18.drop();
                                                } else {
                                                    altMarker18.rollbackTo();
                                                    PsiBuilder.Marker altMarker17;;
                                                    altMarker17 = builder.mark();
                                                    if (this.term_215_alt_17(builder, opp)) {
                                                        altMarker17.drop();
                                                    } else {
                                                        altMarker17.rollbackTo();
                                                        PsiBuilder.Marker altMarker16;;
                                                        altMarker16 = builder.mark();
                                                        if (this.term_215_alt_16(builder, opp)) {
                                                            altMarker16.drop();
                                                        } else {
                                                            altMarker16.rollbackTo();
                                                            PsiBuilder.Marker altMarker15;;
                                                            altMarker15 = builder.mark();
                                                            if (this.term_215_alt_15(builder, opp)) {
                                                                altMarker15.drop();
                                                            } else {
                                                                altMarker15.rollbackTo();
                                                                PsiBuilder.Marker altMarker14;;
                                                                altMarker14 = builder.mark();
                                                                if (this.term_215_alt_14(builder, opp)) {
                                                                    altMarker14.drop();
                                                                } else {
                                                                    altMarker14.rollbackTo();
                                                                    PsiBuilder.Marker altMarker13;;
                                                                    altMarker13 = builder.mark();
                                                                    if (this.term_215_alt_13(builder, opp)) {
                                                                        altMarker13.drop();
                                                                    } else {
                                                                        altMarker13.rollbackTo();
                                                                        PsiBuilder.Marker altMarker12;;
                                                                        altMarker12 = builder.mark();
                                                                        if (this.term_215_alt_12(builder, opp)) {
                                                                            altMarker12.drop();
                                                                        } else {
                                                                            altMarker12.rollbackTo();
                                                                            PsiBuilder.Marker altMarker11;;
                                                                            altMarker11 = builder.mark();
                                                                            if (this.term_215_alt_11(builder, opp)) {
                                                                                altMarker11.drop();
                                                                            } else {
                                                                                altMarker11.rollbackTo();
                                                                                PsiBuilder.Marker altMarker10;;
                                                                                altMarker10 = builder.mark();
                                                                                if (this.term_215_alt_10(builder, opp)) {
                                                                                    altMarker10.drop();
                                                                                } else {
                                                                                    altMarker10.rollbackTo();
                                                                                    PsiBuilder.Marker altMarker9;;
                                                                                    altMarker9 = builder.mark();
                                                                                    if (this.term_215_alt_9(builder, opp)) {
                                                                                        altMarker9.drop();
                                                                                    } else {
                                                                                        altMarker9.rollbackTo();
                                                                                        PsiBuilder.Marker altMarker8;;
                                                                                        altMarker8 = builder.mark();
                                                                                        if (this.term_215_alt_8(builder, opp)) {
                                                                                            altMarker8.drop();
                                                                                        } else {
                                                                                            altMarker8.rollbackTo();
                                                                                            PsiBuilder.Marker altMarker7;;
                                                                                            altMarker7 = builder.mark();
                                                                                            if (this.term_215_alt_7(builder, opp)) {
                                                                                                altMarker7.drop();
                                                                                            } else {
                                                                                                altMarker7.rollbackTo();
                                                                                                PsiBuilder.Marker altMarker6;;
                                                                                                altMarker6 = builder.mark();
                                                                                                if (this.term_215_alt_6(builder, opp)) {
                                                                                                    altMarker6.drop();
                                                                                                } else {
                                                                                                    altMarker6.rollbackTo();
                                                                                                    PsiBuilder.Marker altMarker5;;
                                                                                                    altMarker5 = builder.mark();
                                                                                                    if (this.term_215_alt_5(builder, opp)) {
                                                                                                        altMarker5.drop();
                                                                                                    } else {
                                                                                                        altMarker5.rollbackTo();
                                                                                                        PsiBuilder.Marker altMarker4;;
                                                                                                        altMarker4 = builder.mark();
                                                                                                        if (this.term_215_alt_4(builder, opp)) {
                                                                                                            altMarker4.drop();
                                                                                                        } else {
                                                                                                            altMarker4.rollbackTo();
                                                                                                            PsiBuilder.Marker altMarker3;;
                                                                                                            altMarker3 = builder.mark();
                                                                                                            if (this.term_215_alt_3(builder, opp)) {
                                                                                                                altMarker3.drop();
                                                                                                            } else {
                                                                                                                altMarker3.rollbackTo();
                                                                                                                PsiBuilder.Marker altMarker2;;
                                                                                                                altMarker2 = builder.mark();
                                                                                                                if (this.term_215_alt_2(builder, opp)) {
                                                                                                                    altMarker2.drop();
                                                                                                                } else {
                                                                                                                    altMarker2.rollbackTo();
                                                                                                                    PsiBuilder.Marker altMarker1;;
                                                                                                                    altMarker1 = builder.mark();
                                                                                                                    if (this.term_215_alt_1(builder, opp)) {
                                                                                                                        altMarker1.drop();
                                                                                                                    } else {
                                                                                                                        altMarker1.rollbackTo();
                                                                                                                        return false;
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean term_empty_set_216(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM) && (tt1.equals("\u2205"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.TERM);
        return true;
    }

    private boolean term_hyperwhatever_217(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.HYPER_WHATEVER) && (tt1.equals("*"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.HYPER_WHATEVER);
        return true;
    }

    private boolean term_ident_218_quant_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) && (tt1.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_ident_218(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.SUB_CALL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker2.done(Perl6ElementTypes.SUB_CALL_NAME);
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.term_ident_218_quant_1(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.args_7(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.SUB_CALL);
        return true;
    }

    private boolean term_name_219_quant_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) && (tt1.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_name_219_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        PsiBuilder.Marker marker3;
        marker3 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.SUB_CALL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker3.done(Perl6ElementTypes.SUB_CALL_NAME);
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.LONG_NAME);
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.term_name_219_quant_1(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        if (!(this.args_7(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.SUB_CALL);
        return true;
    }

    private boolean term_name_219_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker6;
        marker6 = builder.mark();
        PsiBuilder.Marker marker7;
        marker7 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker7.done(Perl6ElementTypes.LONG_NAME);
        marker6.done(Perl6ElementTypes.TYPE_NAME);
        return true;
    }

    private boolean term_name_219(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.term_name_219_alt_3(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.term_name_219_alt_2(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean term_now_220(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM) && (tt1.equals("now"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.tok_236(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.TERM);
        return true;
    }

    private boolean term_onlystar_221(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.ONLY_STAR) && (tt1.equals("{*}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.ONLY_STAR);
        return true;
    }

    private boolean term_rand_222(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM) && (tt1.equals("rand"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.end_keyword_43(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.TERM);
        return true;
    }

    private boolean term_reduce_223_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.infixish_57(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_reduce_223_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt2.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.infixish_57(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_reduce_223(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt1.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.term_reduce_223_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.term_reduce_223_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METAOP) && (tt3.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.args_7(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.REDUCE_METAOP);
        return true;
    }

    private boolean term_self_224(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SELF) && (tt1.equals("self"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.SELF);
        if (!(this.end_keyword_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_stub_code_225_alt_1(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STUB_CODE) && (tt1.equals("!!!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_stub_code_225_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STUB_CODE) && (tt2.equals("???"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_stub_code_225_alt_3(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STUB_CODE) && (tt3.equals("..."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_stub_code_225_alt_4(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.STUB_CODE) && (tt4.equals("\u2026"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_stub_code_225(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.term_stub_code_225_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.term_stub_code_225_alt_3(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.term_stub_code_225_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.term_stub_code_225_alt_1(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        return false;
                    }
                }
            }
        }
        if (!(this.args_7(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.STUB_CODE);
        return true;
    }

    private boolean term_time_226(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM) && (tt1.equals("time"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.tok_236(builder))) {
            return false;
        }
        marker1.done(Perl6ElementTypes.TERM);
        return true;
    }

    private boolean term_type_const_227(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.TYPE_CONST) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.TYPE_NAME);
        return true;
    }

    private boolean term_whatever_228(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHATEVER) && (tt1.equals("*"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.WHATEVER);
        return true;
    }

    private boolean termalt_229_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MISSING_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean termalt_229_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.termconj_231(builder))) {
            return false;
        }
        return true;
    }

    private boolean termalt_229_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.termalt_229_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.termalt_229_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean termalt_229(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.termconj_231(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.termalt_229_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean termaltseq_230_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MISSING_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean termaltseq_230_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.termconjseq_232(builder))) {
            return false;
        }
        return true;
    }

    private boolean termaltseq_230_quant_3(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt1.equals("||"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.termaltseq_230_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.termaltseq_230_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean termaltseq_230(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.termconjseq_232(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.termaltseq_230_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean termconj_231_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MISSING_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean termconj_231_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.rxtermish_155(builder))) {
            return false;
        }
        return true;
    }

    private boolean termconj_231_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.termconj_231_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.termconj_231_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean termconj_231(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.rxtermish_155(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.termconj_231_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean termconjseq_232_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_MISSING_TERM) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean termconjseq_232_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.termalt_229(builder))) {
            return false;
        }
        return true;
    }

    private boolean termconjseq_232_quant_3(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_INFIX) && (tt1.equals("&&"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.rxws_156(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.termconjseq_232_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.termconjseq_232_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean termconjseq_232(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.termalt_229(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.termconjseq_232_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean terminator_233_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_5(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_6(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_7(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_8(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_9(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_10(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.terminator_233_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.terminator_233_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.terminator_233_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    PsiBuilder.Marker altMarker7;;
                    altMarker7 = builder.mark();
                    if (this.terminator_233_alt_7(builder, opp)) {
                        altMarker7.drop();
                    } else {
                        altMarker7.rollbackTo();
                        PsiBuilder.Marker altMarker6;;
                        altMarker6 = builder.mark();
                        if (this.terminator_233_alt_6(builder, opp)) {
                            altMarker6.drop();
                        } else {
                            altMarker6.rollbackTo();
                            PsiBuilder.Marker altMarker5;;
                            altMarker5 = builder.mark();
                            if (this.terminator_233_alt_5(builder, opp)) {
                                altMarker5.drop();
                            } else {
                                altMarker5.rollbackTo();
                                PsiBuilder.Marker altMarker4;;
                                altMarker4 = builder.mark();
                                if (this.terminator_233_alt_4(builder, opp)) {
                                    altMarker4.drop();
                                } else {
                                    altMarker4.rollbackTo();
                                    PsiBuilder.Marker altMarker3;;
                                    altMarker3 = builder.mark();
                                    if (this.terminator_233_alt_3(builder, opp)) {
                                        altMarker3.drop();
                                    } else {
                                        altMarker3.rollbackTo();
                                        PsiBuilder.Marker altMarker2;;
                                        altMarker2 = builder.mark();
                                        if (this.terminator_233_alt_2(builder, opp)) {
                                            altMarker2.drop();
                                        } else {
                                            altMarker2.rollbackTo();
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean terminator_233_alt_12(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233_alt_13(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean terminator_233(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.terminator_233_alt_13(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker12;;
            altMarker12 = builder.mark();
            if (this.terminator_233_alt_12(builder, opp)) {
                altMarker12.drop();
            } else {
                altMarker12.rollbackTo();
                PsiBuilder.Marker altMarker11;;
                altMarker11 = builder.mark();
                if (this.terminator_233_alt_11(builder, opp)) {
                    altMarker11.drop();
                } else {
                    altMarker11.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.terminator_233_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean termish_234(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean termseq_235(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.termaltseq_230(builder))) {
            return false;
        }
        return true;
    }

    private boolean tok_236(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.end_keyword_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_237(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.trait_mod_238(builder))) {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_3(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt1.equals("handles"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.trait_mod_238_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.trait_mod_238_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_6(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt2.equals("returns"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.trait_mod_238_alt_5(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.trait_mod_238_alt_4(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_9(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt3.equals("of"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker9;
        altMarker9 = builder.mark();
        if (this.trait_mod_238_alt_8(builder, opp)) {
            altMarker9.drop();
        } else {
            altMarker9.rollbackTo();
            PsiBuilder.Marker altMarker8;;
            altMarker8 = builder.mark();
            if (this.trait_mod_238_alt_7(builder, opp)) {
                altMarker8.drop();
            } else {
                altMarker8.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_11(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_12(PsiBuilder builder, OPP opp) {
        if (!(this.pblock_93(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_13(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.trait_mod_238_alt_12(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker12;;
            altMarker12 = builder.mark();
            if (this.trait_mod_238_alt_11(builder, opp)) {
                altMarker12.drop();
            } else {
                altMarker12.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_14(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt4.equals("will"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker14;
        altMarker14 = builder.mark();
        if (this.trait_mod_238_alt_13(builder, opp)) {
            altMarker14.drop();
        } else {
            altMarker14.rollbackTo();
            PsiBuilder.Marker altMarker11;;
            altMarker11 = builder.mark();
            if (this.trait_mod_238_alt_10(builder, opp)) {
                altMarker11.drop();
            } else {
                altMarker11.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_15(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_16(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_17(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt5.equals("does"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker17;
        altMarker17 = builder.mark();
        if (this.trait_mod_238_alt_16(builder, opp)) {
            altMarker17.drop();
        } else {
            altMarker17.rollbackTo();
            PsiBuilder.Marker altMarker16;;
            altMarker16 = builder.mark();
            if (this.trait_mod_238_alt_15(builder, opp)) {
                altMarker16.drop();
            } else {
                altMarker16.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_18(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_19(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_20(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt6.equals("hides"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.trait_mod_238_alt_19(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker19;;
            altMarker19 = builder.mark();
            if (this.trait_mod_238_alt_18(builder, opp)) {
                altMarker19.drop();
            } else {
                altMarker19.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238_alt_21(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRAIT_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_quant_22(PsiBuilder builder, OPP opp) {
        if (!(this.circumfix_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean trait_mod_238_alt_23(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker23;
        marker23 = builder.mark();
        PsiBuilder.Marker marker24;
        marker24 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        marker24.done(Perl6ElementTypes.LONG_NAME);
        marker23.done(Perl6ElementTypes.IS_TRAIT_NAME);
        PsiBuilder.Marker quantMarker25;
        quantMarker25 = builder.mark();
        if (this.trait_mod_238_quant_22(builder, opp)) {
            quantMarker25.drop();
        } else {
            quantMarker25.rollbackTo();
        }
        return true;
    }

    private boolean trait_mod_238_alt_24(PsiBuilder builder, OPP opp) {
        String tt7;
        tt7 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRAIT) && (tt7.equals("is"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker26;
        altMarker26 = builder.mark();
        if (this.trait_mod_238_alt_23(builder, opp)) {
            altMarker26.drop();
        } else {
            altMarker26.rollbackTo();
            PsiBuilder.Marker altMarker22;;
            altMarker22 = builder.mark();
            if (this.trait_mod_238_alt_21(builder, opp)) {
                altMarker22.drop();
            } else {
                altMarker22.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean trait_mod_238(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker27;
        altMarker27 = builder.mark();
        if (this.trait_mod_238_alt_24(builder, opp)) {
            altMarker27.drop();
        } else {
            altMarker27.rollbackTo();
            PsiBuilder.Marker altMarker21;;
            altMarker21 = builder.mark();
            if (this.trait_mod_238_alt_20(builder, opp)) {
                altMarker21.drop();
            } else {
                altMarker21.rollbackTo();
                PsiBuilder.Marker altMarker18;;
                altMarker18 = builder.mark();
                if (this.trait_mod_238_alt_17(builder, opp)) {
                    altMarker18.drop();
                } else {
                    altMarker18.rollbackTo();
                    PsiBuilder.Marker altMarker15;;
                    altMarker15 = builder.mark();
                    if (this.trait_mod_238_alt_14(builder, opp)) {
                        altMarker15.drop();
                    } else {
                        altMarker15.rollbackTo();
                        PsiBuilder.Marker altMarker10;;
                        altMarker10 = builder.mark();
                        if (this.trait_mod_238_alt_9(builder, opp)) {
                            altMarker10.drop();
                        } else {
                            altMarker10.rollbackTo();
                            PsiBuilder.Marker altMarker7;;
                            altMarker7 = builder.mark();
                            if (this.trait_mod_238_alt_6(builder, opp)) {
                                altMarker7.drop();
                            } else {
                                altMarker7.rollbackTo();
                                PsiBuilder.Marker altMarker4;;
                                altMarker4 = builder.mark();
                                if (this.trait_mod_238_alt_3(builder, opp)) {
                                    altMarker4.drop();
                                } else {
                                    altMarker4.rollbackTo();
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        marker1.done(Perl6ElementTypes.TRAIT);
        return true;
    }

    private boolean tribble_239_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean tribble_239_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.tribbler_240(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.tribble_239_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean tribble_239_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean tribble_239_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TR_DISTINCT_START_STOP) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.tribbler_240(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.tribble_239_quant_3(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean tribble_239_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.tribble_239_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.tribble_239_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean tribble_239_quant_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.tribble_239_quant_5(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean tribble_239(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.QUOTE_REGEX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.tribbler_240(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.tribble_239_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean tribbler_240_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRANS_CHAR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ccstate_23(builder))) {
            return false;
        }
        return true;
    }

    private boolean tribbler_240_alt_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRANS_BAD) && (tt1.equals(".."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean tribbler_240_alt_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TRANS_RANGE) && (tt2.equals(".."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean tribbler_240_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.tribbler_240_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.tribbler_240_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean tribbler_240_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRANS_BAD) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean tribbler_240_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.TRANS_ESCAPE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ccstate_23(builder))) {
            return false;
        }
        return true;
    }

    private boolean tribbler_240_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean tribbler_240_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.tribbler_240_quant_7(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean tribbler_240_quant_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.tribbler_240_alt_8(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.tribbler_240_alt_6(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.tribbler_240_alt_5(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.tribbler_240_alt_4(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.tribbler_240_alt_1(builder, opp)) {
                            altMarker1.drop();
                        } else {
                            altMarker1.rollbackTo();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean tribbler_240(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker9;;
            quantMarker9 = builder.mark();
            if (this.tribbler_240_quant_9(builder, opp)) {
                quantMarker9.drop();
            } else {
                quantMarker9.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean twigil_241(PsiBuilder builder) {
        OPP opp;
        opp = null;
        return true;
    }

    private boolean type_constraint_242_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_constraint_242_alt_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.PREFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.numish_82(builder))) {
            return false;
        }
        marker2.done(Perl6ElementTypes.VALUE_CONSTRAINT);
        return true;
    }

    private boolean type_constraint_242_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker4;
        marker4 = builder.mark();
        if (!(this.value_247(builder))) {
            return false;
        }
        marker4.done(Perl6ElementTypes.VALUE_CONSTRAINT);
        return true;
    }

    private boolean type_constraint_242_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_constraint_242_alt_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker6;
        marker6 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHERE_CONSTRAINT) && (tt1.equals("where"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.type_constraint_242_quant_4(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        marker6.done(Perl6ElementTypes.WHERE_CONSTRAINT);
        return true;
    }

    private boolean type_constraint_242_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_constraint_242(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.type_constraint_242_alt_5(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.type_constraint_242_alt_3(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.type_constraint_242_alt_2(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.type_constraint_242_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.type_constraint_242_quant_6(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean type_declarator_243_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.CONSTANT_ANON) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_3(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TERM_DECLARATION_BACKSLASH) && (tt2.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.type_declarator_243_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        if (!(this.defterm_38(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.CONSTANT_MISSING_INITIALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.initializer_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_DECLARATOR) && (tt1.equals("constant"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.type_declarator_243_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.type_declarator_243_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.type_declarator_243_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker6;;
            quantMarker6 = builder.mark();
            if (this.type_declarator_243_quant_5(builder, opp)) {
                quantMarker6.drop();
            } else {
                quantMarker6.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.type_declarator_243_alt_7(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.type_declarator_243_alt_6(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                return false;
            }
        }
        marker1.done(Perl6ElementTypes.CONSTANT);
        return true;
    }

    private boolean type_declarator_243_alt_9(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SUBSET_ANON) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_11(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_12(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SUBSET_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_13(PsiBuilder builder, OPP opp) {
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_14(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHERE_CONSTRAINT) && (tt4.equals("where"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker15;
        altMarker15 = builder.mark();
        if (this.type_declarator_243_alt_13(builder, opp)) {
            altMarker15.drop();
        } else {
            altMarker15.rollbackTo();
            PsiBuilder.Marker altMarker14;;
            altMarker14 = builder.mark();
            if (this.type_declarator_243_alt_12(builder, opp)) {
                altMarker14.drop();
            } else {
                altMarker14.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean type_declarator_243_quant_15(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker16;
        quantMarker16 = builder.mark();
        if (this.type_declarator_243_quant_14(builder, opp)) {
            quantMarker16.drop();
        } else {
            quantMarker16.rollbackTo();
        }
        return true;
    }

    private boolean type_declarator_243_quant_16(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_17(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker12;
        altMarker12 = builder.mark();
        if (this.type_declarator_243_alt_10(builder, opp)) {
            altMarker12.drop();
        } else {
            altMarker12.rollbackTo();
            PsiBuilder.Marker altMarker11;;
            altMarker11 = builder.mark();
            if (this.type_declarator_243_alt_9(builder, opp)) {
                altMarker11.drop();
            } else {
                altMarker11.rollbackTo();
                return false;
            }
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker13;;
            quantMarker13 = builder.mark();
            if (this.type_declarator_243_quant_11(builder, opp)) {
                quantMarker13.drop();
            } else {
                quantMarker13.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker17;
        quantMarker17 = builder.mark();
        if (this.type_declarator_243_quant_15(builder, opp)) {
            quantMarker17.drop();
        } else {
            quantMarker17.rollbackTo();
        }
        PsiBuilder.Marker quantMarker18;
        quantMarker18 = builder.mark();
        if (this.type_declarator_243_quant_16(builder, opp)) {
            quantMarker18.drop();
        } else {
            quantMarker18.rollbackTo();
        }
        return true;
    }

    private boolean type_declarator_243_alt_18(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker10;
        marker10 = builder.mark();
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_DECLARATOR) && (tt3.equals("subset"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker19;
        quantMarker19 = builder.mark();
        if (this.type_declarator_243_quant_17(builder, opp)) {
            quantMarker19.drop();
        } else {
            quantMarker19.rollbackTo();
        }
        marker10.done(Perl6ElementTypes.SUBSET);
        return true;
    }

    private boolean type_declarator_243_alt_19(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ENUM_ANON) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_20(PsiBuilder builder, OPP opp) {
        if (!(this.variable_248(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_21(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_22(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_23(PsiBuilder builder, OPP opp) {
        if (!(this.term_215(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_alt_24(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.ENUM_INCOMPLETE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_25(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker27;
        altMarker27 = builder.mark();
        if (this.type_declarator_243_alt_24(builder, opp)) {
            altMarker27.drop();
        } else {
            altMarker27.rollbackTo();
            PsiBuilder.Marker altMarker26;;
            altMarker26 = builder.mark();
            if (this.type_declarator_243_alt_23(builder, opp)) {
                altMarker26.drop();
            } else {
                altMarker26.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean type_declarator_243_quant_26(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        return true;
    }

    private boolean type_declarator_243_quant_27(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker24;
        altMarker24 = builder.mark();
        if (this.type_declarator_243_alt_21(builder, opp)) {
            altMarker24.drop();
        } else {
            altMarker24.rollbackTo();
            PsiBuilder.Marker altMarker23;;
            altMarker23 = builder.mark();
            if (this.type_declarator_243_alt_20(builder, opp)) {
                altMarker23.drop();
            } else {
                altMarker23.rollbackTo();
                PsiBuilder.Marker altMarker22;;
                altMarker22 = builder.mark();
                if (this.type_declarator_243_alt_19(builder, opp)) {
                    altMarker22.drop();
                } else {
                    altMarker22.rollbackTo();
                    return false;
                }
            }
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker25;;
            quantMarker25 = builder.mark();
            if (this.type_declarator_243_quant_22(builder, opp)) {
                quantMarker25.drop();
            } else {
                quantMarker25.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker28;
        quantMarker28 = builder.mark();
        if (this.type_declarator_243_quant_25(builder, opp)) {
            quantMarker28.drop();
        } else {
            quantMarker28.rollbackTo();
        }
        PsiBuilder.Marker quantMarker29;
        quantMarker29 = builder.mark();
        if (this.type_declarator_243_quant_26(builder, opp)) {
            quantMarker29.drop();
        } else {
            quantMarker29.rollbackTo();
        }
        return true;
    }

    private boolean type_declarator_243_alt_28(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker21;
        marker21 = builder.mark();
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_DECLARATOR) && (tt5.equals("enum"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.kok_64(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker30;
        quantMarker30 = builder.mark();
        if (this.type_declarator_243_quant_27(builder, opp)) {
            quantMarker30.drop();
        } else {
            quantMarker30.rollbackTo();
        }
        marker21.done(Perl6ElementTypes.ENUM);
        return true;
    }

    private boolean type_declarator_243(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker31;
        altMarker31 = builder.mark();
        if (this.type_declarator_243_alt_28(builder, opp)) {
            altMarker31.drop();
        } else {
            altMarker31.rollbackTo();
            PsiBuilder.Marker altMarker20;;
            altMarker20 = builder.mark();
            if (this.type_declarator_243_alt_18(builder, opp)) {
                altMarker20.drop();
            } else {
                altMarker20.rollbackTo();
                PsiBuilder.Marker altMarker9;;
                altMarker9 = builder.mark();
                if (this.type_declarator_243_alt_8(builder, opp)) {
                    altMarker9.drop();
                } else {
                    altMarker9.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean typename_244_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean typename_244_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.longname_colonpairs_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean typename_244_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean typename_244_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_PARAMETER_BRACKET) && (tt2.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean typename_244_quant_5(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_PARAMETER_BRACKET) && (tt1.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.arglist_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.typename_244_quant_4(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean typename_244_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean typename_244_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean typename_244_alt_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INCOMPLETE_TYPE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean typename_244_alt_9(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_COERCION_PARENTHESES_CLOSE) && (tt4.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean typename_244_quant_10(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.TYPE_COERCION_PARENTHESES_OPEN) && (tt3.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.typename_244_quant_7(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.typename_244_alt_9(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.typename_244_alt_8(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean typename_244_alt_11(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.INCOMPLETE_TYPE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean typename_244_alt_12(PsiBuilder builder, OPP opp) {
        if (!(this.typename_244(builder))) {
            return false;
        }
        return true;
    }

    private boolean typename_244_quant_13(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.NAME) && (tt5.equals("of"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker altMarker14;
        altMarker14 = builder.mark();
        if (this.typename_244_alt_12(builder, opp)) {
            altMarker14.drop();
        } else {
            altMarker14.rollbackTo();
            PsiBuilder.Marker altMarker13;;
            altMarker13 = builder.mark();
            if (this.typename_244_alt_11(builder, opp)) {
                altMarker13.drop();
            } else {
                altMarker13.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean typename_244(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.typename_244_alt_2(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.typename_244_alt_1(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        marker2.done(Perl6ElementTypes.LONG_NAME);
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.typename_244_quant_3(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.typename_244_quant_5(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.typename_244_quant_6(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.typename_244_quant_10(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        PsiBuilder.Marker quantMarker15;
        quantMarker15 = builder.mark();
        if (this.typename_244_quant_13(builder, opp)) {
            quantMarker15.drop();
        } else {
            quantMarker15.rollbackTo();
        }
        marker1.done(Perl6ElementTypes.TYPE_NAME);
        return true;
    }

    private boolean unsp_245_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean unsp_245_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.unv_246(builder))) {
            return false;
        }
        return true;
    }

    private boolean unsp_245_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.vws_252(builder))) {
            return false;
        }
        return true;
    }

    private boolean unsp_245_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.unsp_245_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.unsp_245_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.unsp_245_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean unsp_245(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.UNSP_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.unsp_245_quant_4(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean unv_246_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.pod_content_toplevel_102(builder))) {
            return false;
        }
        return true;
    }

    private boolean unv_246_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.comment_31(builder))) {
            return false;
        }
        return true;
    }

    private boolean unv_246_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.UNV_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.unv_246_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean unv_246_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.comment_31(builder))) {
            return false;
        }
        return true;
    }

    private boolean unv_246(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.unv_246_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.unv_246_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.unv_246_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean value_247_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.version_250(builder))) {
            return false;
        }
        return true;
    }

    private boolean value_247_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.quote_119(builder))) {
            return false;
        }
        return true;
    }

    private boolean value_247_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.number_81(builder))) {
            return false;
        }
        return true;
    }

    private boolean value_247(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.value_247_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.value_247_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.value_247_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean variable_248_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean variable_248_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean variable_248_alt_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean variable_248_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean variable_248_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.variable_248_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.variable_248_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.variable_248_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.variable_248_alt_1(builder, opp)) {
                        altMarker1.drop();
                    } else {
                        altMarker1.rollbackTo();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean variable_248_alt_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker6;
        marker6 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker6.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean variable_248_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.contextualizer_33(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_248_alt_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker9;
        marker9 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker9.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean variable_248_quant_9(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) && (tt1.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean variable_248_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.VARIABLE_REGEX_NAMED_CAPTURE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker11;
        marker11 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.REGEX_CAPTURE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.quote_q_126(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.variable_248_quant_9(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        marker11.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean variable_248_alt_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker14;
        marker14 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker14.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean variable_248_alt_12(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker16;
        marker16 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker16.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean variable_248_alt_13(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) && (tt3.equals("\\"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean variable_248_alt_14(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_248_quant_15(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker20;
        altMarker20 = builder.mark();
        if (this.variable_248_alt_14(builder, opp)) {
            altMarker20.drop();
        } else {
            altMarker20.rollbackTo();
            PsiBuilder.Marker altMarker19;;
            altMarker19 = builder.mark();
            if (this.variable_248_alt_13(builder, opp)) {
                altMarker19.drop();
            } else {
                altMarker19.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean variable_248_quant_16(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SELF_CALL_VARIABLE_ARGS) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker21;
        quantMarker21 = builder.mark();
        if (this.variable_248_quant_15(builder, opp)) {
            quantMarker21.drop();
        } else {
            quantMarker21.rollbackTo();
        }
        if (!(this.postcircumfix_108(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_248_alt_17(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SELF_CALL_VARIABLE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker18;
        marker18 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.SELF) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_OPERATOR) && (tt2.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == Perl6TokenTypes.METHOD_CALL_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker22;
        quantMarker22 = builder.mark();
        if (this.variable_248_quant_16(builder, opp)) {
            quantMarker22.drop();
        } else {
            quantMarker22.rollbackTo();
        }
        marker18.done(Perl6ElementTypes.METHOD_CALL);
        return true;
    }

    private boolean variable_248_quant_18(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) && (tt5.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean variable_248_quant_19(PsiBuilder builder, OPP opp) {
        if (!(this.infixish_57(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker25;
        quantMarker25 = builder.mark();
        if (this.variable_248_quant_18(builder, opp)) {
            quantMarker25.drop();
        } else {
            quantMarker25.rollbackTo();
        }
        return true;
    }

    private boolean variable_248_alt_20(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker24;
        marker24 = builder.mark();
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.VARIABLE) && (tt4.equals("&["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker26;
        quantMarker26 = builder.mark();
        if (this.variable_248_quant_19(builder, opp)) {
            quantMarker26.drop();
        } else {
            quantMarker26.rollbackTo();
        }
        marker24.done(Perl6ElementTypes.VARIABLE);
        return true;
    }

    private boolean variable_248_alt_21(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.SIMPLE_CONTEXTUALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker marker28;
        marker28 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.CONTEXTUALIZER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.variable_248(builder))) {
            return false;
        }
        marker28.done(Perl6ElementTypes.CONTEXTUALIZER);
        return true;
    }

    private boolean variable_248(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.variable_248_quant_5(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker altMarker29;
        altMarker29 = builder.mark();
        if (this.variable_248_alt_21(builder, opp)) {
            altMarker29.drop();
        } else {
            altMarker29.rollbackTo();
            PsiBuilder.Marker altMarker27;;
            altMarker27 = builder.mark();
            if (this.variable_248_alt_20(builder, opp)) {
                altMarker27.drop();
            } else {
                altMarker27.rollbackTo();
                PsiBuilder.Marker altMarker23;;
                altMarker23 = builder.mark();
                if (this.variable_248_alt_17(builder, opp)) {
                    altMarker23.drop();
                } else {
                    altMarker23.rollbackTo();
                    PsiBuilder.Marker altMarker17;;
                    altMarker17 = builder.mark();
                    if (this.variable_248_alt_12(builder, opp)) {
                        altMarker17.drop();
                    } else {
                        altMarker17.rollbackTo();
                        PsiBuilder.Marker altMarker15;;
                        altMarker15 = builder.mark();
                        if (this.variable_248_alt_11(builder, opp)) {
                            altMarker15.drop();
                        } else {
                            altMarker15.rollbackTo();
                            PsiBuilder.Marker altMarker13;;
                            altMarker13 = builder.mark();
                            if (this.variable_248_alt_10(builder, opp)) {
                                altMarker13.drop();
                            } else {
                                altMarker13.rollbackTo();
                                PsiBuilder.Marker altMarker10;;
                                altMarker10 = builder.mark();
                                if (this.variable_248_alt_8(builder, opp)) {
                                    altMarker10.drop();
                                } else {
                                    altMarker10.rollbackTo();
                                    PsiBuilder.Marker altMarker8;;
                                    altMarker8 = builder.mark();
                                    if (this.variable_248_alt_7(builder, opp)) {
                                        altMarker8.drop();
                                    } else {
                                        altMarker8.rollbackTo();
                                        PsiBuilder.Marker altMarker7;;
                                        altMarker7 = builder.mark();
                                        if (this.variable_248_alt_6(builder, opp)) {
                                            altMarker7.drop();
                                        } else {
                                            altMarker7.rollbackTo();
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean variable_declarator_249_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_declarator_249_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SHAPE_DECLARATION) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean variable_declarator_249_alt_3(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SHAPE_DECLARATION) && (tt1.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.variable_declarator_249_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean variable_declarator_249_quant_4(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SHAPE_DECLARATION) && (tt4.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean variable_declarator_249_alt_5(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == Perl6TokenTypes.SHAPE_DECLARATION) && (tt3.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.semilist_159(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.variable_declarator_249_quant_4(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean variable_declarator_249_quant_6(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.variable_declarator_249_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.variable_declarator_249_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean variable_declarator_249_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.variable_declarator_249_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.variable_declarator_249_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker7;;
            quantMarker7 = builder.mark();
            if (this.variable_declarator_249_quant_6(builder, opp)) {
                quantMarker7.drop();
            } else {
                quantMarker7.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean variable_declarator_249_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.trait_237(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_declarator_249_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.post_constraint_107(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_declarator_249_quant_10(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker10;;
            quantMarker10 = builder.mark();
            if (this.variable_declarator_249_quant_9(builder, opp)) {
                quantMarker10.drop();
            } else {
                quantMarker10.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean variable_declarator_249(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.variable_248(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.variable_declarator_249_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        if (!(this.ws_253(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker9;;
            quantMarker9 = builder.mark();
            if (this.variable_declarator_249_quant_8(builder, opp)) {
                quantMarker9.drop();
            } else {
                quantMarker9.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.variable_declarator_249_quant_10(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        return true;
    }

    private boolean version_250(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == Perl6TokenTypes.VERSION) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(Perl6ElementTypes.VERSION);
        return true;
    }

    private boolean vnum_251_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean vnum_251_quant_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean vnum_251_alt_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.vnum_251_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.vnum_251_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean vnum_251(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.vnum_251_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.vnum_251_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean vws_252(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == Perl6TokenTypes.VERTICAL_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ws_253_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.unsp_245(builder))) {
            return false;
        }
        return true;
    }

    private boolean ws_253_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.unv_246(builder))) {
            return false;
        }
        return true;
    }

    private boolean ws_253_alt_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == Perl6TokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.heredoc_49(builder))) {
            return false;
        }
        return true;
    }

    private boolean ws_253_quant_4(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.ws_253_alt_3(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.ws_253_alt_2(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.ws_253_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean ws_253(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.ws_253_quant_4(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean xblock_254_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.pblock_93(builder))) {
            return false;
        }
        return true;
    }

    private boolean xblock_254_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.ws_253(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.xblock_254_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean xblock_254(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.EXPR_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.xblock_254_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

}