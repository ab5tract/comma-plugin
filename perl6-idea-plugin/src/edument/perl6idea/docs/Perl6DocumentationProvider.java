package edument.perl6idea.docs;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.impl.PodPostCommentImpl;
import edument.perl6idea.psi.impl.PodPreCommentImpl;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6DocumentationProvider implements DocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof Perl6PackageDecl ||
            element instanceof Perl6RoutineDecl ||
            element instanceof Perl6VariableDecl) {
            return getEnclosingPodDocs(element);
        }
        return null;
    }

    private static String getEnclosingPodDocs(PsiElement element) {
        StringBuilder docs = new StringBuilder();
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        if (statement == null) return null;
        PsiElement temp = statement.getPrevSibling();
        gatherInlineComments(temp, p -> p.getPrevSibling(), t -> docs.insert(0, t), PodPreCommentImpl.class);
        temp = statement.getNextSibling();
        gatherInlineComments(temp, p -> p.getNextSibling(), t -> docs.append(t), PodPostCommentImpl.class);
        return docs.toString();
    }

    private static void gatherInlineComments(PsiElement temp,
                                             Function<PsiElement, PsiElement> next,
                                             Function<String, StringBuilder> insert,
                                             Class classToCompare) {
        while (true) {
            if (temp == null) break;
            if (temp instanceof PsiWhiteSpace ||
                temp.getNode().getElementType() == UNV_WHITE_SPACE) {
                temp = next.fun(temp);
                continue;
            }
            if (temp.getClass().isAssignableFrom(classToCompare)) {
                insert.fun(temp.getText().substring(3) + "\n");
                temp = next.fun(temp);
                continue;
            }
            break;
        }
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }
}