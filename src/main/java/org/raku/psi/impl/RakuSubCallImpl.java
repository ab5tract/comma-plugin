package org.raku.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.raku.extensions.RakuFrameworkCall;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.*;
import org.raku.psi.stub.RakuSubCallStub;
import org.raku.psi.stub.RakuSubCallStubElementType;
import org.raku.psi.type.RakuType;
import org.raku.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RakuSubCallImpl extends StubBasedPsiElementBase<RakuSubCallStub> implements RakuSubCall {

    public static final @NotNull TokenSet OPEN_PAREN_TOKEN_SET = TokenSet.create(RakuTokenTypes.PARENTHESES_OPEN);

    public RakuSubCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuSubCallImpl(RakuSubCallStub stub, RakuSubCallStubElementType type) {
        super(stub, type);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        RakuSubCallName call =
            RakuElementFactory.createSubCallName(getProject(), name);
        RakuSubCallName callName = getSubCallNameNode();
        if (callName != null) {
            ASTNode keyNode = callName.getNode();
            ASTNode newKeyNode = call.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }

    @Nullable
    public RakuSubCallName getSubCallNameNode() {
        return findChildByClass(RakuSubCallName.class);
    }

    @NotNull
    @Override
    public String getCallName() {
        RakuSubCallName name = getSubCallNameNode();
        return name == null ? "" : name.getCallName();
    }

    @NotNull
    @Override
    public PsiElement getWholeCallNode() {
        return this;
    }

    @Override
    public @NotNull RakuType inferType() {
        PsiElement name = getFirstChild();
        if (!(name instanceof RakuSubCallName))
            return RakuUntyped.INSTANCE;
        PsiReference ref = name.getReference();
        if (ref == null)
            return RakuUntyped.INSTANCE;
        PsiElement resolved = ref.resolve();
        if (resolved instanceof RakuRoutineDecl) {
            return ((RakuRoutineDecl)resolved).getReturnType();
        }
        return RakuUntyped.INSTANCE;
    }

    @Override
    public String getName() {
        ItemPresentation presentation = getPresentation();
        return presentation == null ? getCallName() : presentation.getPresentableText();
    }

    @Override
    public ItemPresentation getPresentation() {
        RakuFrameworkCall[] extensions = RakuFrameworkCall.EP_NAME.getExtensions();
        RakuSubCallStub stub = getStub();
        if (stub != null) {
            Map<String, String> allFrameworkData = stub.getAllFrameworkData();
            for (RakuFrameworkCall ext : extensions) {
                String prefix = ext.getFrameworkName();
                Map<String, String> frameworkData = new HashMap<>();
                for (Map.Entry<String, String> entry : allFrameworkData.entrySet())
                    if (entry.getKey().startsWith(prefix + "."))
                        frameworkData.put(entry.getKey().substring(prefix.length() + 1), entry.getValue());
                if (!frameworkData.isEmpty())
                    return ext.getNavigatePresentation(this, frameworkData);
            }
        }
        else {
            for (RakuFrameworkCall ext : extensions)
                if (ext.isApplicable(this))
                    return ext.getNavigatePresentation(this, ext.getFrameworkData(this));
        }
        return null;
    }

    @Override
    public boolean maybeCoercion() {
        return getNode().getChildren(OPEN_PAREN_TOKEN_SET).length == 1;
    }

    public String toString() {
        return getClass().getSimpleName() + "(Raku:SUB_CALL)";
    }
}
