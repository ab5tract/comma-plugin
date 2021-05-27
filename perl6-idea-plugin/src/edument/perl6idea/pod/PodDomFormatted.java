package edument.perl6idea.pod;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;

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
    public void renderInto(StringBuilder builder) {
        switch (code) {
            case 'B': renderTrival(builder, "strong"); break;
            case 'C': renderTrival(builder, "code"); break;
            case 'I': renderTrival(builder, "em"); break;
            case 'K': renderTrival(builder, "kbd"); break;
            case 'L': renderLink(builder); break;
            case 'R': renderTrival(builder, "var"); break;
            case 'T': renderTrival(builder, "samp"); break;
            case 'U': renderTrival(builder, "u"); break;
            case 'Z': break;
            default:
                renderChlidrenInto(builder);
        }
    }

    private void renderTrival(StringBuilder builder, String tag) {
        builder.append('<');
        builder.append(tag);
        builder.append('>');
        renderChlidrenInto(builder);
        builder.append("</");
        builder.append(tag);
        builder.append('>');
    }

    private void renderLink(StringBuilder builder) {
        builder.append("<a href=\"");
        if (link != null)
            builder.append(StringEscapeUtils.escapeHtml(link));
        builder.append("\">");
        renderChlidrenInto(builder);
        builder.append("</a>");
    }
}
