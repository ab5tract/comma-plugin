package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class Perl6VariableImpl extends ASTWrapperPsiElement implements Perl6Variable {
    public Perl6VariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6VariableReference(this);
    }

    @Override
    public PsiElement getVariableToken() {
        return findChildByType(Perl6TokenTypes.VARIABLE);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        String variableName = getVariableName();
        char sigil = Perl6Variable.getSigil(variableName);
        char twigil = Perl6Variable.getTwigil(variableName);
        String prefix = twigil != ' ' ?
                        String.valueOf(sigil) + String.valueOf(twigil) :
                        String.valueOf(sigil);
        Perl6Variable var =
            Perl6ElementFactory.createVariable(getProject(), prefix + name);
        ASTNode keyNode = getVariableToken().getNode();
        ASTNode newKeyNode = var.getVariableToken().getNode();
        getNode().replaceChild(keyNode, newKeyNode);
        return this;
    }

    @Override
    public String inferType() {
        String text = getText();
        // Special cases
        if (text.equals("$!")) return "Exception";
        if (text.equals("$/")) return "Match";
        if (text.substring(1).chars().allMatch(Character::isDigit)) return "Match";
        if (text.startsWith("$<") && text.endsWith(">")) return "Match";

        // Check if typed
        // Firstly get definition
        PsiReference ref = getReference();
        if (ref == null) return "Any";
        PsiElement resolved = ref.resolve();
        // Handle $ case
        if (text.startsWith("$")) {
            if (resolved instanceof Perl6VariableDecl) {
                Perl6VariableDecl decl = (Perl6VariableDecl)resolved;
                String type = decl.getVariableType();
                if (!type.equals(" "))
                    return type;
            } else if (resolved instanceof Perl6ParameterVariable) {
                Perl6ParameterVariable parameter = (Perl6ParameterVariable)resolved;
                String type = parameter.getVariableType();
                if (type != " ")
                    return type;
            }
        } else {
            if (resolved instanceof Perl6VariableDecl) {
                Perl6VariableDecl decl = (Perl6VariableDecl)resolved;
                String type = decl.getVariableType();
                if (type.equals(" ")) {
                    // Untyped @% variable
                    if (text.startsWith("@"))
                        return "Array";
                    if (text.startsWith("%"))
                        return "Hash";
                }
            } else if (resolved instanceof Perl6ParameterVariable) {
                Perl6ParameterVariable parameter = (Perl6ParameterVariable)resolved;
                String type = parameter.getVariableType();
                if (type == " ") {
                    if (text.startsWith("@"))
                        return "List";
                    if (text.startsWith("%"))
                        return "Map";
                }
            }
        }

        return "Any";
    }
}
