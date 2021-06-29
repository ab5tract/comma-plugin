package edument.perl6idea.pod;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Trait;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PodDomRoutineDeclarator extends PodDomDeclarator {
    private final String routineKind;
    private final PodDomParameterDeclarator[] parameters;
    private final String returnType;

    public PodDomRoutineDeclarator(int offset, @NotNull String shortName,
           @Nullable String globalName, List<PsiElement> docComments,
           @Nullable Perl6Trait exportTrait, @NotNull String routineKind,
           PodDomParameterDeclarator[] parameters, @Nullable String returnType) {
        super(offset, shortName, globalName, docComments, exportTrait);
        this.routineKind = routineKind;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        // Render the name.
        builder.append("<h4 class=\"doc-routine-name\">");
        builder.append(getPrimaryName());
        builder.append(" <span class=\"doc-kind\">");
        builder.append(routineKind);
        builder.append("</span>");
        renderExportTags(builder, context);
        builder.append("</h4>");

        // Parameters and return type.
        if (parameters.length > 0 || returnType != null) {
            builder.append("<table class=\"doc-prop-table\">\n");
            if (parameters.length > 0) {
                builder.append("<tr><td rowspan=\"");
                builder.append(parameters.length);
                builder.append("\" class=\"doc-prop-name\">Parameters</td>");
                for (int i = 0; i < parameters.length; i++) {
                    builder.append(i == 0 ? "<td>" : "<tr><td>");
                    parameters[i].renderInto(builder, context);
                    builder.append("</td></tr>");
                }
            }
            if (returnType != null) {
                builder.append("<tr><td class=\"doc-prop-name\">Return Type</td><td><code>");
                builder.append(returnType);
                builder.append("</code></td></tr>");
            }
            builder.append("</table>\n");
        }

        // Add provided documentation.
        renderDocComments(builder, context);
    }
}
