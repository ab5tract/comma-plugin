package edument.perl6idea.docs;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.external.ExternalPerl6File;
import edument.perl6idea.psi.external.Perl6ExternalPsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Perl6DocumentationProvider implements DocumentationProvider {
    @Nullable
    @Override
    public synchronized String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof Perl6Constant) {
            if (element.getText().length() < 50)
                return element.getText();
            return "constant " + ((Perl6Constant)element).getConstantName() + " = ...";
        } else if (element instanceof Perl6Enum) {
            Perl6Enum enumEl = (Perl6Enum)element;
            return "enum " + enumEl.getEnumName() + " (" + String.join(", ", enumEl.getEnumValues()) + ")";
        } else if (element instanceof Perl6PackageDecl) {
            Perl6PackageDecl decl = (Perl6PackageDecl)element;
            Optional<String> traits = decl.getTraits().stream().map(t -> t.getTraitModifier() + " " + t.getTraitName()).reduce((s1, s2) -> s1 + " " + s2);
            return String.format("%s %s%s", decl.getPackageKind(), decl.getPackageName(),
                                 traits.map(s -> " " + s).orElse(""));
        } else if (element instanceof Perl6ParameterVariable) {
            Perl6ParameterVariable decl = (Perl6ParameterVariable)element;
            Perl6Parameter parameter = PsiTreeUtil.getParentOfType(decl, Perl6Parameter.class);
            if (parameter == null) return null;
            return parameter.getText();
        } else if (element instanceof Perl6RegexDecl) {
            String text = element.getText();
            if (text.length() < 50)
                return text;
            Perl6RegexDecl decl = (Perl6RegexDecl)element;
            return String.format("%s %s { ... }", decl.getRegexKind(), decl.getRegexName());
        } else if (element instanceof Perl6RoutineDecl) {
            Perl6RoutineDecl decl = (Perl6RoutineDecl)element;
            String signature;
            if (decl instanceof Perl6ExternalPsiElement)
                signature = "(" + decl.summarySignature() + ")";
            else {
                Perl6Signature node = decl.getSignatureNode();
                signature = node != null ? node.getText() : "()";
            }
            return String.format("%s %s%s", decl.getRoutineKind(), decl.getRoutineName(), signature);
        } else if (element instanceof Perl6Subset) {
            return "subset " + ((Perl6Subset)element).getSubsetName() + " of " + ((Perl6Subset)element).getSubsetBaseTypeName();
        } else if (element instanceof Perl6VariableDecl) {
            Perl6VariableDecl decl = (Perl6VariableDecl)element;
            String name = String.join(", ", decl.getVariableNames());
            String scope = decl.getScope();
            PsiElement initializer = decl.getInitializer();
            Perl6PackageDecl selfType = scope.equals("has") ? PsiTreeUtil.getParentOfType(decl, Perl6PackageDecl.class) : null;
            return String.format("%s %s%s%s",
                                 scope, name, initializer == null ? "" : " = " + initializer.getText(),
                                 selfType == null ? "" : " (attribute of " + selfType.getPackageName() + ")");
        }
        return null;
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof Perl6ExternalPsiElement) {
            String name = ((Perl6ExternalPsiElement)element).getName();
            PsiElement parent = element.getParent();
            if (parent instanceof ExternalPerl6File &&
                Objects.equals(((ExternalPerl6File)parent).getName(), "SETTINGS.pm6")) {
                if (element instanceof Perl6PackageDecl)
                    return Collections.singletonList("https://docs.perl6.org/type/" + name);
                else if (element instanceof Perl6RoutineDecl)
                    return Collections.singletonList("https://docs.perl6.org/routine/" + name);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public synchronized String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof Perl6MethodCall || element instanceof Perl6SubCall) {
            PsiReference ref;
            if (element instanceof Perl6MethodCall) {
                ref = element.getReference();
            } else {
                Perl6SubCallName callName = PsiTreeUtil.findChildOfType(element, Perl6SubCallName.class);
                if (callName == null) return null;
                ref = callName.getReference();
            }
            if (!(ref instanceof PsiReferenceBase.Poly))
                return null;
            ResolveResult[] resolves = ((PsiReferenceBase.Poly)ref).multiResolve(false);
            for (ResolveResult result : resolves) {
                PsiElement declaration = result.getElement();
                if (declaration instanceof Perl6RoutineDecl) {
                    String docs = ((Perl6RoutineDecl)declaration).getDocsString();
                    if (docs != null)
                        return docs;
                }
            }
        }

        if (element instanceof Perl6Documented) {
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
