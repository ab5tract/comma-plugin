package edument.perl6idea.pod;

public class PodDomItem extends PodDomBlock {
    private final int level;

    public PodDomItem(int offset, int level) {
        super(offset);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        builder.append("<li>");
        renderChlidrenInto(builder, context);
        builder.append("</li>");
    }
}
