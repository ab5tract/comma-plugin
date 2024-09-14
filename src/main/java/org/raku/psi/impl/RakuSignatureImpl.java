package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.util.ArrayUtil;
import org.raku.psi.RakuParameter;
import org.raku.psi.RakuSignature;
import org.raku.psi.type.RakuType;
import org.raku.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RakuSignatureImpl extends ASTWrapperPsiElement implements RakuSignature {
    public RakuSignatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary(RakuType type) {
        RakuParameter[] params = getParameters();
        List<String> sums = new ArrayList<>();
        for (RakuParameter param : params)
            sums.add(param.summary(false));
        String paramsSummary = String.join(", ", ArrayUtil.toStringArray(sums));
        if (type instanceof RakuUntyped)
            return String.format("(%s)", paramsSummary);
        else
            return String.format("(%s%s--> %s)", paramsSummary, paramsSummary.isEmpty() ? "" : " ", type.getName());
    }

    @Override
    @NotNull
    public RakuParameter[] getParameters() {
        RakuParameter[] params = findChildrenByClass(RakuParameter.class);
        if (params.length != 0 && params[0].getNextSibling() != null && params[0].getNextSibling().getText().equals(":"))
            return Arrays.copyOfRange(params, 1, params.length);
        return params;
    }
}
