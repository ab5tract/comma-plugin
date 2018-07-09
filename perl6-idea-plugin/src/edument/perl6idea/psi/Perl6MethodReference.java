package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Perl6MethodReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        String type = getCallerType(call);
        List<Perl6Symbol> method = getMethodsForType(call, type, true);
        if (method.size() != 0) {
            return method.get(0) == null ? null : method.get(0).getPsi();
        } else return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        String type = getCallerType(call);
        return getMethodsForType(call, type, false).toArray();
    }

    private String getCallerType(Perl6MethodCall call) {
        // Short-circuit as !foo is always self-based
        if (call.getCallOperator().equals("!"))
            return "self";

        // Based on previous element decide what type methods we want
        PsiElement prev = call.getPrevSibling();
        // .foo or self.foo;
        if (prev == null || prev instanceof Perl6Self) {
            // No heuristic for now, not includes cases where `.foo` is called on $_, not self
            return "self";
        } else if (prev instanceof Perl6TypeName) { // Foo.foo;
            return ((Perl6TypeName)prev).getTypeName();
        } else if (prev instanceof Perl6Variable) { // $foo.foo;
            return "Any";
        }
        return "Any";
    }

    private static List getMethodsForType(Perl6MethodCall call, String type, boolean isSingle) {
        switch (type) {
            case "self": {
                return isSingle ?
                       Collections.singletonList(call.resolveSymbol(Perl6SymbolKind.Routine, call.getCallName())) :
                       call.getSymbolVariants(Perl6SymbolKind.Routine).stream().map(s -> s.getName()).collect(Collectors.toList());
            }
            default: {
                return Collections.EMPTY_LIST;
            }
        }
    }
}
