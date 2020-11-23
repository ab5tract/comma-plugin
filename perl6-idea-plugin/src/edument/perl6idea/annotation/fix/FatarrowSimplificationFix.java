package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.annotation.NamedPairArgumentAnnotator;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6FatArrow;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class FatarrowSimplificationFix implements IntentionAction {
    private PsiElement myArrow;
    private String mySimplifiedPair;
    private boolean isQuickfix = false;

    // When called as an annotation
    public FatarrowSimplificationFix() {}

    // When called as a fix
    public FatarrowSimplificationFix(PsiElement arrow, String simplifiedPair) {
        myArrow = arrow;
        mySimplifiedPair = simplifiedPair;
        isQuickfix = true;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Convert to " + (mySimplifiedPair == null ? "colonpair" : ":" + mySimplifiedPair);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert to simpler pair literal";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (isQuickfix)
            return true;
        Perl6FatArrow arrow = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), Perl6FatArrow.class);
        return arrow != null && NamedPairArgumentAnnotator.getSimplifiedText(arrow.getKey(), arrow.getValue()) == null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (mySimplifiedPair != null) {
            Perl6ColonPair newPair = Perl6ElementFactory.createColonPair(project, mySimplifiedPair);
            myArrow.replace(newPair);
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
        } else {
            Perl6FatArrow arrow = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), Perl6FatArrow.class);
            assert arrow != null;
            arrow.replace(Perl6ElementFactory.createColonPair(project, String.format("%s(%s)",
                    arrow.getKey(), arrow.getValue().getText())));
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
