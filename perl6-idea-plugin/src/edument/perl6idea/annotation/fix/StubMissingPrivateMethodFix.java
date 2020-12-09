package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.NewCodeBlockData;
import edument.perl6idea.refactoring.Perl6CodeBlockType;
import edument.perl6idea.refactoring.Perl6VariableData;
import edument.perl6idea.utils.Perl6PsiUtil;
import edument.perl6idea.utils.Perl6SignatureUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static edument.perl6idea.parsing.Perl6ElementTypes.UNTERMINATED_STATEMENT;

public class StubMissingPrivateMethodFix implements IntentionAction {
    private final String myName;
    private final Perl6MethodCall myCall;

    public StubMissingPrivateMethodFix(String name, Perl6MethodCall call) {
        myName = name;
        myCall = call;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return String.format("Create private method '%s'", myName);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6PackageDecl packageDecl = PsiTreeUtil.getParentOfType(myCall, Perl6PackageDecl.class);
        Perl6StatementList list = PsiTreeUtil.findChildOfType(packageDecl, Perl6StatementList.class);
        if (packageDecl == null || list == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method without enclosing class",
                                                "Stubbing private method", null);
            return;
        }

        PsiElement anchor = null;
        PsiElement temp = myCall;
        while (temp != null && !(temp instanceof Perl6PackageDecl)) {
            temp = temp.getParent();
            if (temp instanceof Perl6RoutineDecl) {
                anchor = temp;
            }
        }
        anchor = anchor != null ? PsiTreeUtil.getParentOfType(anchor, Perl6Statement.class, false) : Perl6PsiUtil.skipSpaces(list.getLastChild(), false);
        if (anchor == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method: can't find suitable anchor",
                                                "Stubbing private method", null);
            return;
        }

        if (anchor.getLastChild().getNode().getElementType() == UNTERMINATED_STATEMENT) {
            Perl6PsiUtil.terminateStatement(anchor);
        }

        List<String> parameters = Perl6SignatureUtils.populateParameters(myCall.getCallArguments());

        NewCodeBlockData data =
                new NewCodeBlockData(
                        Perl6CodeBlockType.PRIVATEMETHOD, "", myName, "",
                        parameters.stream().map(n -> new Perl6VariableData(n, "", false, true)).toArray(Perl6VariableData[]::new));
        Perl6Statement newMethod = Perl6ElementFactory.createNamedCodeBlock(project, data, new ArrayList<>());

        PsiElement newlyAddedMethod = list.addAfter(newMethod, anchor);

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformatNewlyAddedElement(list.getNode(), newlyAddedMethod.getNode());
        allowRename(newlyAddedMethod, editor);
    }

    private static void allowRename(PsiElement newMethod, Editor editor) {
        Collection<Perl6ParameterVariable> children = PsiTreeUtil.findChildrenOfType(newMethod, Perl6ParameterVariable.class);
        TemplateBuilder builder = new TemplateBuilderImpl(newMethod);
        for (Perl6ParameterVariable var : children) {
            builder.replaceElement(var, var.getName());
        }
        builder.run(editor, true);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
