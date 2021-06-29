package edument.perl6idea.pod;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Trait;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class PodDomEnumDeclarator extends PodDomDeclarator {
    private final Collection<String> enumValues;

    public PodDomEnumDeclarator(int offset,
                                @NotNull String shortName,
                                @Nullable String globalName,
                                List<PsiElement> docComments,
                                Perl6Trait exportTrait,
                                Collection<String> enumValues) {
        super(offset, shortName, globalName, docComments, exportTrait);
        this.enumValues = enumValues;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        // Render the name.
        builder.append("<h3 class=\"doc-package-name\">");
        builder.append(StringEscapeUtils.escapeHtml(getPrimaryName()));
        builder.append(" <span class=\"doc-kind\">enum</span>");
        renderExportTags(builder, context);
        builder.append("</h3>");

        // Values, if any.
        if (!(enumValues == null || enumValues.isEmpty())) {
            builder.append("<table class=\"doc-prop-table\">\n");
            builder.append("<tr><td class=\"doc-prop-name\">Values</td><td>");
            for (String value : enumValues) {
                builder.append("<code>");
                builder.append(StringEscapeUtils.escapeHtml(value));
                builder.append("</code><br>");
            }
            builder.append("</td></tr>");
            builder.append("</table>\n");
        }

        // Add provided documentation.
        renderDocComments(builder, context);
    }
}
