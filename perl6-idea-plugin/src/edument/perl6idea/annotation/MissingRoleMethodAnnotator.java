package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.annotation.fix.StubMissingMethodsFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MissingRoleMethodAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;

        Perl6PackageDecl packageDecl = (Perl6PackageDecl)element;
        if (!packageDecl.getPackageKind().equals("class")) return;

        List<Perl6Trait> traits = packageDecl.getTraits();
        if (traits.size() == 0) return;

        Map<String, Pair<Integer, String>> methodsToImplement = new HashMap<>();
        Map<String, Integer> seen = new HashMap<>();
        gatherRoleStubs(traits, methodsToImplement, seen, 0);

        List<Perl6PsiDeclaration> declarations = packageDecl.getDeclarations();
        for (Perl6PsiDeclaration decl : declarations) {
            if (decl instanceof Perl6RoutineDecl) {
                Perl6RoutineDecl routineDecl = (Perl6RoutineDecl)decl;
                if (routineDecl.getRoutineKind().equals("method")) {
                    methodsToImplement.remove(routineDecl.getRoutineName());
                }
            } else if (decl instanceof Perl6VariableDecl) {
                Perl6VariableDecl variableDecl = (Perl6VariableDecl)decl;
                if (!Objects.equals(variableDecl.getScope(), "has"))
                    continue;
                String[] names = variableDecl.getVariableNames();
                for (String name : names) {
                    if (Perl6Variable.getTwigil(name) == '.') {
                        methodsToImplement.remove(name.substring(2));
                    }
                }
                List<Perl6Trait> attrTraits = variableDecl.getTraits();
                for (Perl6Trait trait : attrTraits) {
                    if (trait.getTraitModifier().equals("handles")) {
                        String handledMethods = trait.getTraitName();
                        for (String method : handledMethods.split("\\s+"))
                            methodsToImplement.remove(method);
                    }
                }
            }
        }

        if (methodsToImplement.size() != 0) {
            String names = String.join(", ", methodsToImplement.keySet());
            int start = packageDecl.getTextOffset();
            PsiElement blockoid = PsiTreeUtil.getChildOfType(packageDecl, Perl6Blockoid.class);
            // Block is not yet typed
            if (blockoid == null) return;
            int end = blockoid.getTextOffset();
            holder.newAnnotation(HighlightSeverity.ERROR, String.format("Composed roles require to implement methods: %s", names))
                .range(new TextRange(start, end))
                .withFix(new StubMissingMethodsFix(packageDecl, ContainerUtil.map(methodsToImplement.values(), p -> p.second)))
                .create();
        }
    }

    private static void gatherRoleStubs(List<Perl6Trait> traits,
                                        Map<String, Pair<Integer, String>> methodsToImplement,
                                        Map<String, Integer> seen,
                                        int level) {
        for (Perl6Trait trait : traits) {
            if (trait.getTraitModifier().equals("does")) {
                Perl6TypeName type = trait.getCompositionTypeName();
                if (type == null) continue;
                PsiReference ref = type.getReference();
                if (ref == null) continue;
                PsiElement roleDeclaration = ref.resolve();
                if (!(roleDeclaration instanceof Perl6PackageDecl)) continue;
                List<Perl6PsiDeclaration> declarations = ((Perl6PackageDecl)roleDeclaration).getDeclarations();
                for (Perl6PsiDeclaration maybeMethod : declarations) {
                    if (!(maybeMethod instanceof Perl6RoutineDecl)) continue;
                    Perl6RoutineDecl method = (Perl6RoutineDecl)maybeMethod;
                    if (!method.getRoutineKind().equals("method") || method.getParent() instanceof Perl6MultiDecl) continue;
                    if (method.isStubbed()) {
                        // If method is not indexed or we saw it, and it was not closer to root class than current stub,
                        // add it to candidates for stubbing
                        boolean isIndexed = seen.containsKey(method.getRoutineName());
                        if ((!isIndexed || seen.get(method.getRoutineName()) > level)) {
                            Pair<Integer, String> value = Pair.create(
                                level, method.getText().replace("...", ""));
                            methodsToImplement.put(method.getRoutineName(), value);
                        }
                    }
                    else {
                        // If this method was planned to be stubbed, but now we see an implementation
                        // with level closer to equal relatively to root class, do not stub it
                        Pair<Integer, String> value = methodsToImplement.get(method.getRoutineName());
                        if (value != null && value.first >= level) {
                            methodsToImplement.remove(method.getRoutineName());
                        }
                        seen.put(method.getRoutineName(), level);
                    }
                }
                List<Perl6Trait> innerTraits = ((Perl6PackageDecl)roleDeclaration).getTraits();
                gatherRoleStubs(innerTraits, methodsToImplement, seen, level + 1);
            }
        }
    }
}
