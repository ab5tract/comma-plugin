package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.symbols.Perl6SingleResolutionSymbolCollector;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.psi.type.Perl6Type;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6IsTraitReference extends PsiReferenceBase<Perl6PsiElement> {
    static final String[] ROUTINE_DEFAULT_TRAITS = new String[]{
        "assoc", "tighter", "looser", "equiv", "default",
        "export", "rw", "raw", "nodal", "pure"
    };
    static final String[] VARIABLE_DEFAULT_TRAITS = new String[]{
        "default", "required", "DEPRECATED", "rw"
    };
    static final String[] PARAMETER_DEFAULT_TRAITS = new String[]{
        "copy", "raw", "rw", "readonly", "required"
    };
    static final String[] REGEX_DEFAULT_TRAITS = new String[]{
        "DEPRECATED", "export"
    };

    public Perl6IsTraitReference(Perl6PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6PsiElement ref = getElement();
        String typeName = ref.getText();
        Perl6Symbol result = ref.resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, typeName);
        if (result != null) {
            PsiElement psi = result.getPsi();
            if (psi != null) {
                // It's fine if it's either imported or declared ahead of the point
                // it is being referenced.
                if (psi.getContainingFile() != ref.getContainingFile())
                    return psi;
                if (psi.getTextOffset() < ref.getTextOffset())
                    return psi;
            }
        }
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        Perl6Trait trait = PsiTreeUtil.getParentOfType(getElement(), Perl6Trait.class);
        if (trait == null) return ArrayUtil.EMPTY_OBJECT_ARRAY;
        PsiElement owner = trait.getParent();
        if (owner == null) return ArrayUtil.EMPTY_OBJECT_ARRAY;

        if (owner instanceof Perl6Parameter) {
            return PARAMETER_DEFAULT_TRAITS;
        }
        else if (owner instanceof Perl6VariableDecl) {
            List<Object> types = new ArrayList<>(Arrays.asList(new Perl6TypeNameReference(myElement).getVariants()));
            types.addAll(Arrays.asList(VARIABLE_DEFAULT_TRAITS));
            if (!((Perl6VariableDecl)owner).getScope().equals("my"))
                types.add("export");
            if (((Perl6VariableDecl)owner).getScope().equals("has"))
                gatherExternalTraits(types, "Attribute");
            return types.toArray();
        }
        else if (owner instanceof Perl6RoutineDecl) {
            List<Object> options = new ArrayList<>();
            gatherExternalTraits(options, "Routine");
            return ArrayUtils.addAll(options.toArray(), ROUTINE_DEFAULT_TRAITS);
        }
        else if (owner instanceof Perl6RegexDecl) {
            List<Object> traits = new ArrayList<>(Arrays.asList(REGEX_DEFAULT_TRAITS));
            traits.addAll(Arrays.asList(ROUTINE_DEFAULT_TRAITS));
            return traits.toArray();
        }
        else if (owner instanceof Perl6PackageDecl) {
            List<Object> types = new ArrayList<>(Arrays.asList(new Perl6TypeNameReference(myElement).getVariants()));
            types.add("export");
            return types.toArray();
        }
        else {
            return ArrayUtil.EMPTY_OBJECT_ARRAY;
        }
    }

    private void gatherExternalTraits(List<Object> types, String traitType) {
        Perl6SingleResolutionSymbolCollector subsCollector = new Perl6SingleResolutionSymbolCollector("trait_mod:<is>", Perl6SymbolKind.Routine);
        myElement.applyLexicalSymbolCollector(subsCollector);
        for (Perl6Symbol symbol : subsCollector.getResults()) {
            Perl6RoutineDecl decl = (Perl6RoutineDecl)symbol.getPsi();
            Perl6Parameter[] params = decl.getParams();
            if (params.length != 2)
                continue;
            Perl6Type caller = params[0].inferType();
            if (caller.getName().equals(traitType)) {
                List<String> traitNames = params[1].getVariableNames();
                for (String traitName : traitNames) {
                    if (traitName.length() > 1)
                        types.add(traitName.substring(1));
                }
            }
        }
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        Perl6IsTraitName isTraitName = (Perl6IsTraitName)getElement();
        return isTraitName.setName(newElementName);
    }
}
