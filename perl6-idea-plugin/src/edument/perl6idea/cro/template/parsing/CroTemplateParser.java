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
        if (!(this.sequenceelement_4(builder))) {
            return false;
        }
        return true;
    }

    private boolean TOP_1_quant_2(PsiBuilder builder, OPP opp) {
        if (!(this.bogusend_2(builder))) {
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

    private boolean bogusend_2(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.BAD_CHARACTER) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean hws_3(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.WHITE_SPACE) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_4_alt_1(PsiBuilder builder, OPP opp) {
        if (!(this.sigiltag_6(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_4_alt_2(PsiBuilder builder, OPP opp) {
        if (!(this.sequenceelementliteraltext_5(builder))) {
            return false;
        }
        return true;
    }

    private boolean sequenceelement_4(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker altMarker2;
        altMarker2 = builder.mark();
        if (this.sequenceelement_4_alt_2(builder, opp)) {
            altMarker2.drop();
        } else {
            altMarker2.rollbackTo();
            PsiBuilder.Marker altMarker1;;
            altMarker1 = builder.mark();
            if (this.sequenceelement_4_alt_1(builder, opp)) {
                altMarker1.drop();
            } else {
                altMarker1.rollbackTo();
                return false;
            }
        }
        return true;
    }

    private boolean sequenceelementliteraltext_5(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if ((builder.getTokenType()) == CroTemplateTokenTypes.LITERAL_TEXT) {
            builder.advanceLexer();
        } else {
            return false;
        }
        return true;
    }

    private boolean sigiltag_6(PsiBuilder builder) {
        OPP opp;
        opp = null;
        if (!(this.sigiltaguse_7(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_7_quant_1(PsiBuilder builder, OPP opp) {
        if (!(this.hws_3(builder))) {
            return false;
        }
        return true;
    }

    private boolean sigiltaguse_7(PsiBuilder builder) {
        OPP opp;
        opp = null;
        PsiBuilder.Marker marker1;
        marker1 = builder.mark();
        String tt1;
        tt1 = builder.getTokenText();
        if (((builder.getTokenType()) == CroTemplateTokenTypes.USE_OPENER) && (tt1.equals("<:use"))) {
            builder.advanceLexer();
        } else {
            return false;
        }
        PsiBuilder.Marker quantMarker2;
        quantMarker2 = builder.mark();
        if (this.sigiltaguse_7_quant_1(builder, opp)) {
            quantMarker2.drop();
        } else {
            quantMarker2.rollbackTo();
        }
        marker1.done(CroTemplateElementTypes.USE);
        return true;
    }

}
