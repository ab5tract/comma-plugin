package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6SignatureImpl extends ASTWrapperPsiElement implements Perl6Signature {
    public Perl6SignatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary(Perl6Type type) {
        Perl6Parameter[] params = getParameters();
        List<String> sums = new ArrayList<>();
        for (Perl6Parameter param : params)
            sums.add(param.summary(false));
        String paramsSummary = String.join(", ", ArrayUtil.toStringArray(sums));
        if (type instanceof Perl6Untyped)
            return String.format("(%s)", paramsSummary);
        else
            return String.format("(%s%s--> %s)", paramsSummary, paramsSummary.isEmpty() ? "" : " ", type.getName());
    }

    @Override
    @NotNull
    public Perl6Parameter[] getParameters() {
        Perl6Parameter[] params = findChildrenByClass(Perl6Parameter.class);
        if (params.length != 0 && params[0].getNextSibling() != null && params[0].getNextSibling().getText().equals(":"))
            return Arrays.copyOfRange(params, 1, params.length);
        return params;
    }
}
