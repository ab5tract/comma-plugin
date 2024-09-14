package org.raku.pod;

import org.raku.utils.RakuUtils;

public class PodDomFormatted extends PodDomInnerNode {
    private final char code;
    private String link;

    public PodDomFormatted(int offset, char code) {
        super(offset);
        this.code = code;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        switch (code) {
            case 'B': renderTrival(builder, context, "strong"); break;
            case 'C': renderTrival(builder, context, "code"); break;
            case 'I': renderTrival(builder, context, "em"); break;
            case 'K': renderTrival(builder, context, "kbd"); break;
            case 'L': renderLink(builder, context); break;
            case 'R': renderTrival(builder, context, "var"); break;
            case 'T': renderTrival(builder, context, "samp"); break;
            case 'U': renderTrival(builder, context, "u"); break;
            case 'Z': break;
            default:
                renderChildrenInfo(builder, context);
        }
    }

    private void renderTrival(StringBuilder builder, PodRenderingContext context, String tag) {
        builder.append('<');
        builder.append(tag);
        builder.append('>');
        renderChildrenInfo(builder, context);
        builder.append("</");
        builder.append(tag);
        builder.append('>');
    }

    private void renderLink(StringBuilder builder, PodRenderingContext context) {
        builder.append("<a href=\"");
        if (link != null)
            builder.append(RakuUtils.escapeHTML(link));
        builder.append("\">");
        renderChildrenInfo(builder, context);
        builder.append("</a>");
    }
}
