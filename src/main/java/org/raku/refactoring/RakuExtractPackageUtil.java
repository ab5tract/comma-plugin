package org.raku.refactoring;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RakuExtractPackageUtil {
    public static boolean extractPackage(@NotNull Project project,
                                         RakuPackageDecl aPackage,
                                         String name,
                                         boolean isRole,
                                         Collection<RakuAttributeInfo> attributes) {
        RakuPackageDecl newPackageDecl = CompleteRakuElementFactory.createPackage(project, isRole ? "role" : "class", name);

        RakuStatementList newPackageList = PsiTreeUtil.findChildOfType(newPackageDecl, RakuStatementList.class);
        RakuStatementList list = PsiTreeUtil.findChildOfType(aPackage, RakuStatementList.class);

        if (list == null || newPackageList == null) {
            return false;
        }

        List<PsiElement> statementsToDelete = new ArrayList<>();

        for (PsiElement statement : list.getChildren()) {
            if (statement instanceof RakuStatement) {
                PsiElement maybeDecl = statement.getFirstChild();
                if (maybeDecl instanceof RakuRoutineDecl) {
                    for (RakuAttributeInfo attribute : attributes) {
                        if (PsiEquivalenceUtil.areElementsEquivalent(maybeDecl, attribute.getMember())) {
                            newPackageList.add(statement.copy());
                            newPackageList.add(RakuElementFactory.createNewLine(project));
                            statementsToDelete.add(statement);
                        }
                    }
                }
                else if (maybeDecl instanceof RakuScopedDecl && Objects.equals(((RakuScopedDecl)maybeDecl).getScope(), "has")) {
                    PsiElement actualVarDeclaration = maybeDecl.getLastChild();
                    for (RakuAttributeInfo attribute : attributes) {
                        if (PsiEquivalenceUtil.areElementsEquivalent(actualVarDeclaration, attribute.getMember())) {
                            newPackageList.add(statement.copy());
                            newPackageList.add(RakuElementFactory.createNewLine(project));
                            statementsToDelete.add(statement);
                        }
                    }
                }
            }
        }

        // delete old statements
        statementsToDelete.forEach(PsiElement::delete);

        // prepare newly created package node for insertion
        PsiElement formattedElement = CodeStyleManager.getInstance(project).reformat(newPackageDecl);
        PsiElement packageStatement = aPackage.getParent();
        PsiElement newPackageStatement = packageStatement.copy();
        newPackageStatement.deleteChildRange(newPackageStatement.getFirstChild(), newPackageStatement.getFirstChild());
        newPackageStatement.add(formattedElement);

        // insert the node
        PsiElement line = RakuElementFactory.createNewLine(project);
        line = packageStatement.getParent().addBefore(line, packageStatement);
        packageStatement.getParent().addBefore(newPackageStatement, line);

        // update original package
        PsiElement newTrait = isRole
                              ? RakuElementFactory.createTrait(project, "does", name)
                              : RakuElementFactory.createTrait(project, "is", name);

        // insert trait to establish composition/inheritance
        RakuBlockoid blockoid = PsiTreeUtil.findChildOfType(aPackage, RakuBlockoid.class);
        assert blockoid != null;
        newTrait = blockoid.getParent().addBefore(newTrait, blockoid);
        PsiElement ws = PsiParserFacade.getInstance(project).createWhiteSpaceFromText(" ");
        blockoid.getParent().addBefore(ws, newTrait);
        Document document = PsiDocumentManager.getInstance(project).getDocument(aPackage.getContainingFile());
        if (document != null) {
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
            CodeStyleManager.getInstance(project).reformat(aPackage);
        }

        return true;
    }

    public static List<RakuAttributeInfo> getAllMemberInfo(RakuPackageDecl aPackage, boolean isRole) {
        List<RakuPsiDeclaration> decls = aPackage.getDeclarations();
        return decls
            .stream()
            .filter(decl -> {
                if (decl instanceof RakuRoutineDecl) {
                    return ((RakuRoutineDecl)decl).isMethod();
                }
                if (decl instanceof RakuVariableDecl) {
                    return Objects.equals(decl.getScope(), "has");
                }
                return false;
            })
            .map(decl -> {
                return new RakuAttributeInfo(decl);
            })
            .collect(Collectors.toList());
    }
}
