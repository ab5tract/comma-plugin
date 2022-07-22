package edument.perl6idea.refactoring;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RakuExtractPackageUtil {
    public static boolean extractPackage(@NotNull Project project,
                                         Perl6PackageDecl aPackage,
                                         String name,
                                         boolean role,
                                         Collection<RakuAttributeInfo> attributes) {
        Perl6PackageDecl newPackageDecl = CompletePerl6ElementFactory.createPackage(project, role ? "role" : "class", name);

        Perl6StatementList newPackageList = PsiTreeUtil.findChildOfType(newPackageDecl, Perl6StatementList.class);
        Perl6StatementList list = PsiTreeUtil.findChildOfType(aPackage, Perl6StatementList.class);

        if (list == null || newPackageList == null) {
            return false;
        }

        List<PsiElement> statementsToDelete = new ArrayList<>();

        for (PsiElement statement : list.getChildren()) {
            if (statement instanceof Perl6Statement) {
                PsiElement maybeDecl = statement.getFirstChild();
                if (maybeDecl instanceof Perl6RoutineDecl) {
                    for (RakuAttributeInfo attribute : attributes) {
                        if (PsiEquivalenceUtil.areElementsEquivalent(maybeDecl, attribute.getMember())) {
                            newPackageList.add(statement.copy());
                            newPackageList.add(Perl6ElementFactory.createNewLine(project));
                            statementsToDelete.add(statement);
                        }
                    }
                }
                else if (maybeDecl instanceof Perl6ScopedDecl && Objects.equals(((Perl6ScopedDecl)maybeDecl).getScope(), "has")) {
                    PsiElement actualVarDeclaration = maybeDecl.getLastChild();
                    for (RakuAttributeInfo attribute : attributes) {
                        if (PsiEquivalenceUtil.areElementsEquivalent(actualVarDeclaration, attribute.getMember())) {
                            newPackageList.add(statement.copy());
                            newPackageList.add(Perl6ElementFactory.createNewLine(project));
                            statementsToDelete.add(statement);
                        }
                    }
                }
            }
        }

        statementsToDelete.forEach(PsiElement::delete);

        PsiElement formattedElement = CodeStyleManager.getInstance(project).reformat(newPackageDecl);
        PsiElement packageStatement = aPackage.getParent();
        PsiElement newPackageStatement = packageStatement.copy();
        newPackageStatement.deleteChildRange(newPackageStatement.getFirstChild(), newPackageStatement.getFirstChild());
        newPackageStatement.add(formattedElement);

        PsiElement line = Perl6ElementFactory.createNewLine(project);
        line = packageStatement.getParent().addBefore(line, packageStatement);
        packageStatement.getParent().addBefore(newPackageStatement, line);

        return true;
    }

    public static List<RakuAttributeInfo> getAllMemberInfo(Perl6PackageDecl aPackage, boolean isRole) {
        List<Perl6PsiDeclaration> decls = aPackage.getDeclarations();
        return decls
            .stream()
            .filter(decl -> {
                if (decl instanceof Perl6RoutineDecl) {
                    return ((Perl6RoutineDecl)decl).isMethod();
                }
                if (decl instanceof Perl6VariableDecl) {
                    return Objects.equals(decl.getScope(), "has");
                }
                return false;
            })
            .filter(decl -> {
                if (isRole) {
                    return true;
                }
                // private things are not allowed for parent classes for now
                if (decl instanceof Perl6VariableDecl) {
                    Perl6Variable[] variables = ((Perl6VariableDecl)decl).getVariables();
                    for (Perl6Variable variable : variables) {
                        if (Perl6Variable.getTwigil(variable.getVariableName()) == '!')
                            return false;
                    }
                    return true;
                }
                return !((Perl6RoutineDecl)decl).isPrivate();
            })
            .map(decl -> {
                return new RakuAttributeInfo(decl);
            })
            .collect(Collectors.toList());
    }
}
