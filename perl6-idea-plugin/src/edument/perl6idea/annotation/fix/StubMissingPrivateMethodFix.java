package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

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
        return "Perl 6";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6PackageDecl packageDecl = PsiTreeUtil.getParentOfType(myCall, Perl6PackageDecl.class);
        Perl6StatementList list = packageDecl == null ?
                                  null :
                                  PsiTreeUtil.findChildOfType(packageDecl, Perl6StatementList.class);
        if (packageDecl == null || list == null) {
            CommonRefactoringUtil.showErrorHint(project, editor, "Cannot stub private method without enclosing class",
                                                "Stubbing private method", null);
            return;
        }

        Perl6RoutineDecl decl = null;
        PsiElement answer = myCall.getParent();
        do {
            if (answer == null || answer instanceof Perl6PackageDecl) break;
            if (answer instanceof Perl6RoutineDecl)
                decl = (Perl6RoutineDecl)answer;
            answer = answer.getParent();
        } while (true);

        PsiElement anchor = decl != null ? PsiTreeUtil.getParentOfType(decl, Perl6Statement.class) : list.getLastChild();
        PsiElement newMethod = Perl6ElementFactory.createPrivateMethod(project, myName);
        list.getNode().addChild(newMethod.getNode(), anchor.getNode());
        addPossibleNewline(list, anchor);
        list.getNode().addChild(new PsiWhiteSpaceImpl("\n"), anchor.getNode());
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        CodeStyleManager.getInstance(project).reformat(list);
    }

    //FIXME A HACK ADDED TO BACK UP FORMATTER THAT DOES NOT HANDLE IT YET
    private static void addPossibleNewline(Perl6StatementList list, PsiElement anchor) {
        list.getNode().addChild(new PsiWhiteSpaceImpl("\n"), anchor.getNode());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
