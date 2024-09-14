package org.raku.comma.pod;

public class PodDomPara extends PodDomBlock {
    public PodDomPara(int offset) {
        super(offset);
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        builder.append("<p>");
        renderChildrenInfo(builder, context);
        builder.append("</p>");
    }
}
