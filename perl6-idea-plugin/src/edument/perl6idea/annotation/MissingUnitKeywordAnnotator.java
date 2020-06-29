package edument.perl6idea.annotation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import edument.perl6idea.annotation.fix.AddUnitDeclaratorQuickFix;
import edument.perl6idea.annotation.fix.RemoveUnitDeclaratorQuickFix;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class MissingUnitKeywordAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6PackageDecl)) return;
        final Perl6PackageDecl ref = (Perl6PackageDecl)element;

        /* Let's check for a unit declared thing with blockoid */
        if (ref.getParent() != null && ref.getParent() instanceof Perl6ScopedDecl) {
            Perl6ScopedDecl scopedDeclarator = (Perl6ScopedDecl)ref.getParent();

            PsiElement firstChild = scopedDeclarator.getFirstChild();
            if (firstChild.getNode().getElementType() != Perl6TokenTypes.SCOPE_DECLARATOR) return;

            if (firstChild.getText().equals("unit")) {
                PsiElement maybeBlockoid = ref.getLastChild();

                while (maybeBlockoid instanceof PsiWhiteSpace ||
                        maybeBlockoid != null && maybeBlockoid.getNode().getElementType() == UNV_WHITE_SPACE) {
                    maybeBlockoid = maybeBlockoid.getPrevSibling();
                }

                    if (maybeBlockoid instanceof Perl6Blockoid) {
                        int textOffset = firstChild.getTextOffset();
                        int textOffset1 = ref.getTextOffset();
                        int length = ref.getPackageName().length();
                        holder.newAnnotation(HighlightSeverity.ERROR,"Cannot use 'unit' with block form of declaration")
                            .range(new TextRange(textOffset, textOffset1 + length))
                            .withFix(new RemoveUnitDeclaratorQuickFix(textOffset, ref)).create();
                    }
            }
        }
        else if (ref.getLastChild() instanceof Perl6StatementList) {
            /* This looks like a missing unit at the front with a semicolon
             * at the end of the line. Let's complain about the statement
             * separator if we can find it.
             */
            PsiElement maybeStatementTerminator = ref.getLastChild().getPrevSibling();
            while (maybeStatementTerminator instanceof PsiWhiteSpace ||
                    maybeStatementTerminator != null && maybeStatementTerminator.getNode().getElementType() == UNV_WHITE_SPACE) {
                maybeStatementTerminator = maybeStatementTerminator.getPrevSibling();
            }

            if (maybeStatementTerminator == null) return;
            ASTNode node = maybeStatementTerminator.getNode();
            if (node.getElementType() == Perl6TokenTypes.STATEMENT_TERMINATOR) {
                PsiElement packageDeclarator = ref.getFirstChild();
                String declaratorType = null;
                if (packageDeclarator.getNode().getElementType() == Perl6TokenTypes.PACKAGE_DECLARATOR) {
                    declaratorType = packageDeclarator.getText();
                }
                String errorMessage = declaratorType == null ?
                                      "Semicolon form of package declaration without 'unit' is illegal." :
                                      String.format("Semicolon form of '%s' without 'unit' is illegal.", declaratorType);

                holder.newAnnotation(HighlightSeverity.ERROR, errorMessage).range(ref)
                    .withFix(new AddUnitDeclaratorQuickFix(ref)).create();
            }
        }
    }
}
