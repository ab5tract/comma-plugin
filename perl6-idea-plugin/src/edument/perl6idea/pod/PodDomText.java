package edument.perl6idea.pod;

import org.apache.commons.lang.StringEscapeUtils;

public class PodDomText extends PodDomNode {
    private final String text;

    public PodDomText(int offset, String text) {
        super(offset);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        builder.append(StringEscapeUtils.escapeHtml(text));
    }
}
