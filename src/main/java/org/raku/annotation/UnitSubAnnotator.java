package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuLongName;
import org.raku.psi.RakuRoutineDecl;
import org.raku.psi.RakuScopedDecl;
import org.jetbrains.annotations.NotNull;

public class UnitSubAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuScopedDecl))
            return;

        PsiElement declarator = element.getFirstChild();
        if (declarator != null && declarator.getText().equals("unit")) {
            PsiElement next = declarator.getNextSibling();
            while (next != null) {
                if (next instanceof RakuRoutineDecl) break;
                next = next.getNextSibling();
            }
            if (next == null) return;
            RakuLongName name = PsiTreeUtil.findChildOfType(element, RakuLongName.class);
            if (name == null) return;
            if (!name.getText().equals("MAIN"))
                holder.newAnnotation(HighlightSeverity.ERROR, "The unit sub syntax is only allowed for the sub MAIN")
                    .range(declarator).create();
        }
    }
}
