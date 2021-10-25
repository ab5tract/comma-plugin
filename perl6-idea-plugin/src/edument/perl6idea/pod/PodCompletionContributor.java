package edument.perl6idea.pod;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.POD_FINISH_TEXT;
import static edument.perl6idea.parsing.Perl6TokenTypes.POD_TYPENAME;

public class PodCompletionContributor extends CompletionContributor {
    public static final String[] POD_KEYWORDS = new String[]{
        "finish", "begin", "end", "for", "pod", "item", "table",
        "head1", "head2", "head3", "head4", "code", "para", "defn"
    };

    public PodCompletionContributor() {
        super();
        extend(CompletionType.BASIC, new ElementPattern<>() {
            @Override
            public boolean accepts(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean accepts(@Nullable Object o, ProcessingContext context) {
                if (o instanceof PsiElement) {
                    IElementType type = ((PsiElement)o).getNode().getElementType();
                    return type == POD_FINISH_TEXT || type == POD_TYPENAME;
                }
                return false;
            }

            @Override
            public ElementPatternCondition<PsiElement> getCondition() {
                return null;
            }
        }, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                for (String keyword : POD_KEYWORDS) {
                    result.addElement(LookupElementBuilder.create(keyword));
                }
            }
        });
    }
}
