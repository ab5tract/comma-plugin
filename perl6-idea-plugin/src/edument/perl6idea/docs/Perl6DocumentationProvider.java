package edument.perl6idea.docs;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.external.ExternalPerl6PackageDecl;
import edument.perl6idea.psi.external.Perl6ExternalPsiElement;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Perl6DocumentationProvider implements DocumentationProvider {
    private final Map<String, String> coreTypeCache = new ConcurrentHashMap<>();
    private boolean coreCache = false;

    @Nullable
    @Override
    public synchronized String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof Perl6Constant) {
            return "Constant " + ((Perl6Constant)element).getConstantName();
        } else if (element instanceof Perl6Enum) {
            return "Enum " + ((Perl6Enum)element).getEnumName();
        } else if (element instanceof Perl6PackageDecl) {
            return ((Perl6PackageDecl)element).getPackageKind() + " " + ((Perl6PackageDecl)element).getPackageName();
        } else if (element instanceof Perl6Parameter) {
            return "Parameter: " + ((Perl6Parameter)element).summary();
        } else if (element instanceof Perl6Regex) {
            return "Regex";
        } else if (element instanceof Perl6RoutineDecl) {
            return ((Perl6RoutineDecl)element).getRoutineKind() + " " + ((Perl6RoutineDecl)element).getRoutineName();
        } else if (element instanceof Perl6Subset) {
            return "Subset " + ((Perl6Subset)element).getSubsetName() + " of " + ((Perl6Subset)element).getSubsetBaseTypeName();
        } else if (element instanceof Perl6VariableDecl) {
            return "Variable declaration: " + Arrays.toString(((Perl6VariableDecl)element).getVariableNames());
        }
        return null;
    }

    private void populateCoreCache() {
        List<String> docLines = Perl6Utils.getResourceAsLines("docs/core.json");
        JSONObject docsCORE = new JSONObject(String.join("\n", ArrayUtil.toStringArray(docLines)));
        for (Object type : docsCORE.getJSONArray("types")) {
            JSONObject typeDescription = (JSONObject)type;
            coreTypeCache.put(typeDescription.getString("name"), typeDescription.getString("desc"));
        }
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof ExternalPerl6PackageDecl) {
            String name = ((Perl6PackageDecl)element).getName();
            if (coreTypeCache.containsKey(name)) {
                return Collections.singletonList("https://docs.perl6.org/type/" + name);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public synchronized String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof Perl6ExternalPsiElement) {
            String docs = ((Perl6ExternalPsiElement)element).getDocsString();
            if (docs == null) {
                if (!coreCache) {
                    coreCache = true;
                    populateCoreCache();
                }
                String name = ((Perl6ExternalPsiElement)element).getName();
                docs = coreTypeCache.get(name);
                if (docs != null)
                    docs = docs.replaceAll("\n", "<br>");
            }
            return docs;
        } else if (element instanceof Perl6Documented) {
            return ((Perl6Documented)element).getDocsString();
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
