package edument.perl6idea.pod;

import edument.perl6idea.utils.Perl6Utils;

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
        builder.append(Perl6Utils.escapeHTML(text));
    }
}
