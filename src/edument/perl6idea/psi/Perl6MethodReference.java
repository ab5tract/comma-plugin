package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6MethodReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        if (!call.getCallOperator().equals("!")) return null;
        Perl6Symbol symbol = call.resolveSymbol(Perl6SymbolKind.Routine, call.getCallName());
        return symbol != null ? symbol.getPsi() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        if (!call.getCallOperator().equals("!")) return ArrayUtil.EMPTY_OBJECT_ARRAY;
        return call.getSymbolVariants(Perl6SymbolKind.Routine)
                   .stream()
                   .map(s -> s.getName())
                   .filter(n -> n.startsWith("!"))
                   .toArray();
    }
}
