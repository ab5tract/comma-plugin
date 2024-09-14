package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

import static org.raku.comma.parsing.RakuTokenTypes.STATEMENT_CONTROL;
import static org.raku.comma.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public interface RakuConditional extends RakuPsiElement {
    default RakuConditionalBranch[] getBranches() {
        PsiElement node = getFirstChild();
        List<RakuConditionalBranch> branches = new ArrayList<>();
        RakuConditionalBranch temp = null;

        while (node != null) {
            IElementType elementType = node.getNode().getElementType();

            // We ignore spaces
            if (elementType == UNV_WHITE_SPACE || node instanceof PsiWhiteSpace) {
                node = node.getNextSibling();
                continue;
            }

            // Create a new statement control
            if (elementType == STATEMENT_CONTROL) {
                temp = new RakuConditionalBranch(node, null, null);
            } else if (temp != null) {
                // With everything else, if condition is not encountered yet,
                // we have a condition to save
                if (temp.condition == null && !temp.term.getText().equals("else")) {
                    temp.condition = node;
                } else if (node instanceof RakuBlock) {
                    // Save a block otherwise
                    temp.block = (RakuBlock) node;
                    branches.add(temp);
                    temp = null;
                }
            }

            node = node.getNextSibling();
        }

        return branches.toArray(new RakuConditionalBranch[0]);
    }
}
