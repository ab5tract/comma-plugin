package org.raku.annotation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.raku.annotation.fix.AddUnitDeclaratorQuickFix;
import org.raku.annotation.fix.RemoveUnitDeclaratorQuickFix;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public class MissingUnitKeywordAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof RakuScopedDecl) {
            PsiElement[] nodes = element.getChildren();
            if (nodes.length == 0 || !(nodes[0] instanceof RakuPackageDecl perl6PackageDecl))
                return;
            /* Let's check for a unit declared thing with blockoid */
            RakuScopedDecl scopedDeclarator = (RakuScopedDecl)perl6PackageDecl.getParent();

            PsiElement firstChild = scopedDeclarator.getFirstChild();
            if (firstChild.getNode().getElementType() != RakuTokenTypes.SCOPE_DECLARATOR) return;

            if (firstChild.getText().equals("unit")) {
                PsiElement maybeBlockoid = perl6PackageDecl.getLastChild();

                while (maybeBlockoid instanceof PsiWhiteSpace ||
                       maybeBlockoid != null && maybeBlockoid.getNode().getElementType() == UNV_WHITE_SPACE) {
                    maybeBlockoid = maybeBlockoid.getPrevSibling();
                }

                if (maybeBlockoid instanceof RakuBlockoid) {
                    int textOffset = firstChild.getTextOffset();
                    int textOffset1 = perl6PackageDecl.getTextOffset();
                    int length = perl6PackageDecl.getPackageName().length();
                    holder.newAnnotation(HighlightSeverity.ERROR, "Cannot use 'unit' with block form of declaration")
                        .range(new TextRange(textOffset, textOffset1 + length))
                        .withFix(new RemoveUnitDeclaratorQuickFix(textOffset, perl6PackageDecl)).create();
                }
            }
        }
        else if (element instanceof RakuPackageDecl && element.getLastChild() instanceof RakuStatementList) {
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
            if (element.getParent() instanceof RakuScopedDecl && ((RakuScopedDecl)element.getParent()).getScope().equals("unit"))
                return;
            ASTNode node = maybeStatementTerminator.getNode();
            if (node.getElementType() == org.raku.parsing.RakuTokenTypes.STATEMENT_TERMINATOR) {
                PsiElement packageDeclarator = element.getFirstChild();
                String declaratorType = null;
                if (packageDeclarator.getNode().getElementType() == org.raku.parsing.RakuTokenTypes.PACKAGE_DECLARATOR) {
                    declaratorType = packageDeclarator.getText();
                }
                String errorMessage = declaratorType == null ?
                                      "Semicolon form of package declaration without 'unit' is illegal." :
                                      String.format("Semicolon form of '%s' without 'unit' is illegal.", declaratorType);

                holder.newAnnotation(HighlightSeverity.ERROR, errorMessage).range(element)
                    .withFix(new AddUnitDeclaratorQuickFix((RakuPackageDecl)element)).create();
            }
        }
    }
}
