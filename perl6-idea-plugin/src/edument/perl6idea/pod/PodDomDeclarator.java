package edument.perl6idea.pod;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/* The base of all nodes that indicate documentable program elements. */
public abstract class PodDomDeclarator extends PodDomNode {
    // The set of things that the element is exported as, if any.
    private final List<String> exportTags = new ArrayList<>();

    // The element's short name, which it would be exported as.
    private final String shortName;

    // The global name, if it has one.
    private final String globalName;

    // The Pod documentation comments.
    private final List<PsiElement> docComments;

    public PodDomDeclarator(int offset, @NotNull String shortName, @Nullable String globalName,
                List<PsiElement> docComments) {
        super(offset);
        this.shortName = shortName;
        this.globalName = globalName;
        this.docComments = docComments;
    }

    public void addExportTag(String tag) {
        exportTags.add(tag);
    }

    protected String getPrimaryName() {
        return globalName != null ? globalName : shortName;
    }

    protected void renderDocComments(StringBuilder builder, PodRenderingContext context) {
        boolean inParagraph = false;
        for (PsiElement comment : docComments) {
            String text = comment == null ? "" : comment.getText().trim();

            // Process empty lines, which may be paragraph breaks.
            if (text.isEmpty()) {
                // If we're in a paragraph, end it.
                if (inParagraph) {
                    builder.append("</p>");
                    inParagraph = false;
                }
            }

            // If it's not empty, emit the content, starting a paragraph if
            // needed.
            else {
                if (!inParagraph) {
                    builder.append("<p>");
                    inParagraph = true;
                }
                builder.append(text);
                builder.append(' ');
            }
        }
        if (inParagraph)
            builder.append("</p>");
    }
}
