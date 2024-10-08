package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuRegexAssertion;
import org.raku.comma.psi.RakuRegexCall;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.psi.RakuRegexDecl;

import java.util.Collection;
import java.util.List;

public class RakuRegexAssertionImpl extends RakuASTWrapperPsiElement implements RakuRegexAssertion {
    public RakuRegexAssertionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        Collection<RakuRegexCall> calls = PsiTreeUtil.findChildrenOfType(this, RakuRegexCall.class);
        if (calls.size() == 1) {
            return "$" + getText();
        } else if (!calls.isEmpty()) {
            RakuRegexCall next = calls.iterator().next();
            if (next != null) {
                return "$<" + next.getText() + ">";
            }
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
        if (getNode().findChildByType(RakuTokenTypes.REGEX_LOOKAROUND) != null) {
            return true;
        }

        // Special-case the ws rule. But only when there is not a custom ws declared in scope.
        RakuRegexCall call = PsiTreeUtil.getChildOfType(this, RakuRegexCall.class);

        if (call != null) {
            boolean wsDeclaredInScope = false;
            List<RakuRegexDecl> decls = PsiTreeUtil.collectElementsOfType(this.getContainingFile(), RakuRegexDecl.class).stream().toList();

            if (! decls.isEmpty()) {
                for (RakuRegexDecl decl : decls) {
                    if (decl.getRegexName().equals("ws")) {
                        wsDeclaredInScope = true;
                        break;
                    }
                }
            }

            String name = call.getName();
            return !wsDeclaredInScope && name != null && name.equals("ws");
        }

        return false;
    }
}
