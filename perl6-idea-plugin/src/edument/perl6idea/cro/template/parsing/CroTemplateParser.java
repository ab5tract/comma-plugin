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
        if (!(this.sequenceelement_37(builder))) {
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
        if (!(this.ws_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean arglist_3_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_19(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
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
        if (!(this.hws_21(builder))) {
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
        if (!(this.ws_61(builder))) {
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
        if (!(this.ws_61(builder))) {
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
        if (!(this.expression_19(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
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
        if (!(this.ws_61(builder))) {
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

    private boolean condsigil_8(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.TEMPLATE_TAG_COND_SIGIL) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean declsigil_9(PsiBuilder builder) {
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

    private boolean deref_10_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitem_11(builder))) {
            return false;
        }
        return true;
    }

    private boolean deref_10_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_18(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.deref_10_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        return true;
    }

    private boolean deref_10(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.derefitem_11(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.deref_10_quant_2(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean derefitem_11_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemhashliteral_14(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_11_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemhash_13(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_11_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemarray_12(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_11_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemsmart_16(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_11_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.derefitemmethod_15(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefitem_11(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.derefitem_11_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.derefitem_11_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                PsiBuilder.Marker altMarker3;;
                altMarker3 = builder.mark();
                if (this.derefitem_11_alt_3(builder, opp)) {
                    altMarker3.drop();
                } else {
                    altMarker3.rollbackTo();
                    PsiBuilder.Marker altMarker2;;
                    altMarker2 = builder.mark();
                    if (this.derefitem_11_alt_2(builder, opp)) {
                        altMarker2.drop();
                    } else {
                        altMarker2.rollbackTo();
                        PsiBuilder.Marker altMarker1;;
                        altMarker1 = builder.mark();
                        if (this.derefitem_11_alt_1(builder, opp)) {
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

    private boolean derefitemarray_12_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_BRACKET) && (tt2.equals("]"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemarray_12_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemarray_12_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemarray_12(PsiBuilder builder) {
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
        if (this.derefitemarray_12_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_ARRAY);
        return true;
    }

    private boolean derefitemhash_13_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_CURLY) && (tt2.equals("}"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemhash_13_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_19(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemhash_13_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemhash_13(PsiBuilder builder) {
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
        if (this.derefitemhash_13_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_HASH);
        return true;
    }

    private boolean derefitemhashliteral_14_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_ANGLE) && (tt2.equals(">"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemhashliteral_14_quant_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_KEY) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemhashliteral_14_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean derefitemhashliteral_14(PsiBuilder builder) {
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
        if (this.derefitemhashliteral_14_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_HASH_LITERAL);
        return true;
    }

    private boolean derefitemmethod_15_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean derefitemmethod_15(PsiBuilder builder) {
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
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefitemmethod_15_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.DEREF_METHOD);
        return true;
    }

    private boolean derefitemsmart_16(PsiBuilder builder) {
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

    private boolean derefterm_17_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_10(builder))) {
            return false;
        }
        return true;
    }

    private boolean derefterm_17(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.dot_18(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.derefterm_17_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean dot_18(PsiBuilder builder) {
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

    private boolean expression_19_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ws_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_19_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.nextterm_32(builder))) {
            return false;
        }
        return true;
    }

    private boolean expression_19_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.ws_61(builder))) {
            return false;
        }
        opp.startInfix();
        if (!(this.infixish_25(builder))) {
            return false;
        }
        opp.endInfix();
        PsiBuilder.Marker quantMarker1;
        quantMarker1 = builder.mark();
        if (this.expression_19_quant_1(builder, opp)) {
            quantMarker1.drop();
        } else {
            quantMarker1.rollbackTo();
        }
        opp.startPrefixes();
        opp.endPrefixes();
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.expression_19_quant_2(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        opp.startPostfixes();
        opp.endPostfixes();
        return true;
    }

    private boolean expression_19_alt_4(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean expression_19_alt_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.END_OF_EXPR) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean expression_19(PsiBuilder builder) {
        OPP opp;
        opp = new OPP(builder);
        opp.startExpr();
        opp.startPrefixes();
        opp.endPrefixes();
        if (!(this.term_57(builder))) {
            return false;
        }
        opp.startPostfixes();
        opp.endPostfixes();
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.expression_19_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        opp.endExpr();
        PsiBuilder.Marker altMarker5;
        altMarker5 = builder.mark();
        if (this.expression_19_alt_5(builder, opp)) {
            altMarker5.drop();
        } else {
            altMarker5.rollbackTo();
            PsiBuilder.Marker altMarker4;;
            altMarker4 = builder.mark();
            if (this.expression_19_alt_4(builder, opp)) {
                altMarker4.drop();
            } else {
                altMarker4.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean gt_20(PsiBuilder builder) {
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

    private boolean hws_21(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ident_22_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_22_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_22_quant_3(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ident_22(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ident_22_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ident_22_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.ident_22_quant_3(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean identifier_23_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.ident_22(builder))) {
            return false;
        }
        return true;
    }

    private boolean identifier_23(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.ident_22(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker1;;
            quantMarker1 = builder.mark();
            if (this.identifier_23_quant_1(builder, opp)) {
                quantMarker1.drop();
            } else {
                quantMarker1.rollbackTo();
                break;
            }
        }
        return true;
    }

    private boolean infix_24(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.INFIX) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean infixish_25(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.infix_24(builder))) {
            return false;
        }
        marker1.done(CroTemplateElementTypes.INFIX);
        return true;
    }

    private boolean int_26(PsiBuilder builder) {
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

    private boolean itersigil_27(PsiBuilder builder) {
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

    private boolean literaltagattribute_28_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattributevalue_29(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattribute_28_quant_2(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_EQUALS) && (tt1.equals("="))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.literaltagattribute_28_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattribute_28(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.literaltagattribute_28_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_TAG_ATTRIBUTE);
        return true;
    }

    private boolean literaltagattributevalue_29_alt_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) && (tt2.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_4(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_quant_5(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.literaltagattributevalue_29_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.literaltagattributevalue_29_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.literaltagattributevalue_29_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagattributevalue_29_quant_6(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt3.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_7(PsiBuilder builder, OPP opp) {
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
            if (this.literaltagattributevalue_29_quant_5(builder, opp)) {
                quantMarker5.drop();
            } else {
                quantMarker5.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.literaltagattributevalue_29_quant_6(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_8(PsiBuilder builder, OPP opp) {
        String tt5;
        tt5 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) && (tt5.equals("<"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_10(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_VALUE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_quant_11(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.literaltagattributevalue_29_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.literaltagattributevalue_29_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.literaltagattributevalue_29_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagattributevalue_29_quant_12(PsiBuilder builder, OPP opp) {
        String tt6;
        tt6 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.ATTRIBUTE_QUOTE) && (tt6.equals("\""))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean literaltagattributevalue_29_alt_13(PsiBuilder builder, OPP opp) {
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
            if (this.literaltagattributevalue_29_quant_11(builder, opp)) {
                quantMarker11.drop();
            } else {
                quantMarker11.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker12;
        quantMarker12 = builder.mark();
        if (this.literaltagattributevalue_29_quant_12(builder, opp)) {
            quantMarker12.drop();
        } else {
            quantMarker12.rollbackTo();
        }
        return true;
    }

    private boolean literaltagattributevalue_29(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker13;
        altMarker13 = builder.mark();
        if (this.literaltagattributevalue_29_alt_13(builder, opp)) {
            altMarker13.drop();
        } else {
            altMarker13.rollbackTo();
            PsiBuilder.Marker altMarker7;;
            altMarker7 = builder.mark();
            if (this.literaltagattributevalue_29_alt_7(builder, opp)) {
                altMarker7.drop();
            } else {
                altMarker7.rollbackTo();
                PsiBuilder.Marker altMarker1;;
                altMarker1 = builder.mark();
                if (this.literaltagattributevalue_29_alt_1(builder, opp)) {
                    altMarker1.drop();
                } else {
                    altMarker1.rollbackTo();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean literaltagname_30(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TAG_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean lt_31(PsiBuilder builder) {
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

    private boolean nextterm_32(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.term_57(builder))) {
            return false;
        }
        return true;
    }

    private boolean num_33(PsiBuilder builder) {
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

    private boolean parameter_34(PsiBuilder builder) {
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

    private boolean parenthesizedexpression_35_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt2.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean parenthesizedexpression_35_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.expression_19(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.parenthesizedexpression_35_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean parenthesizedexpression_35(PsiBuilder builder) {
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
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.parenthesizedexpression_35_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.PARENTHESIZED_EXPRESSION);
        return true;
    }

    private boolean rat_36(PsiBuilder builder) {
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

    private boolean sequenceelement_37_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_43(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_37_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteraltext_41(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_37_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralopentag_40(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_37_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteralclosetag_39(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_37(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.sequenceelement_37_alt_4(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker3;;
            altMarker3 = builder.mark();
            if (this.sequenceelement_37_alt_3(builder, opp)) {
                altMarker3.drop();
            } else {
                altMarker3.rollbackTo();
                PsiBuilder.Marker altMarker2;;
                altMarker2 = builder.mark();
                if (this.sequenceelement_37_alt_2(builder, opp)) {
                    altMarker2.drop();
                } else {
                    altMarker2.rollbackTo();
                    PsiBuilder.Marker altMarker1;;
                    altMarker1 = builder.mark();
                    if (this.sequenceelement_37_alt_1(builder, opp)) {
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

    private boolean sequenceelementgroup_38_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelement_37(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementgroup_38(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        while (true) {
            PsiBuilder.Marker quantMarker2;;
            quantMarker2 = builder.mark();
            if (this.sequenceelementgroup_38_quant_1(builder, opp)) {
                quantMarker2.drop();
            } else {
                quantMarker2.rollbackTo();
                break;
            }
        }
        marker1.done(CroTemplateElementTypes.TAG_SEQUENCE);
        return true;
    }

    private boolean sequenceelementliteralclosetag_39_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.gt_20(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_39_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.ws_61(builder))) {
            return false;
        }
        if (!(this.literaltagname_30(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sequenceelementliteralclosetag_39_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralclosetag_39(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_31(builder))) {
            return false;
        }
        if (!(this.close_7(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sequenceelementliteralclosetag_39_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_CLOSE_TAG);
        return true;
    }

    private boolean sequenceelementliteralopentag_40_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_43(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_40_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagattribute_28(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_40_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker3;
        altMarker3 = builder.mark();
        if (this.sequenceelementliteralopentag_40_alt_2(builder, opp)) {
            altMarker3.drop();
        } else {
            altMarker3.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sequenceelementliteralopentag_40_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_40_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.gt_20(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_40_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.literaltagname_30(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        while (true) {
            PsiBuilder.Marker quantMarker4;;
            quantMarker4 = builder.mark();
            if (this.sequenceelementliteralopentag_40_quant_3(builder, opp)) {
                quantMarker4.drop();
            } else {
                quantMarker4.rollbackTo();
                break;
            }
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sequenceelementliteralopentag_40_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sequenceelementliteralopentag_40(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.lt_31(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sequenceelementliteralopentag_40_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.LITERAL_OPEN_TAG);
        return true;
    }

    private boolean sequenceelementliteraltext_41(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigil_42_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_42_alt_2(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean sigil_42(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.sigil_42_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.sigil_42_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sigiltag_43_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltaguse_52(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagapply_44(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagbody_45(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagmacro_49(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagsub_50(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcall_46(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagcondition_47(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_8(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagiteration_48(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_9(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagvariable_53(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43_alt_10(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltagtopic_51(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltag_43(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker10;
        altMarker10 = builder.mark();
        if (this.sigiltag_43_alt_10(builder, opp)) {
            altMarker10.drop();
        } else {
            altMarker10.rollbackTo();
            PsiBuilder.Marker altMarker9;;
            altMarker9 = builder.mark();
            if (this.sigiltag_43_alt_9(builder, opp)) {
                altMarker9.drop();
            } else {
                altMarker9.rollbackTo();
                PsiBuilder.Marker altMarker8;;
                altMarker8 = builder.mark();
                if (this.sigiltag_43_alt_8(builder, opp)) {
                    altMarker8.drop();
                } else {
                    altMarker8.rollbackTo();
                    PsiBuilder.Marker altMarker7;;
                    altMarker7 = builder.mark();
                    if (this.sigiltag_43_alt_7(builder, opp)) {
                        altMarker7.drop();
                    } else {
                        altMarker7.rollbackTo();
                        PsiBuilder.Marker altMarker6;;
                        altMarker6 = builder.mark();
                        if (this.sigiltag_43_alt_6(builder, opp)) {
                            altMarker6.drop();
                        } else {
                            altMarker6.rollbackTo();
                            PsiBuilder.Marker altMarker5;;
                            altMarker5 = builder.mark();
                            if (this.sigiltag_43_alt_5(builder, opp)) {
                                altMarker5.drop();
                            } else {
                                altMarker5.rollbackTo();
                                PsiBuilder.Marker altMarker4;;
                                altMarker4 = builder.mark();
                                if (this.sigiltag_43_alt_4(builder, opp)) {
                                    altMarker4.drop();
                                } else {
                                    altMarker4.rollbackTo();
                                    PsiBuilder.Marker altMarker3;;
                                    altMarker3 = builder.mark();
                                    if (this.sigiltag_43_alt_3(builder, opp)) {
                                        altMarker3.drop();
                                    } else {
                                        altMarker3.rollbackTo();
                                        PsiBuilder.Marker altMarker2;;
                                        altMarker2 = builder.mark();
                                        if (this.sigiltag_43_alt_2(builder, opp)) {
                                            altMarker2.drop();
                                        } else {
                                            altMarker2.rollbackTo();
                                            PsiBuilder.Marker altMarker1;;
                                            altMarker1 = builder.mark();
                                            if (this.sigiltag_43_alt_1(builder, opp)) {
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

    private boolean sigiltagapply_44_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_44_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_44_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_44_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagapply_44_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.tclose_56(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagapply_44_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_44_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_38(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagapply_44_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_44_quant_7(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagapply_44_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagapply_44_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagapply_44_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagapply_44_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagapply_44(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.applysigil_2(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagapply_44_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.APPLY);
        return true;
    }

    private boolean sigiltagbody_45_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagbody_45_quant_2(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagbody_45_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagbody_45(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.declsigil_9(builder))) {
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
        if (this.sigiltagbody_45_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.BODY);
        return true;
    }

    private boolean sigiltagcall_46_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_46_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.arglist_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_46_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_46_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcall_46_quant_5(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SUB_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagcall_46_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagcall_46_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagcall_46_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagcall_46_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcall_46(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.callsigil_6(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcall_46_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CALL);
        return true;
    }

    private boolean sigiltagcondition_47_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.block_4(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_47_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.deref_10(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_47_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.dot_18(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagcondition_47_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_47_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagcondition_47_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.tclose_56(builder))) {
            return false;
        }
        if (!(this.condsigil_8(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagcondition_47_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_47_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_38(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagcondition_47_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_47_quant_7(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker altMarker4;
        altMarker4 = builder.mark();
        if (this.sigiltagcondition_47_alt_3(builder, opp)) {
            altMarker4.drop();
        } else {
            altMarker4.rollbackTo();
            PsiBuilder.Marker altMarker2;;
            altMarker2 = builder.mark();
            if (this.sigiltagcondition_47_alt_1(builder, opp)) {
                altMarker2.drop();
            } else {
                altMarker2.rollbackTo();
                return false;
            }
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagcondition_47_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagcondition_47(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.condsigil_8(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagcondition_47_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.CONDITION);
        return true;
    }

    private boolean sigiltagiteration_48_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_3(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_4(PsiBuilder builder, OPP opp) {
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COLON) && (tt1.equals(":"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagiteration_48_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagiteration_48_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.tclose_56(builder))) {
            return false;
        }
        if (!(this.itersigil_27(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagiteration_48_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_38(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagiteration_48_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_48_quant_8(PsiBuilder builder, OPP opp) {
        if (!(this.deref_10(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagiteration_48_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagiteration_48_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagiteration_48_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagiteration_48(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.itersigil_27(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagiteration_48_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.ITERATION);
        return true;
    }

    private boolean sigiltagmacro_49_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_54(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("macro"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.tclose_56(builder))) {
            return false;
        }
        if (!(this.declsigil_9(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagmacro_49_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagmacro_49_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_38(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagmacro_49_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.MACRO_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagmacro_49_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagmacro_49_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagmacro_49_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagmacro_49_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_49_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagmacro_49_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagmacro_49(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.declsigil_9(builder))) {
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
        if (this.sigiltagmacro_49_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.MACRO);
        return true;
    }

    private boolean sigiltagsub_50_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.signature_54(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_4(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.DECL_OPENER) && (tt2.equals("sub"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_5(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_6(PsiBuilder builder, OPP opp) {
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.tclose_56(builder))) {
            return false;
        }
        if (!(this.declsigil_9(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.sigiltagsub_50_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.sigiltagsub_50_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_7(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        if (!(this.sequenceelementgroup_38(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker7;
        quantMarker7 = builder.mark();
        if (this.sigiltagsub_50_quant_6(builder, opp)) {
            quantMarker7.drop();
        } else {
            quantMarker7.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_8(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SUB_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagsub_50_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagsub_50_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagsub_50_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        PsiBuilder.Marker quantMarker8;
        quantMarker8 = builder.mark();
        if (this.sigiltagsub_50_quant_7(builder, opp)) {
            quantMarker8.drop();
        } else {
            quantMarker8.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_50_quant_9(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker9;
        quantMarker9 = builder.mark();
        if (this.sigiltagsub_50_quant_8(builder, opp)) {
            quantMarker9.drop();
        } else {
            quantMarker9.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagsub_50(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.declsigil_9(builder))) {
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
        if (this.sigiltagsub_50_quant_9(builder, opp)) {
            quantMarker10.drop();
        } else {
            quantMarker10.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.SUB);
        return true;
    }

    private boolean sigiltagtopic_51_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagtopic_51_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.deref_10(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagtopic_51_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagtopic_51(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.dot_18(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagtopic_51_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.TOPIC_ACCESS);
        return true;
    }

    private boolean sigiltaguse_52_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_52_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_52_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_55(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltaguse_52_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltaguse_52_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_52_quant_4(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltaguse_52_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean sigiltaguse_52(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if (!(this.declsigil_9(builder))) {
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
        if (this.sigiltaguse_52_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.USE);
        return true;
    }

    private boolean sigiltagvariable_53_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_10(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_53_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_18(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltagvariable_53_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean sigiltagvariable_53_quant_3(PsiBuilder builder, OPP opp) {
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.sigiltagvariable_53_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        if (!(this.tgt_58(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltagvariable_53(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        if (!(this.tlt_59(builder))) {
            return false;
        }
        if ((builder.getTokenType()) == CroTemplateTokenTypes.VARIABLE_NAME) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.sigiltagvariable_53_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean signature_54_quant_1(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.COMMA) && (tt2.equals(","))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_54_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.parameter_34(builder))) {
            return false;
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.signature_54_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean signature_54_quant_3(PsiBuilder builder, OPP opp) {
        if (!(this.hws_21(builder))) {
            return false;
        }
        return true;
    }

    private boolean signature_54_quant_4(PsiBuilder builder, OPP opp) {
        String tt3;
        tt3 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.CLOSE_PAREN) && (tt3.equals(")"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker4;
        quantMarker4 = builder.mark();
        if (this.signature_54_quant_3(builder, opp)) {
            quantMarker4.drop();
        } else {
            quantMarker4.rollbackTo();
        }
        return true;
    }

    private boolean signature_54_quant_5(PsiBuilder builder, OPP opp) {
        while (true) {
            PsiBuilder.Marker quantMarker3;;
            quantMarker3 = builder.mark();
            if (this.signature_54_quant_2(builder, opp)) {
                quantMarker3.drop();
            } else {
                quantMarker3.rollbackTo();
                break;
            }
        }
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker5;
        quantMarker5 = builder.mark();
        if (this.signature_54_quant_4(builder, opp)) {
            quantMarker5.drop();
        } else {
            quantMarker5.rollbackTo();
        }
        return true;
    }

    private boolean signature_54(PsiBuilder builder) {
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
        if (!(this.ws_61(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker6;
        quantMarker6 = builder.mark();
        if (this.signature_54_quant_5(builder, opp)) {
            quantMarker6.drop();
        } else {
            quantMarker6.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.SIGNATURE);
        return true;
    }

    private boolean singlequotestring_55_quant_1(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.STRING_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_55_quant_2(PsiBuilder builder, OPP opp) {
        String tt2;
        tt2 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.STRING_QUOTE_SINGLE) && (tt2.equals("'"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean singlequotestring_55(PsiBuilder builder) {
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
        if (this.singlequotestring_55_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        PsiBuilder.Marker quantMarker3;
        quantMarker3 = builder.mark();
        if (this.singlequotestring_55_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.STRING_LITERAL);
        return true;
    }

    private boolean tclose_56(PsiBuilder builder) {
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

    private boolean term_57_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.parenthesizedexpression_35(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.derefterm_17(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57_alt_3(PsiBuilder builder, OPP opp) {
        if (!(this.variable_60(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57_alt_4(PsiBuilder builder, OPP opp) {
        if (!(this.int_26(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57_alt_5(PsiBuilder builder, OPP opp) {
        if (!(this.rat_36(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57_alt_6(PsiBuilder builder, OPP opp) {
        if (!(this.num_33(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57_alt_7(PsiBuilder builder, OPP opp) {
        if (!(this.singlequotestring_55(builder))) {
            return false;
        }
        return true;
    }

    private boolean term_57(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker7;
        altMarker7 = builder.mark();
        if (this.term_57_alt_7(builder, opp)) {
            altMarker7.drop();
        } else {
            altMarker7.rollbackTo();
            PsiBuilder.Marker altMarker6;;
            altMarker6 = builder.mark();
            if (this.term_57_alt_6(builder, opp)) {
                altMarker6.drop();
            } else {
                altMarker6.rollbackTo();
                PsiBuilder.Marker altMarker5;;
                altMarker5 = builder.mark();
                if (this.term_57_alt_5(builder, opp)) {
                    altMarker5.drop();
                } else {
                    altMarker5.rollbackTo();
                    PsiBuilder.Marker altMarker4;;
                    altMarker4 = builder.mark();
                    if (this.term_57_alt_4(builder, opp)) {
                        altMarker4.drop();
                    } else {
                        altMarker4.rollbackTo();
                        PsiBuilder.Marker altMarker3;;
                        altMarker3 = builder.mark();
                        if (this.term_57_alt_3(builder, opp)) {
                            altMarker3.drop();
                        } else {
                            altMarker3.rollbackTo();
                            PsiBuilder.Marker altMarker2;;
                            altMarker2 = builder.mark();
                            if (this.term_57_alt_2(builder, opp)) {
                                altMarker2.drop();
                            } else {
                                altMarker2.rollbackTo();
                                PsiBuilder.Marker altMarker1;;
                                altMarker1 = builder.mark();
                                if (this.term_57_alt_1(builder, opp)) {
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

    private boolean tgt_58(PsiBuilder builder) {
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

    private boolean tlt_59(PsiBuilder builder) {
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

    private boolean variable_60_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.deref_10(builder))) {
            return false;
        }
        return true;
    }

    private boolean variable_60_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.dot_18(builder))) {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.variable_60_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        return true;
    }

    private boolean variable_60(PsiBuilder builder) {
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
        if (this.variable_60_quant_2(builder, opp)) {
            quantMarker3.drop();
        } else {
            quantMarker3.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.VARIABLE_ACCESS);
        return true;
    }

    private boolean ws_61_alt_1(PsiBuilder builder, OPP opp) {
        return true;
    }

    private boolean ws_61_alt_2(PsiBuilder builder, OPP opp) {
        if ((builder.getTokenType()) == CroTemplateTokenTypes.SYNTAX_WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean ws_61(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.ws_61_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.ws_61_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

}
