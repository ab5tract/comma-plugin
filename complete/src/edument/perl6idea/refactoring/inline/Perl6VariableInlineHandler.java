package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Perl6VariableInlineHandler implements InlineHandler {
    public static final String INLINE_VARIABLE = "Inline Variable";

    @Nullable
    @Override
    public Settings prepareInlineElement(@NotNull PsiElement element, @Nullable Editor editor, boolean invokedOnReference) {
        if (element instanceof Perl6VariableDecl || element instanceof Perl6ParameterVariable) {
            return Perl6InlineVariableUtil.inlineVariableSettings(element, editor, invokedOnReference);
        }
        return null;
    }

    @Override
    public void removeDefinition(@NotNull PsiElement element, @NotNull Settings settings) {
        if (element instanceof Perl6VariableDecl) {
            Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
            PsiElement ws = statement.getNextSibling();
            statement.delete();
            if (ws instanceof PsiWhiteSpace)
                ws.delete();
        }
        if (element instanceof Perl6ParameterVariable) {
            Perl6VariableDecl decl = PsiTreeUtil.getParentOfType(element, Perl6VariableDecl.class);
            if (decl != null)
                decl.removeVariable(((Perl6InlineVariableSettings)settings).getVariable());
            else {
                Perl6Parameter parameter = PsiTreeUtil.getParentOfType(element, Perl6Parameter.class);
                assert parameter != null;
                Perl6Signature signature = PsiTreeUtil.getParentOfType(parameter, Perl6Signature.class);
                assert signature != null;
                List<Perl6Parameter> params = Arrays
                        .stream(signature.getParameters())
                        .filter(p -> !Objects.equals(p, parameter))
                        .collect(Collectors.toList());
                Perl6Signature updatedSignature = Perl6ElementFactory.createRoutineSignature(
                        parameter.getProject(), params);
                signature.replace(updatedSignature);
            }
        }
        if (element instanceof Perl6RoutineDecl) {
            Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
            if (statement != null) statement.delete();
        }
    }

    @Nullable
    @Override
    public Inliner createInliner(@NotNull PsiElement element, @NotNull Settings settings) {
        return element instanceof Perl6VariableDecl || element instanceof Perl6ParameterVariable ?
                new Perl6VariableInliner() : null;
    }
}
