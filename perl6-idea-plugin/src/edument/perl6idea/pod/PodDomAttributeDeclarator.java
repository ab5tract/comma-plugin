package edument.perl6idea.pod;

import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PodDomAttributeDeclarator extends PodDomDeclarator {
    private final boolean rw;
    private final String type;

    public PodDomAttributeDeclarator(int offset,
                                     @NotNull String shortName,
                                     @Nullable String globalName,
                                     List<PsiElement> docComments,
                                     boolean rw, String type) {
        super(offset, shortName, globalName, docComments, null);
        this.rw = rw;
        this.type = type;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        // Render the name and documentation.
        builder.append("<h4 class=\"doc-attribute-name\">");
        builder.append(StringEscapeUtils.escapeHtml(getPrimaryName()));
        builder.append(" <span class=\"doc-kind\">attribute</span></h4>");
        if (type != null || rw) {
            builder.append("<table class=\"doc-prop-table\">\n");
            if (type != null) {
                builder.append("<tr><td class=\"doc-prop-name\">Type</td><td><code>");
                builder.append(StringEscapeUtils.escapeHtml(type));
                builder.append("</code></td></tr>");
            }
            if (rw)
                builder.append("<tr><td class=\"doc-prop-name\">Assignable (<code>rw</code>)</td><td>✓</td></tr>");
            builder.append("</table>");
        }
        renderDocComments(builder, context);
    }
}
