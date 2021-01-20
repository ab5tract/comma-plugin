package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6TypeNameStub;
import edument.perl6idea.psi.stub.Perl6TypeNameStubElementType;
import edument.perl6idea.psi.type.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6TypeNameImpl extends StubBasedPsiElementBase<Perl6TypeNameStub> implements Perl6TypeName {
    public Perl6TypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6TypeNameImpl(Perl6TypeNameStub stub, Perl6TypeNameStubElementType type) {
        super(stub, type);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6TypeNameReference(this);
    }

    @Override
    public String getTypeName() {
        Perl6TypeNameStub stub = getStub();
        if (stub != null)
            return stub.getTypeName();
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        assert longName != null; // We always parse one at the start of a type name
        return longName.getNameWithoutColonPairs();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:TYPE_NAME)";
    }

    @Override
    public @NotNull Perl6Type inferType() {
        PsiElement resolution = getReference().resolve();
        return tweakType(resolution instanceof Perl6PsiElement
               ? new Perl6ResolvedType(getTypeName(), (Perl6PsiElement)resolution)
               : new Perl6UnresolvedType(getTypeName()));
    }

    private Perl6Type tweakType(Perl6Type type) {
        // Handle definedness type
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        for (Perl6ColonPair pair : longName.getColonPairs()) {
            if (pair.getKey().equals("D")) {
                type = new Perl6DefinednessType(type, true);
                break;
            }
            if (pair.getKey().equals("U")) {
                type = new Perl6DefinednessType(type, false);
                break;
            }
        }

        // Coercion or parametric type.
        if (getNode().findChildByType(Perl6TokenTypes.TYPE_COERCION_PARENTHESES_CLOSE) != null) {
            // Coercion is another embedded type name.
            Perl6TypeName from = findChildByClass(Perl6TypeName.class);
            if (from != null)
                type = new Perl6CoercionType(type, from.inferType());
        }
        else  {
            ASTNode curToken = getNode().findChildByType(Perl6TokenTypes.TYPE_PARAMETER_BRACKET);
            if (curToken != null) {
                List<Perl6Type> typeArgs = new ArrayList<>();
                PsiElement arg = Perl6PsiUtil.skipSpaces(curToken.getPsi().getNextSibling(), true);
                if (arg instanceof Perl6InfixApplication && ((Perl6InfixApplication)arg).isCommaOperator()) {
                    // List of parameters (for now, we assume all are types).
                    PsiElement[] operands = ((Perl6InfixApplication)arg).getOperands();
                    for (PsiElement operand : operands) {
                        if (operand instanceof Perl6PsiElement)
                            typeArgs.add(((Perl6PsiElement)operand).inferType());
                    }
                }
                else {
                    // One parameter (for now, we assume it's a type).
                    if (arg instanceof Perl6PsiElement)
                        typeArgs.add(((Perl6PsiElement)arg).inferType());
                }
                if (!typeArgs.isEmpty())
                    type = new Perl6ParametricType(type, typeArgs.toArray(new Perl6Type[0]));
            }
        }

        return type;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6LongName type = Perl6ElementFactory
            .createTypeName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = type.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
