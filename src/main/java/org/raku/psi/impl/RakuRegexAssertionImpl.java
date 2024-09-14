package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuRegexAssertion;
import org.raku.psi.RakuRegexCall;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RakuRegexAssertionImpl extends ASTWrapperPsiElement implements RakuRegexAssertion {
    public RakuRegexAssertionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        Collection<RakuRegexCall> calls = PsiTreeUtil.findChildrenOfType(this, RakuRegexCall.class);
        if (calls.size() == 1)
            return "$" + getText();
        else if (!calls.isEmpty()) {
            RakuRegexCall next = calls.iterator().next();
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
        if (getNode().findChildByType(RakuTokenTypes.REGEX_LOOKAROUND) != null)
            return true;

        // Special-case the ws rule.
        RakuRegexCall call = PsiTreeUtil.getChildOfType(this, RakuRegexCall.class);
        if (call != null) {
            String name = call.getName();
            return name != null && name.equals("ws");
        }

        return false;
    }
}
