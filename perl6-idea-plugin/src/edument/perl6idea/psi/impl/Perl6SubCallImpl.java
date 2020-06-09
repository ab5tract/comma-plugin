package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.extensions.Perl6FrameworkCall;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6SubCallStub;
import edument.perl6idea.psi.stub.Perl6SubCallStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Perl6SubCallImpl extends StubBasedPsiElementBase<Perl6SubCallStub> implements Perl6SubCall {

    public static final @NotNull TokenSet OPEN_PAREN_TOKEN_SET = TokenSet.create(Perl6TokenTypes.PARENTHESES_OPEN);

    public Perl6SubCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6SubCallImpl(Perl6SubCallStub stub, Perl6SubCallStubElementType type) {
        super(stub, type);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6SubCallName call =
            Perl6ElementFactory.createSubCallName(getProject(), name);
        Perl6SubCallName callName = getSubCallNameNode();
        if (callName != null) {
            ASTNode keyNode = callName.getNode();
            ASTNode newKeyNode = call.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }

    @Nullable
    private Perl6SubCallName getSubCallNameNode() {
        return findChildByClass(Perl6SubCallName.class);
    }

    @NotNull
    @Override
    public String getCallName() {
        Perl6SubCallName name = getSubCallNameNode();
        return name == null ? "" : name.getCallName();
    }

    @Override
    public PsiElement getWholeCallNode() {
        return this;
    }

    @Override
    public @NotNull String inferType() {
        PsiElement name = getFirstChild();
        if (!(name instanceof Perl6SubCallName)) return "Mu";
        PsiReference ref = name.getReference();
        if (ref == null) return "Mu";
        PsiElement resolved = ref.resolve();
        if (resolved instanceof Perl6RoutineDecl) {
            String returnType = ((Perl6RoutineDecl)resolved).getReturnType();
            return returnType == null ? "Mu" : returnType;
        }
        return "Mu";
    }

    @Override
    public String getName() {
        ItemPresentation presentation = getPresentation();
        return presentation == null ? getCallName() : presentation.getPresentableText();
    }

    @Override
    public ItemPresentation getPresentation() {
        Perl6FrameworkCall[] extensions = Perl6FrameworkCall.EP_NAME.getExtensions();
        Perl6SubCallStub stub = getStub();
        if (stub != null) {
            Map<String, String> allFrameworkData = stub.getAllFrameworkData();
            for (Perl6FrameworkCall ext : extensions) {
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
            for (Perl6FrameworkCall ext : extensions)
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
        return getClass().getSimpleName() + "(Perl6:SUB_CALL)";
    }
}
