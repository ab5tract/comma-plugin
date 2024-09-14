package org.raku.comma.pod;

public class PodDomNodeList extends PodDomInnerNode {
    public PodDomNodeList(int offset) {
        super(offset);
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        renderChildrenInfo(builder, context);
    }
}
