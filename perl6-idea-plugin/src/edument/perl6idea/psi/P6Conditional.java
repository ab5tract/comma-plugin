package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_CONTROL;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public interface P6Conditional extends Perl6PsiElement {
    default Perl6ConditionalBranch[] getBranches() {
        PsiElement node = getFirstChild();
        List<Perl6ConditionalBranch> branches = new ArrayList<>();
        Perl6ConditionalBranch temp = null;

        while (node != null) {
            IElementType elementType = node.getNode().getElementType();

            // We ignore spaces
            if (elementType == UNV_WHITE_SPACE || node instanceof PsiWhiteSpace) {
                node = node.getNextSibling();
                continue;
            }

            // Create a new statement control
            if (elementType == STATEMENT_CONTROL) {
                temp = new Perl6ConditionalBranch(node, null, null);
            } else if (temp != null) {
                // With everything else, if condition is not encountered yet,
                // we have a condition to save
                if (temp.condition == null && !temp.term.getText().equals("else")) {
                    temp.condition = node;
                } else if (node instanceof Perl6Block) {
                    // Save a block otherwise
                    temp.block = (Perl6Block) node;
                    branches.add(temp);
                    temp = null;
                }
            }

            node = node.getNextSibling();
        }

        return branches.toArray(new Perl6ConditionalBranch[0]);
    }
}
