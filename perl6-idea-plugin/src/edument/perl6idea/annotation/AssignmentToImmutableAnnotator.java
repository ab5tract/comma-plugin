package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.type.Perl6Type;
import org.jetbrains.annotations.NotNull;

public class AssignmentToImmutableAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof Perl6InfixApplication && ((Perl6InfixApplication)element).isAssignish()) {
            checkAssignable(((Perl6InfixApplication)element).getOperands()[0], element, holder);
        }
        else if (element instanceof Perl6PrefixApplication && ((Perl6PrefixApplication)element).isAssignish()) {
            checkAssignable(((Perl6PrefixApplication)element).getOperand(), element, holder);
        }
        else if (element instanceof Perl6PostfixApplication && ((Perl6PostfixApplication)element).isAssignish()) {
            checkAssignable(((Perl6PostfixApplication)element).getOperand(), element, holder);
        }
    }

    private static void checkAssignable(PsiElement operand,
                                        @NotNull PsiElement annotate,
                                        @NotNull AnnotationHolder holder) {
        if (operand instanceof Perl6Variable) {
            // Only scalars.
            String name = ((Perl6Variable)operand).getVariableName();
            if (name == null || (!name.startsWith("$") && !name.startsWith("&")))
                return;

            // See if it resolves to a parameter.
            PsiReference reference = operand.getReference();
            if (reference == null)
                return;
            PsiElement declaration = reference.resolve();
            if (declaration instanceof Perl6ParameterVariable) {
                // Parameter, but is it a signature on a block, not just `my ($x, $y)`?
                PsiElement parameter = declaration.getParent();
                if (!(parameter instanceof Perl6Parameter))
                    return;
                Perl6Signature signature = PsiTreeUtil.getParentOfType(parameter, Perl6Signature.class);
                if (signature == null || signature.getParent() instanceof Perl6VariableDecl)
                    return;

                // Ensure it's readonly.
                if (((Perl6Parameter)parameter).isCopy() || ((Perl6Parameter)parameter).isRW())
                    return;
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a readonly parameter").create();
            } else if (declaration instanceof Perl6Constant) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a constant").create();
            } else if (declaration instanceof Perl6RoutineDecl) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a routine").create();
            }
        } else if (operand instanceof Perl6IntLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to an Int literal").create();
        } else if (operand instanceof Perl6StrLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Str literal").create();
        } else if (operand instanceof Perl6ComplexLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Complex literal").create();
        } else if (operand instanceof Perl6NumLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Num literal").create();
        } else if (operand instanceof Perl6RatLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Rat literal").create();
        } else if (operand instanceof Perl6RegexLiteral) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Regex literal").create();
        } else if (operand instanceof Perl6RoutineDecl) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a routine declaration").create();
        } else if (operand instanceof Perl6ParenthesizedExpr) {
            @NotNull Perl6Type type = ((Perl6ParenthesizedExpr)operand).inferType();
            if (type.getName().equals("Pair"))
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Pair literal").create();
        } else if (operand instanceof Perl6ColonPair) {
            if (PsiTreeUtil.getChildOfType(operand, Perl6Signature.class) != null)
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a signature literal").create();
            else
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a Pair literal").create();
        } else if (operand instanceof Perl6Capture) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a capture literal").create();
        } else if (operand instanceof Perl6TypeName) {
            PsiReference reference = operand.getReference();
            if (reference == null)
                return;
            PsiElement declaration = reference.resolve();
            if (declaration instanceof Perl6Constant) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a constant").create();
            }
        }
    }
}
