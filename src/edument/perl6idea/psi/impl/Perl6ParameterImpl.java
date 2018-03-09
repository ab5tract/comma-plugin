package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.PARAMETER_QUANTIFIER;

public class Perl6ParameterImpl extends ASTWrapperPsiElement implements Perl6Parameter {
    public Perl6ParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary() {
        Perl6SignatureImpl maybeSig = findChildByClass(Perl6SignatureImpl.class);
        if (maybeSig != null) {
            if (maybeSig.getText().charAt(0) == '(')
                return "$";
            if  (maybeSig.getText().charAt(0) == '[')
                return "@";
        }
        Perl6NamedParameterImpl maybeNamed = findChildByClass(Perl6NamedParameterImpl.class);
        if (maybeNamed != null)
            return maybeNamed.summary();

        StringBuilder summary = new StringBuilder();
        Perl6TypeNameImpl type = findChildByClass(Perl6TypeNameImpl.class);
        if (type != null)
            summary.append(type.getText()).append(" ");
        PsiElement maybeSlurpy = getFirstChild();
        if (maybeSlurpy.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeSlurpy.getText());
        Perl6ParameterVariableImpl parameter = findChildByClass(Perl6ParameterVariableImpl.class);
        if (parameter != null)
            summary.append(parameter.getText().charAt(0));
        Perl6TermDefinitionImpl term = findChildByClass(Perl6TermDefinitionImpl.class);

        if (term != null && term.getPrevSibling().getText().equals("\\"))
            summary.append(term.getText());

        PsiElement maybeQuant = getLastChild();
        if (maybeQuant.getNode().getElementType() == PARAMETER_QUANTIFIER)
            summary.append(maybeQuant.getText());

        return summary.toString();
    }
}
