package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.*;
import org.raku.psi.effects.Effect;
import org.raku.psi.effects.EffectCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static org.raku.parsing.RakuElementTypes.LONG_NAME;
import static org.raku.parsing.RakuTokenTypes.METHOD_CALL_NAME;
import static org.raku.parsing.RakuTokenTypes.METHOD_CALL_OPERATOR;

public class RakuMethodCallImpl extends ASTWrapperPsiElement implements RakuMethodCall {
    public RakuMethodCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuMethodReference(this);
    }

    @NotNull
    @Override
    public String getCallName() {
        PsiElement name = getSimpleName();
        return name == null ? "" : getCallOperator() + name.getText();
    }

    @Override
    public PsiElement getSimpleName() {
        PsiElement name = findChildByType(LONG_NAME);
        if (name != null)
            return name;
        return findChildByType(METHOD_CALL_NAME);
    }

    @Override
    public boolean isTopicCall() {
        // If the parent is not a postfix application then it's a term-level method
        // call, and so on `$_`.
        PsiElement parent = getParent();
        if (!(parent instanceof RakuPostfixApplication))
            return true;
        // The other case that can happen is in `my $x = .bar.baz` then we are the
        // `.bar` part, and thus this is a topic call too.
        return ((RakuPostfixApplication)parent).getPostfix() != this;
    }

    @NotNull
    @Override
    public PsiElement getWholeCallNode() {
        RakuPostfixApplication postfixApp = PsiTreeUtil.getParentOfType(this, RakuPostfixApplication.class);
        if (postfixApp != null)
            return postfixApp;
        return this;
    }

    @NotNull
    @Override
    public String getCallOperator() {
        PsiElement op = getCallOperatorNode();
        return op == null ? "" : op.getText();
    }

    @Nullable
    @Override
    public PsiElement getCallOperatorNode() {
        return findChildByType(METHOD_CALL_OPERATOR);
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RakuLongName newLongName = RakuElementFactory.createMethodCallName(getProject(), StringUtil.trimStart(newName, "!"));
        RakuLongName longName = findChildByClass(RakuLongName.class);
        if (longName != null)
            longName.replace(newLongName);

        String oldOperator = getCallOperator();
        // If it is private and stays private OR if it is public and stays public
        if (oldOperator.equals("!") && newName.startsWith("!") || oldOperator.equals(".") && !newName.startsWith("!"))
            return this;

        PsiElement newOperator = RakuElementFactory.createMethodCallOperator(getProject(), newName.startsWith("!"));
        PsiElement operatorNode = getCallOperatorNode();
        if (operatorNode != null)
            operatorNode.replace(newOperator);
        return this;
    }

    @Override
    public @NotNull EffectCollection inferEffects() {
        return Arrays.stream(getCallArguments())
          .filter(c -> c instanceof RakuPsiElement)
          .map(c -> ((RakuPsiElement)c).inferEffects())
          .reduce(EffectCollection.EMPTY, EffectCollection::merge)
          .with(Effect.IMPURE);
    }
}
