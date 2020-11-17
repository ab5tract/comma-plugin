package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
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

    private void checkAssignable(PsiElement operand,
                                 @NotNull PsiElement annotate,
                                 @NotNull AnnotationHolder holder) {
        if (operand instanceof Perl6Variable) {
            // Only scalars.
            String name = ((Perl6Variable)operand).getVariableName();
            if (name == null || !name.startsWith("$"))
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
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot assign to a readonly parameter")
                    .create();
            }
        }
    }
}
