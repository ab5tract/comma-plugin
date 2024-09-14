package org.raku.comma.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RakuSelfReference extends PsiReferenceBase<RakuSelf> {
    private final List<String> validPackageKinds = new ArrayList<>(
        Arrays.asList("class", "role", "monitor", "package")
    );

    public RakuSelfReference(RakuSelf self) {
        super(self, new TextRange(0, 4));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        RakuPackageDecl type = PsiTreeUtil.getParentOfType(getElement(), RakuPackageDecl.class);
        if (type != null && validPackageKinds.contains(type.getPackageKind())) {
            return type;
        }
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
}
