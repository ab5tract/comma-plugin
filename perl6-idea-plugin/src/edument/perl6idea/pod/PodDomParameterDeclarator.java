package edument.perl6idea.pod;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PodDomParameterDeclarator extends PodDomDeclarator {
    private final String definition;

    public PodDomParameterDeclarator(int offset,
                                     @NotNull String shortName,
                                     @Nullable String globalName,
                                     List<PsiElement> docComments,
                                     @NotNull String definition) {
        super(offset, shortName, globalName, docComments, null);
        this.definition = definition;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        builder.append("<code>");
        builder.append(definition);
        builder.append("</code>");
        renderDocComments(builder, context);
    }
}
