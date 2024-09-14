package org.raku.comma.annotation.fix;

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
import org.raku.comma.psi.*;
import org.raku.comma.refactoring.NewCodeBlockData;
import org.raku.comma.refactoring.RakuCodeBlockType;
import org.raku.comma.refactoring.RakuVariableData;
import org.raku.comma.utils.RakuPsiUtil;
import org.raku.comma.utils.RakuSignatureUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.raku.comma.parsing.RakuElementTypes.UNTERMINATED_STATEMENT;

public class StubMissingPrivateMethodFix implements IntentionAction {
    private final String myName;
    private final RakuMethodCall myCall;

    public StubMissingPrivateMethodFix(String name, RakuMethodCall call) {
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
        RakuPackageDecl packageDecl = PsiTreeUtil.getParentOfType(myCall, RakuPackageDecl.class);
        RakuStatementList list = PsiTreeUtil.findChildOfType(packageDecl, RakuStatementList.class);
        if (packageDecl == null || list == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method without enclosing class",
                                                "Stubbing private method", null);
            return;
        }

        PsiElement anchor = null;
        PsiElement temp = myCall;
        while (temp != null && !(temp instanceof RakuPackageDecl)) {
            temp = temp.getParent();
            if (temp instanceof RakuRoutineDecl) {
                anchor = temp;
            }
        }
        anchor = anchor != null ? PsiTreeUtil.getParentOfType(anchor, RakuStatement.class, false) : RakuPsiUtil.skipSpaces(list.getLastChild(), false);
        if (anchor == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method: can't find suitable anchor",
                                                "Stubbing private method", null);
            return;
        }

        if (anchor.getLastChild().getNode().getElementType() == UNTERMINATED_STATEMENT) {
            RakuPsiUtil.terminateStatement(anchor);
        }

        List<String> parameters = RakuSignatureUtils.populateParameters(myCall.getCallArguments());

        NewCodeBlockData data =
                new NewCodeBlockData(
                  RakuCodeBlockType.PRIVATEMETHOD, "", myName, "",
                  parameters.stream().map(n -> new RakuVariableData(n, "", false, true)).toArray(RakuVariableData[]::new));
        RakuStatement newMethod = RakuElementFactory.createNamedCodeBlock(project, data, new ArrayList<>());

        PsiElement newlyAddedMethod = list.addAfter(newMethod, anchor);

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformatNewlyAddedElement(list.getNode(), newlyAddedMethod.getNode());
        allowRename(newlyAddedMethod, editor);
    }

    private static void allowRename(PsiElement newMethod, Editor editor) {
        Collection<RakuParameterVariable> children = PsiTreeUtil.findChildrenOfType(newMethod, RakuParameterVariable.class);
        TemplateBuilder builder = new TemplateBuilderImpl(newMethod);
        for (RakuParameterVariable var : children) {
            builder.replaceElement(var, var.getName());
        }
        builder.run(editor, true);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
