package org.raku.comma.pod;

public class PodDomHead extends PodDomBlock {
    private final int level;

    public PodDomHead(int offset, int level) {
        super(offset);
        this.level = level;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        builder.append("<h");
        builder.append(level);
        builder.append('>');
        renderChildrenInfo(builder, context);
        builder.append("</h");
        builder.append(level);
        builder.append('>');
    }
}
