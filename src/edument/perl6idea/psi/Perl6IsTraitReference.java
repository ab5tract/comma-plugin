package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.impl.Perl6LongNameImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.NAME;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6IsTraitReference extends PsiReferenceBase<Perl6PsiElement> {
    static String[] ROUTINE_DEFAULT_TRAITS = new String[]{
      "assoc", "tighter", "looser", "equiv", "default", "export"
    };
    static String[] VARIABLE_DEFAULT_TRAITS = new String[]{
      "default", "required", "DEPRECATED", "export"
    };
    static String[] PARAMETER_DEFAULT_TRAITS = new String[]{
      "copy", "raw", "rw", "readonly", "required"
    };
    static String[] REGEX_DEFAULT_TRAITS = new String[]{
      "rw", "raw", "pure", "DEPRECATED", "inlinable", "nodal", "prec", "export"
    };

    public Perl6IsTraitReference(Perl6PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6Trait trait = PsiTreeUtil.getParentOfType(getElement(), Perl6Trait.class);
        if (trait == null) return ArrayUtil.EMPTY_OBJECT_ARRAY;
        PsiElement prevSibling = null;
        for (PsiElement e = trait.getPrevSibling(); e != null && prevSibling == null; e = e.getPrevSibling())
            if (e.getNode().getElementType() != UNV_WHITE_SPACE)
                prevSibling = e;
        if (prevSibling == null) return ArrayUtil.EMPTY_OBJECT_ARRAY;

        if (prevSibling instanceof Perl6ParameterVariable) {
            return PARAMETER_DEFAULT_TRAITS;
        } else if (prevSibling instanceof Perl6Variable) {
            List<Object> types = new ArrayList<>(Arrays.asList(new Perl6TypeNameReference(myElement).getVariants()));
            types.addAll(Arrays.asList(VARIABLE_DEFAULT_TRAITS));
            return types.toArray();
        } else if (prevSibling instanceof Perl6Signature) {
            return ROUTINE_DEFAULT_TRAITS;
        } else if (prevSibling instanceof Perl6LongNameImpl) {
            List<Object> traits = new ArrayList<>(Arrays.asList(REGEX_DEFAULT_TRAITS));
            traits.addAll(Arrays.asList(ROUTINE_DEFAULT_TRAITS));
            return traits.toArray();
        } else if (prevSibling.getNode().getElementType() == NAME) {
            List<Object> types = new ArrayList<>(Arrays.asList(new Perl6TypeNameReference(myElement).getVariants()));
            types.add("export");
            return types.toArray();
        } else {
            return ArrayUtil.EMPTY_OBJECT_ARRAY;
        }

    }
}
