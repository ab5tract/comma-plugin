package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
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
        for (Perl6Parameter param : params) {
            sums.add(param.summary());
        }
        String paramsSummary = String.join(", ", sums.toArray(new String[sums.size()]));
        if (type.equals(""))
            return "(" + paramsSummary + ")";
        else
            return "(" + paramsSummary + (paramsSummary.trim().length() == 0 ? "--> " : " -->") + type + ")";
    }

    @Override
    @NotNull
    public Perl6Parameter[] getParameters() {
        return findChildrenByClass(Perl6Parameter.class);
    }
}
