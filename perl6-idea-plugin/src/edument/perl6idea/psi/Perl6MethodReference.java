package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edument.perl6idea.parsing.Perl6TokenTypes.SELF;

public class Perl6MethodReference extends PsiReferenceBase.Poly<Perl6MethodCall> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getCallName().length()), false);
    }

    static class CallInfo {
        private final String targetTypeName;
        private final Object targetTypeElement;
        private final String methodName;
        private boolean trustNeeded = false;

        CallInfo(String targetTypeName, Object targetTypeElement, String methodName) {
            this.targetTypeName = targetTypeName;
            this.targetTypeElement = targetTypeElement;
            this.methodName = methodName;
        }

        CallInfo(String targetTypeName, Object targetTypeElement, String methodName, boolean trustNeeded) {
            this(targetTypeName, targetTypeElement, methodName);
            this.trustNeeded = trustNeeded;
        }

        boolean isSelf() {
            return targetTypeName.equals("self");
        }

        public String getTargetTypeName() {
            return targetTypeElement instanceof Perl6TypeName ? ((Perl6TypeName)targetTypeElement).getTypeName() : targetTypeName;
        }

        /**
         * Gets the method name with a . or ! prefix.
         */
        public String getMethodName() {
            return methodName;
        }

        public boolean isTrustNeeded() {
            return trustNeeded;
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Perl6MethodCall call = getElement();
        Collection<Perl6Symbol> method = getMethodsForType(call, getCallInfo(call), true);
        return method.stream()
                .map(s -> s.getPsi())
                .filter(p -> p != null)
                .map(p -> new PsiElementResolveResult(p))
                .collect(Collectors.toList())
                .toArray(ResolveResult.EMPTY_ARRAY);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6MethodCall call = getElement();
        Collection<Perl6Symbol> methods = getMethodsForType(call, getCallInfo(call), false);
        if (call.getCallOperator().equals("!"))
            methods = ContainerUtil.filter(methods, method -> method.getName().startsWith("!"));
        // TODO use
        return methods.stream().map(m -> m.getName()).toArray();
    }

    /**
     * Calculates the type that we are calling the method on.
     */
    private static CallInfo getCallInfo(Perl6MethodCall call) {
        // Check if the targetTypeName contains an explicitly qualified part.
        String name = call.getCallName();
        if (name.length() >= 5) { // Minimum case is !A::m
            boolean isPrivate = name.charAt(0) == '!';
            int lastSep = name.lastIndexOf("::");
            if (lastSep >= 0) {
                String typeName = name.substring(1, lastSep);
                String shortName = name.substring(0, 1) + name.substring(lastSep + 2);
                return new CallInfo(typeName, null, shortName, isPrivate);
            }
        }

        PsiElement firstElement = call.findElementAt(0);
        if (firstElement != null && firstElement.getNode().getElementType() == SELF)
            return new CallInfo("self", null, name);

        // Based on previous targetTypeElement decide what type methods we want
        PsiElement prev = call.getPrevSibling();

        if (prev instanceof PsiWhiteSpace)
            return new CallInfo("self", null, name);

        if (prev == null) // .foo
            return new CallInfo("Mu", null, name);

        if (prev instanceof Perl6PsiElement) {
            Perl6PsiElement element = (Perl6PsiElement) prev;
            String type = element.inferType();
            type = type == null ? "Mu" : type;
            return new CallInfo(type, prev, name);
        }
        else
            return null;
    }

    private static Collection<Perl6Symbol> getMethodsForType(Perl6MethodCall call, CallInfo callInfo, boolean isSingle) {
        if (callInfo == null)
            return Collections.emptyList();
        if (callInfo.isSelf()) {
            Perl6PackageDecl enclosingPackage = PsiTreeUtil.getParentOfType(call, Perl6PackageDeclImpl.class);
            if (enclosingPackage != null)
                return getMethodsFromPsiType(callInfo, isSingle, enclosingPackage, true);
            return Collections.emptyList();
        }
        else {
            return getMethodsByTypeName(call, callInfo, isSingle);
        }
    }

    @NotNull
    private static Collection<Perl6Symbol> getMethodsFromPsiType(CallInfo callinfo, boolean isSingle, Perl6PackageDecl enclosingPackage, boolean privatesVisible) {
        MOPSymbolsAllowed symbolsAllowed = new MOPSymbolsAllowed(privatesVisible, privatesVisible, true, false);
        if (isSingle) {
            Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(callinfo.getMethodName(), Perl6SymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return collector.getResults();
        }
        else {
            Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return collector.getVariants();
        }
    }

    private static Collection<Perl6Symbol> getMethodsByTypeName(Perl6MethodCall call, CallInfo callInfo, boolean isSingle) {
        String typeName = callInfo.getTargetTypeName();
        Perl6Symbol type = call.resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, typeName);
        if (type != null) { // If we know that type, even as an external one
            PsiElement psiDeclaration = type.getPsi();
            Perl6PackageDecl decl;
            if (psiDeclaration instanceof Perl6PackageDecl) {
                return getMethodsFromPsiType(callInfo, isSingle, (Perl6PackageDecl)psiDeclaration, callInfo.isTrustNeeded());
            } else if (psiDeclaration instanceof Perl6Subset) {
                Perl6Subset subset = (Perl6Subset)psiDeclaration;
                // Get original type of subset
                decl = subset.getSubsetBaseType();
                if (decl == null) {
                    return tryToCompleteExternalTypeMethods("Any", call);
                } else {
                    return getMethodsFromPsiType(callInfo, isSingle, decl, callInfo.isTrustNeeded());
                }
            } else if (psiDeclaration instanceof Perl6Enum) {
                if (!isSingle) {
                    Collection<Perl6Symbol> intMethods = tryToCompleteExternalTypeMethods("Int", call);
                    Collection<Perl6Symbol> enumerationMethods = tryToCompleteExternalTypeMethods("Enumeration", call);
                    return Stream.concat(intMethods.stream(), enumerationMethods.stream()).collect(Collectors.toCollection(ArrayList::new));
                }
            }
            // We never reach this
            return new ArrayList<>();
        } else {
            // We don't know that type, assume it is derived from Mu/Any. No privates or
            // attributes there, so simple
            return isSingle ? Collections.emptyList(): MuAnyMethods(call, null);
        }
    }

    public static Collection<Perl6Symbol> MuAnyMethods(Perl6PsiElement element, @Nullable Perl6VariantsSymbolCollector collector) {
        if (collector == null)
            collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        Perl6PackageDecl any = Perl6SdkType.getInstance().getCoreSettingSymbol("Any", element);
        if (any != null)
            any.contributeMOPSymbols(collector, new MOPSymbolsAllowed(true, true, true, true));
        Perl6PackageDecl mu = Perl6SdkType.getInstance().getCoreSettingSymbol("Mu", element);
        if (mu != null)
            mu.contributeMOPSymbols(collector, new MOPSymbolsAllowed(true, true, true, true));
        return collector.getVariants();
    }

    private static Collection<Perl6Symbol> tryToCompleteExternalTypeMethods(String typeName, Perl6PsiElement element) {
        Perl6SingleResolutionSymbolCollector externalPackageCollector = new Perl6SingleResolutionSymbolCollector(typeName, Perl6SymbolKind.TypeOrConstant);
        element.applyExternalSymbolCollector(externalPackageCollector);
        Perl6Symbol type = externalPackageCollector.getResult();
        if (type == null || !(type.getPsi() instanceof Perl6PackageDecl))
            return MuAnyMethods(element, null);

        Perl6VariantsSymbolCollector methodsCollector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        Perl6PackageDecl packageDecl = (Perl6PackageDecl)type.getPsi();
        packageDecl.contributeMOPSymbols(methodsCollector, new MOPSymbolsAllowed(true, true, true, true));
        MuAnyMethods(element, methodsCollector);
        return methodsCollector.getVariants();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement resolved = resolve();
        Perl6MethodCall call = myElement;
        if (resolved instanceof Perl6LongName && !call.getCallName().startsWith("!"))
            throw new IncorrectOperationException("Rename for non-private methods is not yet supported");
        return call.setName(newElementName);
    }
}
