package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Perl6SignatureImpl extends ASTWrapperPsiElement implements Perl6Signature {
    public Perl6SignatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String summary(String type) {
        Perl6Parameter[] params = findChildrenByClass(Perl6Parameter.class);
        String paramsSummary = StringUtils.join(Arrays.stream(params).map(Perl6Parameter::summary).toArray(), ", ");
        if (type.equals(""))
            return "(" + paramsSummary + ")";
        else
            return "(" + paramsSummary + (paramsSummary.trim().length() == 0 ? "--> " : " -->") + type + ")";
    }
}
