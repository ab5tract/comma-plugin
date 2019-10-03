package edument.perl6idea.docs;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.external.Perl6ExternalPsiElement;
import edument.perl6idea.psi.impl.PodPostCommentImpl;
import edument.perl6idea.psi.impl.PodPreCommentImpl;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static edument.perl6idea.parsing.Perl6TokenTypes.COMMENT;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6DocumentationProvider implements DocumentationProvider {
    private final Map<String, String> coreTypeCache = new ConcurrentHashMap<>();
    private boolean coreCache = false;

    @Nullable
    @Override
    public synchronized String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof Perl6ExternalPsiElement) {
            String extDocs = ((Perl6ExternalPsiElement)element).getDocumentationString();
            if (extDocs != null)
                return extDocs;
            return tryToResolveCORE((Perl6ExternalPsiElement)element);
        }
        if (element instanceof Perl6PackageDecl ||
            element instanceof Perl6RoutineDecl ||
            element instanceof Perl6VariableDecl) {
            return getEnclosingPodDocs(element);
        }
        return null;
    }

    @Nullable
    private synchronized String tryToResolveCORE(Perl6ExternalPsiElement element) {
        if (!coreCache) {
            coreCache = true;
            new Thread(() -> populateCoreCache()).start();
            return null;
        } else {
            return coreTypeCache.get(element.getName());
        }
    }

    private void populateCoreCache() {
        List<String> docLines = Perl6Utils.getResourceAsLines("docs/core.json");
        JSONObject docsCORE = new JSONObject(String.join("\n", ArrayUtil.toStringArray(docLines)));
        for (Object type : docsCORE.getJSONArray("types")) {
            JSONObject typeDescription = (JSONObject)type;
            coreTypeCache.put(typeDescription.getString("name"), typeDescription.getString("desc"));
        }
    }

    private static String getEnclosingPodDocs(PsiElement element) {
        StringBuilder builder = new StringBuilder();
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        if (statement == null) return null;
        PsiElement temp = statement.getPrevSibling();
        gatherInlineComments(temp, p -> p.getPrevSibling(), t -> builder.insert(0, t), PodPreCommentImpl.class);
        builder.append("\n");
        temp = statement.getNextSibling();
        gatherInlineComments(temp, p -> p.getNextSibling(), t -> builder.append(t), PodPostCommentImpl.class);
        return builder.toString().trim();
    }

    private static void gatherInlineComments(PsiElement temp,
                                             Function<PsiElement, PsiElement> next,
                                             Consumer<String> insert,
                                             Class classToCompare) {
        while (true) {
            if (temp == null) break;
            if (temp instanceof PsiWhiteSpace ||
                temp.getNode().getElementType() == UNV_WHITE_SPACE) {
                temp = next.fun(temp);
                continue;
            }
            if (temp.getClass().isAssignableFrom(classToCompare)) {
                PsiElement commentContent = temp instanceof PodPreComment ? temp.getLastChild() : temp.getFirstChild();
                for (PsiElement comment = commentContent; comment != null; comment = next.fun(comment)) {
                    if (comment.getNode().getElementType() == COMMENT) {
                        String text = comment.getText();
                        if (text.startsWith("\n"))
                            text = "\n" + text.trim();
                        else
                            text = text.trim();
                        insert.accept(text);
                    }
                }
                insert.accept("\n");
                temp = next.fun(temp);
                continue;
            }
            break;
        }
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof Perl6ExternalPsiElement) {
            String name = ((Perl6ExternalPsiElement)element).getName();
            if (coreTypeCache.containsKey(name)) {
                return Collections.singletonList("https://docs.perl6.org/type/" + name);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof Perl6ExternalPsiElement) {
            if (!coreCache) {
                coreCache = true;
                populateCoreCache();
            }

            String name = ((Perl6ExternalPsiElement)element).getName();
            if (coreTypeCache.containsKey(name)) {
                return coreTypeCache.get(name).replaceAll("\n", "<br>");
            }
        }
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
