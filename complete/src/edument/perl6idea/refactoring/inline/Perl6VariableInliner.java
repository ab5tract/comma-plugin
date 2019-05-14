package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.containers.MultiMap;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class Perl6VariableInliner implements InlineHandler.Inliner {
    @Nullable
    @Override
    public MultiMap<PsiElement, String> getConflicts(@NotNull PsiReference reference, @NotNull PsiElement referenced) {
        MultiMap<PsiElement, String> conflicts = new MultiMap<>();

        PsiElement variable = reference.getElement();
        PsiElement refParent = variable.getParent();

        if (refParent instanceof Perl6InfixApplication) {
            Perl6InfixApplication application = (Perl6InfixApplication)refParent;
            PsiElement[] operands = application.getOperands();
            if (operands.length >= 2 && application.getOperator().equals("=")) {
                for (PsiElement operand : operands) {
                    if (Objects.equals(operand, variable)) {
                        conflicts.putValue(reference.getElement(), "Variable to be inlined has occurrences as lvalue");
                        break;
                    }
                }
            }
        }

        return conflicts.isEmpty() ? null : conflicts;
    }

    @Override
    public void inlineUsage(@NotNull UsageInfo usage, @NotNull PsiElement declaration) {
        PsiElement usageElement = usage.getElement();
        if (PsiTreeUtil.isAncestor(declaration, usageElement, true))
            return;

        Perl6VariableDecl decl = PsiTreeUtil.getNonStrictParentOfType(declaration, Perl6VariableDecl.class);
        // If we work with a multi-declaration, it can be either a `my (...)` form or
        // a routine parameter
        if (declaration instanceof Perl6ParameterVariable) {
            if (decl != null)
                // Presence of declaration means we are dealing with a multi-declaration,
                // so ask for particular initializer
                usageElement.replace(decl.getInitializer((Perl6Variable) usageElement).copy());
            else {
                // Ask for a parameter initializer
                Perl6Parameter parameter = PsiTreeUtil.getNonStrictParentOfType(declaration, Perl6Parameter.class);
                if (parameter != null)
                    usageElement.replace(parameter.getInitializer().copy());
            }
        } else if (decl != null) {
            // If just a simple variable declaration, inline its initializer
            usageElement.replace(decl.getInitializer().copy());
        }
    }
}
