package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Perl6MethodReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getCallName().length()));
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
        List<String> methods = getMethodsForType(call, getCallerType(call), false);
        if (call.getCallOperator().equals("!"))
            methods = methods.stream().filter(name -> name.startsWith("!")).collect(Collectors.toList());
        return methods.toArray();
    }

    private static Caller getCallerType(Perl6MethodCall call) {
        // Short-circuit as !foo is always self-based
        if (call.getCallOperator().equals("!"))
            return new Caller("self", null);

        // Based on previous element decide what type methods we want
        Perl6PsiElement prev = (Perl6PsiElement)call.getPrevSibling();

        if (prev == null) // .foo
            return new Caller("Any", null);

        return new Caller(prev.inferType(), prev);
    }

    private static List getMethodsForType(Perl6MethodCall call, Caller caller, boolean isSingle) {
        if (caller.isSelf()) {
            return isSingle ?
                   Collections.singletonList(call.resolveSymbol(Perl6SymbolKind.Method, call.getCallName())) :
                   call.getSymbolVariants(Perl6SymbolKind.Method).stream().map(s -> s.getName()).collect(Collectors.toList());
        } else if (caller.isTypeName()) {
            Perl6TypeName typeName = (Perl6TypeName)caller.getElement();
            Perl6Symbol type = typeName.resolveSymbol(Perl6SymbolKind.TypeOrConstant, typeName.getTypeName());
            if (type != null) { // If we know that type, even as external
                Perl6PackageDecl decl = (Perl6PackageDecl)type.getPsi();
                if (decl != null) { // Not external type
                    return isSingle ?
                           Collections.singletonList(resolvePackageMethod(decl, call.getCallName())) :
                           completePackageMethod(decl);
                } else {
                    return isSingle ? Collections.EMPTY_LIST :
                           tryToCompleteExternalTypeMethods(typeName.getTypeName(), typeName);
                }
            } else { // We don't know that type, assume it is derived from Mu/Any
                return isSingle ? Collections.EMPTY_LIST : MuAnyMethods(typeName, null);
            }
        } else {
            return isSingle ?
                   Collections.EMPTY_LIST :
                   tryToCompleteExternalTypeMethods(caller.getName(), (Perl6PsiElement)caller.getElement());
        }
    }

    private static List<String> MuAnyMethods(Perl6PsiElement element, Perl6VariantsSymbolCollector collector) {
        if (collector == null)
            collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Any", element).methods())
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
        for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Mu", element).methods())
            collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
        return collector.getVariants().stream().map(s -> s.getName()).collect(Collectors.toList());
    }

    private static List<String> tryToCompleteExternalTypeMethods(String name, Perl6PsiElement element) {
        Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(name, Perl6SymbolKind.ExternalPackage);
        int baseOffset = element.getTextOffset();
        element = (Perl6PsiElement)element.getParent();
        outer:
        while (element != null) {
            for (PsiElement child : element.getChildren()) {
                if (child instanceof Perl6Statement) child = child.getFirstChild();
                if ((child instanceof Perl6UseStatement || child instanceof Perl6NeedStatement) &&
                    child.getTextOffset() < baseOffset) {
                    ((Perl6SymbolContributor)child).contributeSymbols(collector);
                    if (collector.isSatisfied()) break outer;
                }
            }
            if (element instanceof Perl6File) {
                ((Perl6File)element).contributeScopeSymbols(collector);
                break outer;
            }
            element = (Perl6PsiElement)element.getParent();
        }

        Perl6ExternalPackage type = (Perl6ExternalPackage)collector.getResult();
        if (type == null) return new ArrayList<>();
        // From external types we get "raw" method names, so should be prepended with `.`
        // While our local packages properly export already prepended named
        List<String> basicMethods = MuAnyMethods(element, null);
        basicMethods.addAll(type.methods().stream().map(s -> '.' + s).collect(Collectors.toList()));
        return basicMethods;
    }

    private static List<String> completePackageMethod(Perl6PackageDecl decl) {
        Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        decl.contributeScopeSymbols(collector);
        MuAnyMethods(decl, collector);
        return collector.getVariants().stream().map(s -> s.getName()).collect(Collectors.toList());
    }

    private static Object resolvePackageMethod(Perl6PackageDecl decl, String name) {
        Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(name, Perl6SymbolKind.Method);
        decl.contributeScopeSymbols(collector);
        return collector.getResult();
    }

    static class Caller {
        private String name;
        private Object element;

        public Caller(String name, Object element) {
            this.name = name;
            this.element = element;
        }

        boolean isSelf() {
            return name.equals("self");
        }
        boolean isTypeName() {
            return element instanceof Perl6TypeName;
        }

        public Object getElement() {
            return element;
        }
        public String getName() {
            return name;
        }
    }
}
