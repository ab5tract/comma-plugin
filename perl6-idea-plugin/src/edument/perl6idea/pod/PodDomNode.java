package edument.perl6idea.pod;

public abstract class PodDomNode {
    protected final int offset;

    public PodDomNode(int offset) {this.offset = offset;}

    public abstract void renderInto(StringBuilder builder, PodRenderingContext context);

    protected void emitPositionInfo(StringBuilder builder) {
        builder.append("<span id=\"scroll-pos-").append(offset).append("\"></span>");
    }
}
