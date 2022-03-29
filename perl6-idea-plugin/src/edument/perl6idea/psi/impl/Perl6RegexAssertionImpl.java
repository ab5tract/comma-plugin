package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6RegexAssertion;
import edument.perl6idea.psi.Perl6RegexCall;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Perl6RegexAssertionImpl extends ASTWrapperPsiElement implements Perl6RegexAssertion {
    public Perl6RegexAssertionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        Collection<Perl6RegexCall> calls = PsiTreeUtil.findChildrenOfType(this, Perl6RegexCall.class);
        if (calls.size() == 1)
            return "$" + getText();
        else if (!calls.isEmpty()) {
            Perl6RegexCall next = calls.iterator().next();
            if (next != null)
                return "$<" + next.getText() + ">";
        }
        return null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean mightMatchZeroWidth() {
        // Anything with ? or ! will be zero-width.
        if (getNode().findChildByType(Perl6TokenTypes.REGEX_LOOKAROUND) != null)
            return true;

        // Special-case the ws rule.
        Perl6RegexCall call = PsiTreeUtil.getChildOfType(this, Perl6RegexCall.class);
        if (call != null) {
            String name = call.getName();
            return name != null && name.equals("ws");
        }

        return false;
    }
}
