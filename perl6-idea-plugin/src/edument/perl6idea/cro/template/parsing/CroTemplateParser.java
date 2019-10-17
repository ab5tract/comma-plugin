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
        if (!(this.sequenceelement_28(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_1_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.bogusend_4(builder))) {
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
        if (!(this.ws_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean arglist_3_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_12(builder))) {
            return false;
        }
        if (!(this.ws_46(builder))) {
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
        if (!(this.hws_14(builder))) {
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
        if (!(this.ws_46(builder))) {
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
        if (!(this.ws_46(builder))) {
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

    private boolean bogusend_4(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.BAD_CHARACTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean close_5(PsiBuilder builder) {
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

    private boolean condsigil_6(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean declsigil_7(PsiBuilder builder) {
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

    private boolean deref_8_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitem_9(builder))) {
            return false;
        }
        return true;
    }

    private boolean deref_8_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.deref_8_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean deref_8(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.derefitem_9(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.deref_8_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean derefitem_9(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.derefitemsmart_10(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitemsmart_10(PsiBuilder builder) {
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

    private boolean dot_11(PsiBuilder builder) {
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

    private boolean expression_12_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_12_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.nextterm_25(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_12_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_46(builder))) {
            return false;
        }
        opp.startInfix();
        if (!(this.infixish_18(builder))) {
            return false;
        }
        opp.endInfix();
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.expression_12_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        opp.startPrefixes();
        opp.endPrefixes();
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.expression_12_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        opp.startPostfixes();
        opp.endPostfixes();
        return true;
    }

    private boolean expression_12_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean expression_12_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.END_OF_EXPR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean expression_12(PsiBuilder builder) {
        OPP opp;
        opp = new OPP(builder);
        opp.startExpr();
        opp.startPrefixes();
        opp.endPrefixes();
        if (!(this.term_42(builder))) {
            return false;
        }
        opp.startPostfixes();
        opp.endPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.expression_12_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        opp.endExpr();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.expression_12_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.expression_12_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean gt_13(PsiBuilder builder) {
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

    private boolean hws_14(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ident_15_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_15_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_15_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_15(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ident_15_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ident_15_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.ident_15_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean identifier_16_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ident_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean identifier_16(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ident_15(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.identifier_16_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean infix_17(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_18(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.infix_17(builder))) {
            return false;
        }
        marker1.done(CroTemplateElementTypes.INFIX);
        return true;
    }

    private boolean int_19(PsiBuilder builder) {
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

    private boolean itersigil_20(PsiBuilder builder) {
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

    private boolean literaltagattribute_21_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattributevalue_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattribute_21_quant_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_EQUALS) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_46(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.literaltagattribute_21_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattribute_21(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_46(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.literaltagattribute_21_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_TAG_ATTRIBUTE);
        return true;
    }

    private boolean literaltagattributevalue_22_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_22_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt2.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_22_alt_3(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt1.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.literaltagattributevalue_22_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_22_quant_4(PsiBuilder builder, OPP opp) {
        String tt4;
        tt4 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt4.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_22_alt_5(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt3.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.literaltagattributevalue_22_quant_4(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_22(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.literaltagattributevalue_22_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.literaltagattributevalue_22_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.literaltagattributevalue_22_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagname_23(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean lt_24(PsiBuilder builder) {
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

    private boolean nextterm_25(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.term_42(builder))) {
            return false;
        }
        return true;
    }

    private boolean num_26(PsiBuilder builder) {
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

    private boolean rat_27(PsiBuilder builder) {
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

    private boolean sequenceelement_28_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_33(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_28_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteraltext_31(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_28_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralopentag_30(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_28_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralclosetag_29(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_28(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.sequenceelement_28_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.sequenceelement_28_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.sequenceelement_28_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.sequenceelement_28_alt_1(builder, opp)) {
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

    private boolean sequenceelementliteralclosetag_29_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.gt_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_29_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.ws_46(builder))) {
            return false;
        }
        if (!(this.literaltagname_23(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sequenceelementliteralclosetag_29_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_29(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_24(builder))) {
            return false;
        }
        if (!(this.close_5(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sequenceelementliteralclosetag_29_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_CLOSE_TAG);
        return true;
    }

    private boolean sequenceelementliteralopentag_30_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_33(builder))) {
            return false;
        }
        if (!(this.ws_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_30_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattribute_21(builder))) {
            return false;
        }
        if (!(this.ws_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_30_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.sequenceelementliteralopentag_30_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sequenceelementliteralopentag_30_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_30_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.gt_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_30_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagname_23(builder))) {
            return false;
        }
        if (!(this.ws_46(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.sequenceelementliteralopentag_30_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sequenceelementliteralopentag_30_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_30(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_24(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sequenceelementliteralopentag_30_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_OPEN_TAG);
        return true;
    }

    private boolean sequenceelementliteraltext_31(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigil_32_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_32_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_32(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.sigil_32_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.sigil_32_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sigiltag_33_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagapply_34(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_33_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltaguse_38(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_33_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcondition_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_33_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagiteration_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_33_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagvariable_39(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_33_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagtopic_37(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_33(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker6;
        altMarker6 = builder.mark();
        if (this.sigiltag_33_alt_6(builder, opp)) {
            altMarker6.drop();
        } else {
            altMarker6.rollbackTo();
            PsiBuilder.Marker altMarker5;;
            altMarker5 = builder.mark();
            if (this.sigiltag_33_alt_5(builder, opp)) {
                altMarker5.drop();
            } else {
                altMarker5.rollbackTo();
                PsiBuilder.Marker altMarker4;;
                altMarker4 = builder.mark();
                if (this.sigiltag_33_alt_4(builder, opp)) {
                    altMarker4.drop();
                } else {
                    altMarker4.rollbackTo();
                    PsiBuilder.Marker altMarker3;;
                    altMarker3 = builder.mark();
                    if (this.sigiltag_33_alt_3(builder, opp)) {
                        altMarker3.drop();
                    } else {
                        altMarker3.rollbackTo();
                        PsiBuilder.Marker altMarker2;;
                        altMarker2 = builder.mark();
                        if (this.sigiltag_33_alt_2(builder, opp)) {
                            altMarker2.drop();
                        } else {
                            altMarker2.rollbackTo();
                            PsiBuilder.Marker altMarker1;;
                            altMarker1 = builder.mark();
                            if (this.sigiltag_33_alt_1(builder, opp)) {
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

    private boolean sigiltagapply_34_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_28(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.tclose_41(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagapply_34_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker5;;
            quantMarker5 = builder.mark();
            if (this.sigiltagapply_34_quant_4(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagapply_34_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_34_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagapply_34_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagapply_34_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagapply_34_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagapply_34_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_34(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagapply_34_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.APPLY);
        return true;
    }

    private boolean sigiltagcondition_35_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_35_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_28(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_35_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_35_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.tclose_41(builder))) {
            return false;
        }
        if (!(this.condsigil_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagcondition_35_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_35_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.sigiltagcondition_35_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagcondition_35_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_35_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.dot_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagcondition_35_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcondition_35_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_35(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.condsigil_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagcondition_35_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CONDITION);
        return true;
    }

    private boolean sigiltagiteration_36_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_28(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_36_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_36_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.tclose_41(builder))) {
            return false;
        }
        if (!(this.itersigil_20(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagiteration_36_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_36_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.sigiltagiteration_36_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagiteration_36_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_36_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.deref_8(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagiteration_36_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_36(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.itersigil_20(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagiteration_36_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.ITERATION);
        return true;
    }

    private boolean sigiltagtopic_37_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagtopic_37_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.deref_8(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagtopic_37_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagtopic_37(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.dot_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagtopic_37_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean sigiltaguse_38_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_38_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_38_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_40(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltaguse_38_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltaguse_38_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_38_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.hws_14(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltaguse_38_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_38(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.declsigil_7(builder))) {
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
        if (this.sigiltaguse_38_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.USE);
        return true;
    }

    private boolean sigiltagvariable_39_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_8(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_39_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_11(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagvariable_39_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagvariable_39_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_39_quant_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.IDENTIFER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagvariable_39_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagvariable_39_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagvariable_39(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_44(builder))) {
            return false;
        }
        if (!(this.varsigil_45(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagvariable_39_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean singlequotestring_40_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.STRING_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_40_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.STRING_QUOTE_SINGLE) && (tt2.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_40(PsiBuilder builder) {
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
        if (this.singlequotestring_40_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.singlequotestring_40_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean tclose_41(PsiBuilder builder) {
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

    private boolean term_42_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.num_26(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_42_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.rat_27(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_42_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.int_19(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_42_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_40(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_42(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.term_42_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.term_42_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.term_42_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.term_42_alt_1(builder, opp)) {
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

    private boolean tgt_43(PsiBuilder builder) {
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

    private boolean tlt_44(PsiBuilder builder) {
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

    private boolean varsigil_45(PsiBuilder builder) {
        OPP opp;
        opp = null;
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_VAR_SIGIL) && (tt1.equals("$"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ws_46_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ws_46_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ws_46(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ws_46_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ws_46_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

}
