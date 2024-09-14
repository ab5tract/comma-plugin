package org.raku.comma.descriptors;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuRegex;
import org.raku.comma.psi.RakuRegexCapturePositional;
import org.raku.comma.psi.RakuRegexGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RakuRegexSurrounder extends Surrounder {
    PsiElement createAtom(Project project);

    @Override
    default boolean isApplicable(PsiElement @NotNull [] elements) {
        return true;
    }

    default TextRange postprocess(Project project, Editor editor, PsiElement regexAtom) {
        return null;
    }

    @Nullable
    @Override
    default TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, PsiElement @NotNull [] elements)
        throws IncorrectOperationException {
        PsiElement regexAtom = createAtom(project);
        PsiElement group = PsiTreeUtil.findChildOfAnyType(regexAtom, RakuRegexGroup.class, RakuRegexCapturePositional.class);
        if (group == null)
            return null;
        for (PsiElement atom : elements) {
            group.addBefore(atom.copy(), group.getLastChild());
        }
        PsiElement regex = elements[0].getParent();
        if (regex instanceof RakuRegex) {
            regexAtom = regex.addBefore(regexAtom, elements[0]);
        }
        regex.deleteChildRange(elements[0], elements[elements.length - 1]);
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        TextRange range = postprocess(project, editor, regexAtom);
        return range == null ? regexAtom.getTextRange() : range;
    }
}
