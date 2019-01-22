package edument.perl6idea.psi;

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

public class Perl6SelfReference extends PsiReferenceBase<Perl6Self> {
    private List<String> validPackageKinds = new ArrayList<>(
        Arrays.asList("class", "role", "monitor", "package")
    );

    public Perl6SelfReference(Perl6Self self) {
        super(self, new TextRange(0, 4));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6PackageDecl type = PsiTreeUtil.getParentOfType(getElement(), Perl6PackageDecl.class);
        if (type != null && validPackageKinds.contains(type.getPackageKind())) {
            return type;
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
}
