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
        if (element instanceof Perl6ScopedDecl) {
            PsiElement[] nodes = element.getChildren();
            if (!(nodes[0] instanceof Perl6PackageDecl))
                return;
            Perl6PackageDecl perl6PackageDecl = (Perl6PackageDecl)nodes[0];
            /* Let's check for a unit declared thing with blockoid */
            Perl6ScopedDecl scopedDeclarator = (Perl6ScopedDecl)perl6PackageDecl.getParent();

            PsiElement firstChild = scopedDeclarator.getFirstChild();
            if (firstChild.getNode().getElementType() != Perl6TokenTypes.SCOPE_DECLARATOR) return;

            if (firstChild.getText().equals("unit")) {
                PsiElement maybeBlockoid = perl6PackageDecl.getLastChild();

                while (maybeBlockoid instanceof PsiWhiteSpace ||
                       maybeBlockoid != null && maybeBlockoid.getNode().getElementType() == UNV_WHITE_SPACE) {
                    maybeBlockoid = maybeBlockoid.getPrevSibling();
                }

                if (maybeBlockoid instanceof Perl6Blockoid) {
                    int textOffset = firstChild.getTextOffset();
                    int textOffset1 = perl6PackageDecl.getTextOffset();
                    int length = ((Perl6PackageDecl)perl6PackageDecl).getPackageName().length();
                    holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use 'unit' with block form of declaration")
                        .range(new TextRange(textOffset, textOffset1 + length))
                        .withFix(new RemoveUnitDeclaratorQuickFix(textOffset, (Perl6PackageDecl)perl6PackageDecl)).create();
                }
            }
        }
        else if (element instanceof Perl6PackageDecl && element.getLastChild() instanceof Perl6StatementList) {
            /* This looks like a missing unit at the front with a semicolon
             * at the end of the line. Let's complain about the statement
             * separator if we can find it.
             */
            PsiElement maybeStatementTerminator = element.getLastChild().getPrevSibling();
            while (maybeStatementTerminator instanceof PsiWhiteSpace ||
                    maybeStatementTerminator != null && maybeStatementTerminator.getNode().getElementType() == UNV_WHITE_SPACE) {
                maybeStatementTerminator = maybeStatementTerminator.getPrevSibling();
            }

            if (maybeStatementTerminator == null) return;
            ASTNode node = maybeStatementTerminator.getNode();
            if (node.getElementType() == Perl6TokenTypes.STATEMENT_TERMINATOR) {
                PsiElement packageDeclarator = element.getFirstChild();
                String declaratorType = null;
                if (packageDeclarator.getNode().getElementType() == Perl6TokenTypes.PACKAGE_DECLARATOR) {
                    declaratorType = packageDeclarator.getText();
                }
                String errorMessage = declaratorType == null ?
                                      "Semicolon form of package declaration without 'unit' is illegal." :
                                      String.format("Semicolon form of '%s' without 'unit' is illegal.", declaratorType);

                holder.newAnnotation(HighlightSeverity.ERROR, errorMessage).range(element)
                    .withFix(new AddUnitDeclaratorQuickFix((Perl6PackageDecl)element)).create();
            }
        }
    }
}
