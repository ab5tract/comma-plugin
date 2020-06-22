package edument.perl6idea.intention;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.ui.ConflictsDialog;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.MultiMap;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MakeMethodPrivateIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6RoutineDecl decl = PsiTreeUtil.getParentOfType(element, Perl6RoutineDecl.class);
        if (decl == null) {
            return;
        }

        String privateRoutineName = "!" + decl.getRoutineName();
        Collection<PsiReference> refs = ReferencesSearch.search(decl, GlobalSearchScope.allScope(project)).findAll();

        Perl6PackageDecl owner = PsiTreeUtil.getParentOfType(decl, Perl6PackageDecl.class);
        if (owner == null) return;

        MultiMap<PsiElement, String> conflicts = new MultiMap<>();
        for (PsiReference ref : refs) {
            if (!PsiTreeUtil.isAncestor(owner, ref.getElement(), false))
                conflicts.putValue(ref.getElement(), "Usage outside of the owner package");
        }

        if (conflicts.size() != 0) {
            ConflictsDialog dialog = new ConflictsDialog(project, conflicts, () -> convertMethod(decl, privateRoutineName, refs));
            if (dialog.showAndGet()) {
                convertMethod(decl, privateRoutineName, refs);
            }
        }
        else {
            convertMethod(decl, privateRoutineName, refs);
        }
    }

    private static void convertMethod(Perl6RoutineDecl decl, String privateRoutineName, Collection<PsiReference> refs) {
        FileModificationService.getInstance().prepareFileForWrite(decl.getContainingFile());
        ApplicationManager.getApplication().runWriteAction(() -> {
            for (PsiReference ref : refs) {
                ref.handleElementRename(privateRoutineName);
            }
            // Update the declaration
            decl.setName(privateRoutineName);
        });
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element.getNode().getElementType() != Perl6TokenTypes.ROUTINE_DECLARATOR &&
            element.getNode().getElementType() != Perl6TokenTypes.ROUTINE_NAME) {
            return false;
        }

        Perl6RoutineDecl decl = PsiTreeUtil.getParentOfType(element, Perl6RoutineDecl.class);

        Perl6PackageDecl owner = PsiTreeUtil.getParentOfType(decl, Perl6PackageDecl.class);
        return owner != null &&
               (decl.getRoutineKind().equals("method") || decl.getRoutineKind().equals("multi")) &&
               !decl.getRoutineName().equals("<anon>") &&
               !decl.isPrivate();
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getFamilyName() {
        return "Make method private";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getText() {
        return getFamilyName();
    }
}
