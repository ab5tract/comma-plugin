package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6SignatureImpl extends ASTWrapperPsiElement implements Perl6Signature {
    public Perl6SignatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary(String type) {
        Perl6Parameter[] params = getParameters();
        List<String> sums = new ArrayList<>();
        for (Perl6Parameter param : params)
            sums.add(param.summary());
        String paramsSummary = String.join(", ", ArrayUtil.toStringArray(sums));
        if (type.isEmpty())
            return String.format("(%s)", paramsSummary);
        else
            return String.format("(%s%s--> %s)", paramsSummary, paramsSummary.isEmpty() ? "" : " ", type);
    }

    @Override
    @NotNull
    public Perl6Parameter[] getParameters() {
        return findChildrenByClass(Perl6Parameter.class);
    }
}
