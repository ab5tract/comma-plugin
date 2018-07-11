package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import edument.perl6idea.editor.AddUnitDeclaratorQuickFix;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class MissingUnitKeywordAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;
        final Perl6PackageDecl ref = (Perl6PackageDecl)element;

        /* Let's check for a unit declared thing with blockoid */
        if (ref.getParent() != null && ref.getParent() instanceof Perl6ScopedDecl) {
            Perl6ScopedDecl scopedDeclarator = (Perl6ScopedDecl)ref.getParent();

            if (scopedDeclarator.getFirstChild().getNode().getElementType() != Perl6TokenTypes.SCOPE_DECLARATOR) return;

            if (scopedDeclarator.getFirstChild().getText().equals("unit")) {
                    PsiElement maybeBlockoid = ref.getLastChild();

                    while (maybeBlockoid != null && maybeBlockoid instanceof PsiWhiteSpace) {
                        maybeBlockoid = maybeBlockoid.getPrevSibling();
                    }

                    if (maybeBlockoid != null && maybeBlockoid instanceof Perl6Blockoid) {
                        holder.createErrorAnnotation(scopedDeclarator.getFirstChild(), "Cannot use 'unit' with block form of declaration");
                    }
            }
        }
        else if (ref.getLastChild() instanceof Perl6StatementList) {
            /* This looks like a missing unit at the front with a semicolon
             * at the end of the line. Let's complain about the statement
             * separator if we can find it.
             */
            PsiElement maybeStatementTerminator = ref.getLastChild().getPrevSibling();
            while (maybeStatementTerminator != null && maybeStatementTerminator instanceof PsiWhiteSpace) {
                maybeStatementTerminator = maybeStatementTerminator.getPrevSibling();
            }
            if (maybeStatementTerminator.getNode().getElementType() == Perl6TokenTypes.STATEMENT_TERMINATOR) {
                PsiElement packageDeclarator = ref.getFirstChild();
                String declaratorType = null;
                if (packageDeclarator.getNode().getElementType() == Perl6TokenTypes.PACKAGE_DECLARATOR) {
                    declaratorType = packageDeclarator.getText();
                }
                if (declaratorType != null) {
                    holder.createErrorAnnotation(maybeStatementTerminator,
                            String.format(
                                    "Semicolon form of '%s' without 'unit' is illegal.",
                                    declaratorType, declaratorType))
                            .registerFix(new AddUnitDeclaratorQuickFix(), new TextRange(ref.getTextOffset(), maybeStatementTerminator.getTextOffset() + 1));
                }
                else {
                    holder.createErrorAnnotation(maybeStatementTerminator,
                                    "Semicolon form of package declaration without 'unit' is illegal.")
                            .registerFix(new AddUnitDeclaratorQuickFix(), new TextRange(ref.getTextOffset(), maybeStatementTerminator.getTextOffset() + 1));
                }
            }
        }
    }
}
