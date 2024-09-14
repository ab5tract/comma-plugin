package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AssignmentToImmutableAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof RakuInfixApplication && ((RakuInfixApplication)element).isAssignish()) {
            if (((RakuInfixApplication)element).getOperands().length > 0)
                checkAssignable(((RakuInfixApplication)element).getOperands()[0], element, holder);
        }
        else if (element instanceof RakuPrefixApplication && ((RakuPrefixApplication)element).isAssignish()) {
            checkAssignable(((RakuPrefixApplication)element).getOperand(), element, holder);
        }
        else if (element instanceof RakuPostfixApplication && ((RakuPostfixApplication)element).isAssignish()) {
            checkAssignable(((RakuPostfixApplication)element).getOperand(), element, holder);
        }
    }

    private static void checkAssignable(PsiElement operand,
                                        @NotNull PsiElement annotate,
                                        @NotNull AnnotationHolder holder) {
        if (operand instanceof RakuVariable) {
            // Only scalars.
            String name = ((RakuVariable)operand).getVariableName();
            if (name == null || (!name.startsWith("$") && !name.startsWith("&")))
                return;

            // See if it resolves to a parameter.
            PsiReference reference = operand.getReference();
            if (reference == null)
                return;
            PsiElement declaration = reference.resolve();
            if (declaration instanceof RakuParameterVariable) {
                // Parameter, but is it a signature on a block, not just `my ($x, $y)`?
                PsiElement parameter = declaration.getParent();
                if (!(parameter instanceof RakuParameter))
                    return;
                RakuSignature signature = PsiTreeUtil.getParentOfType(parameter, RakuSignature.class);
                if (signature == null || signature.getParent() instanceof RakuVariableDecl)
                    return;

                // Ensure it's readonly.
                if (((RakuParameter)parameter).isCopy() || ((RakuParameter)parameter).isRW())
                    return;
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a readonly parameter").create();
            } else if (declaration instanceof RakuConstant) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a constant").create();
            } else if (declaration instanceof RakuRoutineDecl) {
                if (Objects.equals(name, "$_") || Objects.equals(name, "$!") || Objects.equals(name, "$/"))
                    return;
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a routine").create();
            }
        } else if (operand instanceof RakuIntLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to an Int literal").create();
        } else if (operand instanceof RakuStrLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Str literal").create();
        } else if (operand instanceof RakuComplexLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Complex literal").create();
        } else if (operand instanceof RakuNumLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Num literal").create();
        } else if (operand instanceof RakuRatLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Rat literal").create();
        } else if (operand instanceof RakuRegexLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Regex literal").create();
        } else if (operand instanceof RakuRoutineDecl) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a routine declaration").create();
        } else if (operand instanceof RakuParenthesizedExpr) {
            @NotNull RakuType type = ((RakuParenthesizedExpr)operand).inferType();
            if (type.getName().equals("Pair"))
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Pair literal").create();
        } else if (operand instanceof RakuColonPair) {
            if (PsiTreeUtil.getChildOfType(operand, RakuSignature.class) != null)
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a signature literal").create();
            else
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Pair literal").create();
        } else if (operand instanceof RakuCapture) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a capture literal").create();
        } else if (operand instanceof RakuTypeName) {
            PsiReference reference = operand.getReference();
            if (reference == null)
                return;
            PsiElement declaration = reference.resolve();
            if (declaration instanceof RakuConstant) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a constant").create();
            }
        }
    }
}
