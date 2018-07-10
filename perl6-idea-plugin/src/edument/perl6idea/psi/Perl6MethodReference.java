package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.symbols.Perl6SingleResolutionSymbolCollector;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.psi.symbols.Perl6VariantsSymbolCollector;
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
        List<Perl6Symbol> method = getMethodsForType(call, getCallerType(call), true);
        if (method.size() != 0) {
            return method.get(0) == null ? null : method.get(0).getPsi();
        } else return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6MethodCall call = (Perl6MethodCall)getElement();
        return getMethodsForType(call, getCallerType(call), false).toArray();
    }

    private static Caller getCallerType(Perl6MethodCall call) {
        // Short-circuit as !foo is always self-based
        if (call.getCallOperator().equals("!"))
            return new Caller("self");

        // Based on previous element decide what type methods we want
        PsiElement prev = call.getPrevSibling();

        if (prev == null) { // .foo
            return new Caller("Any");
        } else if (prev instanceof Perl6Self) { // self.foo;
            return new Caller("self");
        } else if (prev instanceof Perl6TypeName) { // Foo.foo;
            return new Caller((Perl6TypeName)prev);
        } else if (prev instanceof Perl6Variable) { // $foo.foo;
            return new Caller("Any");
        }
        return new Caller("Any");
    }

    private static List getMethodsForType(Perl6MethodCall call, Caller caller, boolean isSingle) {
        if (caller.isSelf()) {
            return isSingle ?
                   Collections.singletonList(call.resolveSymbol(Perl6SymbolKind.Method, call.getCallName())) :
                   call.getSymbolVariants(Perl6SymbolKind.Method).stream().map(s -> s.getName()).collect(Collectors.toList());
        } else if (caller.isTypeName()) {
            Perl6TypeName typeName = (Perl6TypeName)caller.getElement();
            Perl6Symbol type = typeName.resolveSymbol(Perl6SymbolKind.TypeOrConstant, typeName.getTypeName());
            if (type == null) return Collections.EMPTY_LIST;
            Perl6PackageDecl decl = (Perl6PackageDecl)type.getPsi();
            if (decl == null) return Collections.EMPTY_LIST;
            return isSingle ?
                   Collections.singletonList(resolvePackageMethod(decl, call.getCallName())) :
                   completePackageMethod(decl);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static List<String> completePackageMethod(Perl6PackageDecl decl) {
        Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        decl.contributeScopeSymbols(collector);
        return collector.getVariants().stream().map(s -> s.getName()).collect(Collectors.toList());
    }

    private static Object resolvePackageMethod(Perl6PackageDecl decl, String name) {
        Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(name, Perl6SymbolKind.Method);
        decl.contributeScopeSymbols(collector);
        return collector.getResult();
    }

    static class Caller {
        private Object element;

        public Caller(String pointer) {
            element = pointer;
        }
        public Caller(Perl6TypeName typeName) {
            element = typeName;
        }

        boolean isSelf() {
            return element instanceof String && element.equals("self");
        }
        boolean isTypeName() {
            return element instanceof Perl6TypeName;
        }

        public Object getElement() {
            return element;
        }
    }
}
