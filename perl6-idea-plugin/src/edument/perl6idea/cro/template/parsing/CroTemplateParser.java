package edument.perl6idea.cro.template.parsing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import edument.perl6idea.parsing.OPP;

public class CroTemplateParser implements PsiParser {

    public ASTNode parse(IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker;
        rootMarker = builder.mark();
        this.TOP_1(builder);
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private boolean TOP_1_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_42(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_1_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.bogusend_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_1(PsiBuilder builder) {
        OPP opp;
        opp = null;
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.TOP_1_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.TOP_1_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean applysigil_2(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_APPLY_SIGIL) && (tt1.equals("|"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean arglist_3_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COMMA) && (tt2.equals(","))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean arglist_3_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.argument_4(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.arglist_3_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean arglist_3_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean arglist_3_quant_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.arglist_3_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean arglist_3_quant_5(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.arglist_3_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.arglist_3_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean arglist_3(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_PAREN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.arglist_3_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.ARGLIST);
        return true;
    }

    private boolean argument_4_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean argument_4_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX) && (tt2.equals("!"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean argument_4_quant_3(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX) && (tt4.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean argument_4_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.argument_4_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean argument_4_quant_5(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX) && (tt3.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.argument_4_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean argument_4_quant_6(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.NAMED_ARGUMENT_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.argument_4_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean argument_4_alt_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker2;
        marker2 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.NAMED_ARGUMENT_SYNTAX) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.argument_4_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.argument_4_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        marker2.done(CroTemplateElementTypes.NAMED_ARGUMENT);
        return true;
    }

    private boolean argument_4(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.argument_4_alt_7(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.argument_4_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean block_5_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_CURLY) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean block_5_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.block_5_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean block_5(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_CURLY) && (tt1.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.block_5_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean bogusend_6(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.BAD_CHARACTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean bool_7(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.BOOL_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.BOOL_LITERAL);
        return true;
    }

    private boolean callsigil_8(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_CALL_SIGIL) && (tt1.equals("&"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean close_9(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_SLASH) && (tt1.equals("/"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean commenttag_10(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.COMMENT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.COMMENT);
        return true;
    }

    private boolean condsigil_11(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean declsigil_12(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_DECL_SIGIL) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean deref_13_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitem_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean deref_13_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.deref_13_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean deref_13(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.derefitem_14(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.deref_13_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean derefitem_14_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemhashliteral_17(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_14_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemhash_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_14_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemarray_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_14_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemsmart_19(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_14_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemmethod_18(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_14(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.derefitem_14_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.derefitem_14_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.derefitem_14_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.derefitem_14_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.derefitem_14_alt_1(builder, opp)) {
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

    private boolean derefitemarray_15_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_BRACKET) && (tt2.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemarray_15_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemarray_15_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemarray_15(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_BRACKET) && (tt1.equals("["))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.derefitemarray_15_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_ARRAY);
        return true;
    }

    private boolean derefitemhash_16_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_CURLY) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemhash_16_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemhash_16_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemhash_16(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_CURLY) && (tt1.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.derefitemhash_16_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_HASH);
        return true;
    }

    private boolean derefitemhashliteral_17_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_ANGLE) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemhashliteral_17_quant_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_KEY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemhashliteral_17_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemhashliteral_17(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_ANGLE) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.derefitemhashliteral_17_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_HASH_LITERAL);
        return true;
    }

    private boolean derefitemmethod_18(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.IDENTIFER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.arglist_3(builder))) {
            return false;
        }
        marker1.done(CroTemplateElementTypes.DEREF_METHOD);
        return true;
    }

    private boolean derefitemsmart_19(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.IDENTIFER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.DEREF_SMART);
        return true;
    }

    private boolean derefterm_20_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefterm_20(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefterm_20_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean dot_21(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DOT) && (tt1.equals("."))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean expression_22_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_22_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.nextterm_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_22_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_67(builder))) {
            return false;
        }
        opp.startInfix();
        if (!(this.infixish_28(builder))) {
            return false;
        }
        opp.endInfix();
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.expression_22_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        opp.startPrefixes();
        opp.endPrefixes();
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.expression_22_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        opp.startPostfixes();
        opp.endPostfixes();
        return true;
    }

    private boolean expression_22_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean expression_22_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.END_OF_EXPR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean expression_22(PsiBuilder builder) {
        OPP opp;
        opp = new OPP(builder);
        opp.startExpr();
        opp.startPrefixes();
        opp.endPrefixes();
        if (!(this.term_63(builder))) {
            return false;
        }
        opp.startPostfixes();
        opp.endPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.expression_22_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        opp.endExpr();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.expression_22_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.expression_22_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean gt_23(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_CLOSE) && (tt1.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean hws_24(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ident_25_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_25_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_25_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_25(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ident_25_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ident_25_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.ident_25_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean identifier_26_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ident_25(builder))) {
            return false;
        }
        return true;
    }

    private boolean identifier_26(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ident_25(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.identifier_26_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean infix_27(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_28(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.infix_27(builder))) {
            return false;
        }
        marker1.done(CroTemplateElementTypes.INFIX);
        return true;
    }

    private boolean int_29(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.INT_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.INT_LITERAL);
        return true;
    }

    private boolean itersigil_30(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_ITER_SIGIL) && (tt1.equals("@"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattribute_31_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattributevalue_32(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattribute_31_quant_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_EQUALS) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.literaltagattribute_31_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattribute_31(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.literaltagattribute_31_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_TAG_ATTRIBUTE);
        return true;
    }

    private boolean literaltagattributevalue_32_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) && (tt2.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_48(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.literaltagattributevalue_32_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.literaltagattributevalue_32_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.literaltagattributevalue_32_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagattributevalue_32_quant_6(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt3.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_7(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt1.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.literaltagattributevalue_32_quant_5(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.literaltagattributevalue_32_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) && (tt5.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_48(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_quant_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.literaltagattributevalue_32_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.literaltagattributevalue_32_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.literaltagattributevalue_32_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagattributevalue_32_quant_12(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt6.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_32_alt_13(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt4.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker11;;
            quantMarker11 = builder.mark();
            if (this.literaltagattributevalue_32_quant_11(builder, opp)) {
                quantMarker11.drop();
            } else {
                quantMarker11.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.literaltagattributevalue_32_quant_12(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_32(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.literaltagattributevalue_32_alt_13(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.literaltagattributevalue_32_alt_7(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.literaltagattributevalue_32_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagname_33(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean lt_34(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_OPEN) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean modulename_35(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MODULE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean nextterm_36(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.term_63(builder))) {
            return false;
        }
        return true;
    }

    private boolean num_37(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.NUM_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.NUM_LITERAL);
        return true;
    }

    private boolean parameter_38_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.parametername_39(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_38_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.parametername_39(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_38_alt_3(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.NAMED_PARAMETER_SYNTAX) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.parameter_38_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean parameter_38_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean parameter_38_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker5;
        marker5 = builder.mark();
        if (!(this.ws_67(builder))) {
            return false;
        }
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DEFAULT_PARAMETER_SYNTAX) && (tt2.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.parameter_38_quant_4(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker5.done(CroTemplateElementTypes.PARAMETER_DEFAULT);
        return true;
    }

    private boolean parameter_38(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.parameter_38_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.parameter_38_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.parameter_38_quant_5(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.PARAMETER);
        return true;
    }

    private boolean parametername_39(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parenthesizedexpression_40_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parenthesizedexpression_40_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_22(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.parenthesizedexpression_40_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean parenthesizedexpression_40(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_PAREN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.parenthesizedexpression_40_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.PARENTHESIZED_EXPRESSION);
        return true;
    }

    private boolean rat_41(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.RAT_LITERAL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.RAT_LITERAL);
        return true;
    }

    private boolean sequenceelement_42_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_48(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_42_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteraltext_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_42_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralopentag_45(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_42_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralclosetag_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_42_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.commenttag_10(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_42(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.sequenceelement_42_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.sequenceelement_42_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.sequenceelement_42_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.sequenceelement_42_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.sequenceelement_42_alt_1(builder, opp)) {
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

    private boolean sequenceelementgroup_43_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_42(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementgroup_43(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.sequenceelementgroup_43_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        marker1.done(CroTemplateElementTypes.TAG_SEQUENCE);
        return true;
    }

    private boolean sequenceelementliteralclosetag_44_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.gt_23(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_44_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagname_33(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sequenceelementliteralclosetag_44_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_44_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sequenceelementliteralclosetag_44_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_44(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_34(builder))) {
            return false;
        }
        if (!(this.close_9(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sequenceelementliteralclosetag_44_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_CLOSE_TAG);
        return true;
    }

    private boolean sequenceelementliteralopentag_45_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_48(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_45_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattribute_31(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_45_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.sequenceelementliteralopentag_45_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sequenceelementliteralopentag_45_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_45_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.gt_23(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_45_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagname_33(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.sequenceelementliteralopentag_45_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sequenceelementliteralopentag_45_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_45(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_34(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sequenceelementliteralopentag_45_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_OPEN_TAG);
        return true;
    }

    private boolean sequenceelementliteraltext_46(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigil_47_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_47_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_47(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.sigil_47_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.sigil_47_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sigiltag_48_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltaguse_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagpart_55(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagapply_49(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagbody_50(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagmacro_54(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagsub_56(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcall_51(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcondition_52(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagiteration_53(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagvariable_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48_alt_11(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagtopic_57(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_48(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker11;
        altMarker11 = builder.mark();
        if (this.sigiltag_48_alt_11(builder, opp)) {
            altMarker11.drop();
        } else {
            altMarker11.rollbackTo();
            PsiBuilder.Marker altMarker10;;
            altMarker10 = builder.mark();
            if (this.sigiltag_48_alt_10(builder, opp)) {
                altMarker10.drop();
            } else {
                altMarker10.rollbackTo();
                PsiBuilder.Marker altMarker9;;
                altMarker9 = builder.mark();
                if (this.sigiltag_48_alt_9(builder, opp)) {
                    altMarker9.drop();
                } else {
                    altMarker9.rollbackTo();
                    PsiBuilder.Marker altMarker8;;
                    altMarker8 = builder.mark();
                    if (this.sigiltag_48_alt_8(builder, opp)) {
                        altMarker8.drop();
                    } else {
                        altMarker8.rollbackTo();
                        PsiBuilder.Marker altMarker7;;
                        altMarker7 = builder.mark();
                        if (this.sigiltag_48_alt_7(builder, opp)) {
                            altMarker7.drop();
                        } else {
                            altMarker7.rollbackTo();
                            PsiBuilder.Marker altMarker6;;
                            altMarker6 = builder.mark();
                            if (this.sigiltag_48_alt_6(builder, opp)) {
                                altMarker6.drop();
                            } else {
                                altMarker6.rollbackTo();
                                PsiBuilder.Marker altMarker5;;
                                altMarker5 = builder.mark();
                                if (this.sigiltag_48_alt_5(builder, opp)) {
                                    altMarker5.drop();
                                } else {
                                    altMarker5.rollbackTo();
                                    PsiBuilder.Marker altMarker4;;
                                    altMarker4 = builder.mark();
                                    if (this.sigiltag_48_alt_4(builder, opp)) {
                                        altMarker4.drop();
                                    } else {
                                        altMarker4.rollbackTo();
                                        PsiBuilder.Marker altMarker3;;
                                        altMarker3 = builder.mark();
                                        if (this.sigiltag_48_alt_3(builder, opp)) {
                                            altMarker3.drop();
                                        } else {
                                            altMarker3.rollbackTo();
                                            PsiBuilder.Marker altMarker2;;
                                            altMarker2 = builder.mark();
                                            if (this.sigiltag_48_alt_2(builder, opp)) {
                                                altMarker2.drop();
                                            } else {
                                                altMarker2.rollbackTo();
                                                PsiBuilder.Marker altMarker1;;
                                                altMarker1 = builder.mark();
                                                if (this.sigiltag_48_alt_1(builder, opp)) {
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
        return true;
    }

    private boolean sigiltagapply_49_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_49_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_49_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_49_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_49_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.tclose_62(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagapply_49_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_49_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_43(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagapply_49_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_49_quant_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagapply_49_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagapply_49_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagapply_49_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagapply_49_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_49(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagapply_49_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.APPLY);
        return true;
    }

    private boolean sigiltagbody_50_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagbody_50_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagbody_50_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagbody_50(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt1.equals("body"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagbody_50_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.BODY);
        return true;
    }

    private boolean sigiltagcall_51_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_51_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_51_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_51_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_51_quant_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SUB_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagcall_51_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagcall_51_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagcall_51_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagcall_51_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcall_51(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.callsigil_8(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcall_51_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CALL);
        return true;
    }

    private boolean sigiltagcondition_52_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.variable_66(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_52_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.block_5(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_52_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_52_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagcondition_52_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_52_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_52_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.tclose_62(builder))) {
            return false;
        }
        if (!(this.condsigil_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcondition_52_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_52_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_43(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagcondition_52_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_52_quant_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.sigiltagcondition_52_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.sigiltagcondition_52_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.sigiltagcondition_52_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagcondition_52_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_52(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.condsigil_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagcondition_52_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CONDITION);
        return true;
    }

    private boolean sigiltagiteration_53_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagiteration_53_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.variable_66(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagiteration_53_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_53_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagiteration_53_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_9(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker marker10;
        marker10 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker10.done(CroTemplateElementTypes.PARAMETER);
        return true;
    }

    private boolean sigiltagiteration_53_quant_10(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COLON) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagiteration_53_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        PsiBuilder.Marker quantMarker11;
        quantMarker11 = builder.mark();
        if (this.sigiltagiteration_53_quant_9(builder, opp)) {
            quantMarker11.drop();
        } else {
            quantMarker11.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_11(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_12(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.tclose_62(builder))) {
            return false;
        }
        if (!(this.itersigil_30(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker13;
        quantMarker13 = builder.mark();
        if (this.sigiltagiteration_53_quant_11(builder, opp)) {
            quantMarker13.drop();
        } else {
            quantMarker13.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_13(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_43(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker14;
        quantMarker14 = builder.mark();
        if (this.sigiltagiteration_53_quant_12(builder, opp)) {
            quantMarker14.drop();
        } else {
            quantMarker14.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53_quant_14(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.sigiltagiteration_53_alt_6(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.sigiltagiteration_53_alt_4(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                PsiBuilder.Marker altMarker4;;
                altMarker4 = builder.mark();
                if (this.sigiltagiteration_53_alt_3(builder, opp)) {
                    altMarker4.drop();
                } else {
                    altMarker4.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagiteration_53_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.sigiltagiteration_53_quant_10(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        PsiBuilder.Marker quantMarker15;
        quantMarker15 = builder.mark();
        if (this.sigiltagiteration_53_quant_13(builder, opp)) {
            quantMarker15.drop();
        } else {
            quantMarker15.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_53(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.itersigil_30(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker16;
        quantMarker16 = builder.mark();
        if (this.sigiltagiteration_53_quant_14(builder, opp)) {
            quantMarker16.drop();
        } else {
            quantMarker16.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.ITERATION);
        return true;
    }

    private boolean sigiltagmacro_54_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("macro"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.tclose_62(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagmacro_54_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagmacro_54_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_43(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagmacro_54_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagmacro_54_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagmacro_54_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagmacro_54_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagmacro_54_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_54_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagmacro_54_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_54(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt1.equals("macro"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.sigiltagmacro_54_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.MACRO);
        return true;
    }

    private boolean sigiltagpart_55_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("sub"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.tclose_62(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagpart_55_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagpart_55_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_43(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagpart_55_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.PART_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagpart_55_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagpart_55_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagpart_55_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagpart_55_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagpart_55_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagpart_55_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagpart_55(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt1.equals("part"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.sigiltagpart_55_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.PART);
        return true;
    }

    private boolean sigiltagsub_56_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("sub"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.tclose_62(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagsub_56_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagsub_56_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_43(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagsub_56_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SUB_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagsub_56_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagsub_56_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagsub_56_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagsub_56_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_56_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagsub_56_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_56(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt1.equals("sub"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker10;
        quantMarker10 = builder.mark();
        if (this.sigiltagsub_56_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.SUB);
        return true;
    }

    private boolean sigiltagtopic_57_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagtopic_57_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagtopic_57_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagtopic_57(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagtopic_57_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean sigiltaguse_58_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.modulename_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_58_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_58_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_58_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_58_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.sigiltaguse_58_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sigiltaguse_58_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltaguse_58_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltaguse_58_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_58_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltaguse_58_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_58(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if (!(this.declsigil_12(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt1.equals("use"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltaguse_58_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.USE);
        return true;
    }

    private boolean sigiltagvariable_59_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_59_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagvariable_59_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagvariable_59_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_64(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_59(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_65(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagvariable_59_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagvariable_59_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean signature_60_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COMMA) && (tt2.equals(","))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_60_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.parameter_38(builder))) {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.signature_60_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean signature_60_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_24(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_60_quant_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.signature_60_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean signature_60_quant_5(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.signature_60_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.signature_60_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean signature_60(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_PAREN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_67(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.signature_60_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.SIGNATURE);
        return true;
    }

    private boolean singlequotestring_61_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.STRING_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_61_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.STRING_QUOTE_SINGLE) && (tt2.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_61(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.STRING_QUOTE_SINGLE) && (tt1.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.singlequotestring_61_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.singlequotestring_61_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean tclose_62(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_SLASH) && (tt1.equals("/"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.parenthesizedexpression_40(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.derefterm_20(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.variable_66(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.bool_7(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.int_29(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.rat_41(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.num_37(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_63(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker8;
        altMarker8 = builder.mark();
        if (this.term_63_alt_8(builder, opp)) {
            altMarker8.drop();
        } else {
            altMarker8.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.term_63_alt_7(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker6;;
                altMarker6 = builder.mark();
                if (this.term_63_alt_6(builder, opp)) {
                    altMarker6.drop();
                } else {
                    altMarker6.rollbackTo();
                    PsiBuilder.Marker altMarker5;;
                    altMarker5 = builder.mark();
                    if (this.term_63_alt_5(builder, opp)) {
                        altMarker5.drop();
                    } else {
                        altMarker5.rollbackTo();
                        PsiBuilder.Marker altMarker4;;
                        altMarker4 = builder.mark();
                        if (this.term_63_alt_4(builder, opp)) {
                            altMarker4.drop();
                        } else {
                            altMarker4.rollbackTo();
                            PsiBuilder.Marker altMarker3;;
                            altMarker3 = builder.mark();
                            if (this.term_63_alt_3(builder, opp)) {
                                altMarker3.drop();
                            } else {
                                altMarker3.rollbackTo();
                                PsiBuilder.Marker altMarker2;;
                                altMarker2 = builder.mark();
                                if (this.term_63_alt_2(builder, opp)) {
                                    altMarker2.drop();
                                } else {
                                    altMarker2.rollbackTo();
                                    PsiBuilder.Marker altMarker1;;
                                    altMarker1 = builder.mark();
                                    if (this.term_63_alt_1(builder, opp)) {
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
        return true;
    }

    private boolean tgt_64(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_CLOSE) && (tt1.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean tlt_65(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_OPEN) && (tt1.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean variable_66_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_66_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.variable_66_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean variable_66(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.variable_66_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean ws_67_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ws_67_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ws_67(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ws_67_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ws_67_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

}
