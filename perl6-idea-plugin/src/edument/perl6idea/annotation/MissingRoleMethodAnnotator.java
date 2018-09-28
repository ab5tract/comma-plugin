package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.StubMissingMethodsFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingRoleMethodAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;

        Perl6PackageDecl decl = (Perl6PackageDecl)element;
        if (!decl.getPackageKind().equals("class")) return;

        List<Perl6Trait> traits = decl.getTraits();
        if (traits.size() == 0) return;

        boolean classDoesRole = false;
        Map<String, String> methodsToImplement = new HashMap<>();
        for (Perl6Trait trait : traits) {
            if (trait.getTraitModifier().equals("does")) {
                classDoesRole = true;
                Perl6TypeName type = trait.getCompositionTypeName();
                if (type == null) continue;
                PsiReference ref = type.getReference();
                if (ref == null) continue;
                PsiElement roleDeclaration = ref.resolve();
                if (!(roleDeclaration instanceof Perl6PackageDecl)) continue;
                List<Perl6PsiDeclaration> declarations = ((Perl6PackageDecl)roleDeclaration).getDeclarations();
                for (Perl6PsiDeclaration maybeMethod : declarations) {
                    if (maybeMethod instanceof Perl6RoutineDecl && checkMethod((Perl6RoutineDecl)maybeMethod)) {
                        Perl6RoutineDecl method = (Perl6RoutineDecl)maybeMethod;
                        methodsToImplement.put(method.getRoutineName(),
                                               method.getText().replace("...", ""));
                    }
                }
            }
        }
        if (!classDoesRole) return;

        List<Perl6PsiDeclaration> declarations = decl.getDeclarations();
        for (Perl6PsiDeclaration declaration : declarations) {
            if (!(declaration instanceof Perl6RoutineDecl)) continue;
            Perl6RoutineDecl routineDecl = (Perl6RoutineDecl)declaration;
            if (routineDecl.getRoutineKind().equals("method") && !(routineDecl.getParent() instanceof Perl6MultiDecl)) {
                methodsToImplement.remove(routineDecl.getRoutineName());
            }
        }

        if (methodsToImplement.size() != 0) {
            String names = String.join(", ", methodsToImplement.keySet());
            int start = element.getTextOffset();
            int end = PsiTreeUtil.getChildOfType(decl, Perl6Blockoid.class).getTextOffset();
            holder.createErrorAnnotation(new TextRange(start, end), String.format("Composed roles require to implement methods: %s", names))
                  .registerFix(new StubMissingMethodsFix(decl, methodsToImplement.values()));
        }
    }

    private static boolean checkMethod(Perl6RoutineDecl method) {
        return method.getRoutineKind().equals("method") && !(method.getParent() instanceof Perl6MultiDecl) && method.isStubbed();
    }
}
