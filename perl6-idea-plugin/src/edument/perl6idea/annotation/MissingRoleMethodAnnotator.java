package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.StubMissingMethodsFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MissingRoleMethodAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;

        Perl6PackageDecl decl = (Perl6PackageDecl)element;
        if (!decl.getPackageKind().equals("class")) return;

        List<Perl6Trait> traits = decl.getTraits();
        if (traits.size() == 0) return;

        Map<String, Pair<Integer, String>> methodsToImplement = new HashMap<>();
        Map<String, Integer> seen = new HashMap<>();
        gatherRoleStubs(traits, methodsToImplement, seen, 0);

        List<Perl6PsiDeclaration> declarations = decl.getDeclarations();
        for (Perl6PsiDeclaration declaration : declarations) {
            if (!(declaration instanceof Perl6RoutineDecl)) continue;
            Perl6RoutineDecl routineDecl = (Perl6RoutineDecl)declaration;
            if (routineDecl.getRoutineKind().equals("method")) {
                methodsToImplement.remove(routineDecl.getRoutineName());
            }
        }

        if (methodsToImplement.size() != 0) {
            String names = String.join(", ", methodsToImplement.keySet());
            int start = decl.getTextOffset();
            PsiElement blockoid = PsiTreeUtil.getChildOfType(decl, Perl6Blockoid.class);
            // Block is not yet typed
            if (blockoid == null) return;
            int end = blockoid.getTextOffset();
            holder.createErrorAnnotation(new TextRange(start, end), String.format("Composed roles require to implement methods: %s", names))
                  .registerFix(new StubMissingMethodsFix(decl,
                                                         methodsToImplement
                                                             .values()
                                                             .stream()
                                                             .map(p -> p.second)
                                                             .collect(Collectors.toList())));
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
