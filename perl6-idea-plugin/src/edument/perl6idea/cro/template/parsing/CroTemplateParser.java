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
        if (!(this.sequenceelement_38(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_1_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.bogusend_5(builder))) {
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
        if (!(this.ws_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean arglist_3_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_20(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
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
        if (!(this.hws_22(builder))) {
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
        if (!(this.ws_62(builder))) {
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
        if (!(this.ws_62(builder))) {
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

    private boolean block_4_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_CURLY) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean block_4_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_20(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.block_4_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean block_4(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_CURLY) && (tt1.equals("{"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.block_4_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean bogusend_5(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.BAD_CHARACTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean callsigil_6(PsiBuilder builder) {
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

    private boolean close_7(PsiBuilder builder) {
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

    private boolean commenttag_8(PsiBuilder builder) {
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

    private boolean condsigil_9(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean declsigil_10(PsiBuilder builder) {
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

    private boolean deref_11_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitem_12(builder))) {
            return false;
        }
        return true;
    }

    private boolean deref_11_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.deref_11_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean deref_11(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.derefitem_12(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.deref_11_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean derefitem_12_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemhashliteral_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_12_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemhash_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_12_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemarray_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_12_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemsmart_17(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_12_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemmethod_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_12(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.derefitem_12_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.derefitem_12_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.derefitem_12_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.derefitem_12_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.derefitem_12_alt_1(builder, opp)) {
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

    private boolean derefitemarray_13_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_BRACKET) && (tt2.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemarray_13_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_20(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemarray_13_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemarray_13(PsiBuilder builder) {
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
        if (this.derefitemarray_13_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_ARRAY);
        return true;
    }

    private boolean derefitemhash_14_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_CURLY) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemhash_14_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_20(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemhash_14_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemhash_14(PsiBuilder builder) {
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
        if (this.derefitemhash_14_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_HASH);
        return true;
    }

    private boolean derefitemhashliteral_15_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_ANGLE) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemhashliteral_15_quant_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_KEY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemhashliteral_15_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemhashliteral_15(PsiBuilder builder) {
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
        if (this.derefitemhashliteral_15_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_HASH_LITERAL);
        return true;
    }

    private boolean derefitemmethod_16_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemmethod_16(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.IDENTIFER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.OPEN_PAREN) && (tt1.equals("("))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemmethod_16_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_METHOD);
        return true;
    }

    private boolean derefitemsmart_17(PsiBuilder builder) {
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

    private boolean derefterm_18_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_11(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefterm_18(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.dot_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefterm_18_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean dot_19(PsiBuilder builder) {
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

    private boolean expression_20_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_20_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.nextterm_33(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_20_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_62(builder))) {
            return false;
        }
        opp.startInfix();
        if (!(this.infixish_26(builder))) {
            return false;
        }
        opp.endInfix();
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.expression_20_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        opp.startPrefixes();
        opp.endPrefixes();
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.expression_20_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        opp.startPostfixes();
        opp.endPostfixes();
        return true;
    }

    private boolean expression_20_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean expression_20_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.END_OF_EXPR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean expression_20(PsiBuilder builder) {
        OPP opp;
        opp = new OPP(builder);
        opp.startExpr();
        opp.startPrefixes();
        opp.endPrefixes();
        if (!(this.term_58(builder))) {
            return false;
        }
        opp.startPostfixes();
        opp.endPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.expression_20_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        opp.endExpr();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.expression_20_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.expression_20_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean gt_21(PsiBuilder builder) {
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

    private boolean hws_22(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ident_23_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_23_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_23_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_23(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ident_23_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ident_23_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.ident_23_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean identifier_24_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ident_23(builder))) {
            return false;
        }
        return true;
    }

    private boolean identifier_24(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ident_23(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.identifier_24_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean infix_25(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_26(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.infix_25(builder))) {
            return false;
        }
        marker1.done(CroTemplateElementTypes.INFIX);
        return true;
    }

    private boolean int_27(PsiBuilder builder) {
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

    private boolean itersigil_28(PsiBuilder builder) {
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

    private boolean literaltagattribute_29_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattributevalue_30(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattribute_29_quant_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_EQUALS) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.literaltagattribute_29_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattribute_29(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.literaltagattribute_29_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_TAG_ATTRIBUTE);
        return true;
    }

    private boolean literaltagattributevalue_30_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) && (tt2.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.literaltagattributevalue_30_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.literaltagattributevalue_30_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.literaltagattributevalue_30_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagattributevalue_30_quant_6(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt3.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_7(PsiBuilder builder, OPP opp) {
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
            if (this.literaltagattributevalue_30_quant_5(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.literaltagattributevalue_30_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) && (tt5.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_quant_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.literaltagattributevalue_30_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.literaltagattributevalue_30_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.literaltagattributevalue_30_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagattributevalue_30_quant_12(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt6.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_30_alt_13(PsiBuilder builder, OPP opp) {
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
            if (this.literaltagattributevalue_30_quant_11(builder, opp)) {
                quantMarker11.drop();
            } else {
                quantMarker11.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.literaltagattributevalue_30_quant_12(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_30(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.literaltagattributevalue_30_alt_13(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.literaltagattributevalue_30_alt_7(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.literaltagattributevalue_30_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagname_31(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean lt_32(PsiBuilder builder) {
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

    private boolean nextterm_33(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.term_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean num_34(PsiBuilder builder) {
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

    private boolean parameter_35(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        marker1.done(CroTemplateElementTypes.PARAMETER);
        return true;
    }

    private boolean parenthesizedexpression_36_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parenthesizedexpression_36_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_20(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.parenthesizedexpression_36_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean parenthesizedexpression_36(PsiBuilder builder) {
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
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.parenthesizedexpression_36_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.PARENTHESIZED_EXPRESSION);
        return true;
    }

    private boolean rat_37(PsiBuilder builder) {
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

    private boolean sequenceelement_38_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_38_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteraltext_42(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_38_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralopentag_41(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_38_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralclosetag_40(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_38_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.commenttag_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_38(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.sequenceelement_38_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.sequenceelement_38_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.sequenceelement_38_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.sequenceelement_38_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.sequenceelement_38_alt_1(builder, opp)) {
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

    private boolean sequenceelementgroup_39_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_38(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementgroup_39(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.sequenceelementgroup_39_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        marker1.done(CroTemplateElementTypes.TAG_SEQUENCE);
        return true;
    }

    private boolean sequenceelementliteralclosetag_40_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.gt_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_40_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagname_31(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sequenceelementliteralclosetag_40_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_40_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sequenceelementliteralclosetag_40_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_40(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_32(builder))) {
            return false;
        }
        if (!(this.close_7(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sequenceelementliteralclosetag_40_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_CLOSE_TAG);
        return true;
    }

    private boolean sequenceelementliteralopentag_41_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_44(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_41_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattribute_29(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_41_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.sequenceelementliteralopentag_41_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sequenceelementliteralopentag_41_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_41_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.gt_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_41_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagname_31(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.sequenceelementliteralopentag_41_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sequenceelementliteralopentag_41_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_41(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_32(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sequenceelementliteralopentag_41_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_OPEN_TAG);
        return true;
    }

    private boolean sequenceelementliteraltext_42(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigil_43_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_43_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_43(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.sigil_43_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.sigil_43_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sigiltag_44_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltaguse_53(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagapply_45(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagbody_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagmacro_50(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagsub_51(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcall_47(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcondition_48(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagiteration_49(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagvariable_54(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagtopic_52(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_44(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.sigiltag_44_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.sigiltag_44_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.sigiltag_44_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    PsiBuilder.Marker altMarker7;;
                    altMarker7 = builder.mark();
                    if (this.sigiltag_44_alt_7(builder, opp)) {
                        altMarker7.drop();
                    } else {
                        altMarker7.rollbackTo();
                        PsiBuilder.Marker altMarker6;;
                        altMarker6 = builder.mark();
                        if (this.sigiltag_44_alt_6(builder, opp)) {
                            altMarker6.drop();
                        } else {
                            altMarker6.rollbackTo();
                            PsiBuilder.Marker altMarker5;;
                            altMarker5 = builder.mark();
                            if (this.sigiltag_44_alt_5(builder, opp)) {
                                altMarker5.drop();
                            } else {
                                altMarker5.rollbackTo();
                                PsiBuilder.Marker altMarker4;;
                                altMarker4 = builder.mark();
                                if (this.sigiltag_44_alt_4(builder, opp)) {
                                    altMarker4.drop();
                                } else {
                                    altMarker4.rollbackTo();
                                    PsiBuilder.Marker altMarker3;;
                                    altMarker3 = builder.mark();
                                    if (this.sigiltag_44_alt_3(builder, opp)) {
                                        altMarker3.drop();
                                    } else {
                                        altMarker3.rollbackTo();
                                        PsiBuilder.Marker altMarker2;;
                                        altMarker2 = builder.mark();
                                        if (this.sigiltag_44_alt_2(builder, opp)) {
                                            altMarker2.drop();
                                        } else {
                                            altMarker2.rollbackTo();
                                            PsiBuilder.Marker altMarker1;;
                                            altMarker1 = builder.mark();
                                            if (this.sigiltag_44_alt_1(builder, opp)) {
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

    private boolean sigiltagapply_45_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_45_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_45_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_45_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_45_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.tclose_57(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagapply_45_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_45_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_39(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagapply_45_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_45_quant_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagapply_45_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagapply_45_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagapply_45_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagapply_45_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_45(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagapply_45_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.APPLY);
        return true;
    }

    private boolean sigiltagbody_46_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagbody_46_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagbody_46_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagbody_46(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.declsigil_10(builder))) {
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
        if (this.sigiltagbody_46_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.BODY);
        return true;
    }

    private boolean sigiltagcall_47_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_47_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_47_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_47_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_47_quant_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SUB_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagcall_47_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagcall_47_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagcall_47_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagcall_47_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcall_47(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.callsigil_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcall_47_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CALL);
        return true;
    }

    private boolean sigiltagcondition_48_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.variable_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_48_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.block_4(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_48_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.deref_11(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_48_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.dot_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagcondition_48_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_48_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_48_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.tclose_57(builder))) {
            return false;
        }
        if (!(this.condsigil_9(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcondition_48_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_48_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_39(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagcondition_48_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_48_quant_8(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.sigiltagcondition_48_alt_4(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.sigiltagcondition_48_alt_2(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.sigiltagcondition_48_alt_1(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagcondition_48_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_48(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.condsigil_9(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagcondition_48_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CONDITION);
        return true;
    }

    private boolean sigiltagiteration_49_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_4(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COLON) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagiteration_49_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagiteration_49_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.tclose_57(builder))) {
            return false;
        }
        if (!(this.itersigil_28(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagiteration_49_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_39(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagiteration_49_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_49_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.deref_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagiteration_49_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagiteration_49_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagiteration_49_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_49(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.itersigil_28(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagiteration_49_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.ITERATION);
        return true;
    }

    private boolean sigiltagmacro_50_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_55(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("macro"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.tclose_57(builder))) {
            return false;
        }
        if (!(this.declsigil_10(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagmacro_50_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagmacro_50_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_39(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagmacro_50_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagmacro_50_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagmacro_50_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagmacro_50_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagmacro_50_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_50_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagmacro_50_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_50(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.declsigil_10(builder))) {
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
        if (this.sigiltagmacro_50_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.MACRO);
        return true;
    }

    private boolean sigiltagsub_51_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_55(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("sub"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.tclose_57(builder))) {
            return false;
        }
        if (!(this.declsigil_10(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagsub_51_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagsub_51_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_39(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagsub_51_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SUB_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagsub_51_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagsub_51_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagsub_51_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagsub_51_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_51_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagsub_51_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_51(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.declsigil_10(builder))) {
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
        if (this.sigiltagsub_51_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.SUB);
        return true;
    }

    private boolean sigiltagtopic_52_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagtopic_52_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.deref_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagtopic_52_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagtopic_52(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.dot_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagtopic_52_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean sigiltaguse_53_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_53_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_53_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_56(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltaguse_53_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltaguse_53_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_53_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltaguse_53_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_53(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if (!(this.declsigil_10(builder))) {
            return false;
        }
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt1.equals("use"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltaguse_53_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.USE);
        return true;
    }

    private boolean sigiltagvariable_54_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_11(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_54_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagvariable_54_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagvariable_54_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagvariable_54_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.tgt_59(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_54(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_60(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagvariable_54_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean signature_55_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COMMA) && (tt2.equals(","))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_55_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.parameter_35(builder))) {
            return false;
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.signature_55_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean signature_55_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_55_quant_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.signature_55_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean signature_55_quant_5(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.signature_55_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.signature_55_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean signature_55(PsiBuilder builder) {
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
        if (!(this.ws_62(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.signature_55_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.SIGNATURE);
        return true;
    }

    private boolean singlequotestring_56_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.STRING_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_56_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.STRING_QUOTE_SINGLE) && (tt2.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_56(PsiBuilder builder) {
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
        if (this.singlequotestring_56_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.singlequotestring_56_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean tclose_57(PsiBuilder builder) {
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

    private boolean term_58_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.parenthesizedexpression_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.derefterm_18(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.variable_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.int_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.rat_37(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.num_34(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_56(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_58(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.term_58_alt_7(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.term_58_alt_6(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.term_58_alt_5(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.term_58_alt_4(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker3;;
                        altMarker3 = builder.mark();
                        if (this.term_58_alt_3(builder, opp)) {
                            altMarker3.drop();
                        } else {
                            altMarker3.rollbackTo();
                            PsiBuilder.Marker altMarker2;;
                            altMarker2 = builder.mark();
                            if (this.term_58_alt_2(builder, opp)) {
                                altMarker2.drop();
                            } else {
                                altMarker2.rollbackTo();
                                PsiBuilder.Marker altMarker1;;
                                altMarker1 = builder.mark();
                                if (this.term_58_alt_1(builder, opp)) {
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
        return true;
    }

    private boolean tgt_59(PsiBuilder builder) {
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

    private boolean tlt_60(PsiBuilder builder) {
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

    private boolean variable_61_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_11(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_61_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.variable_61_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean variable_61(PsiBuilder builder) {
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
        if (this.variable_61_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean ws_62_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ws_62_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ws_62(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ws_62_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ws_62_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

}
