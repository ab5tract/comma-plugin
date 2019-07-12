package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiWhiteSpace;
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
import java.util.Collections;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.SELF;

public class Perl6MethodReference extends PsiReferenceBase<Perl6MethodCall> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getCallName().length()));
    }

    static class CallInfo {
        private final String targetTypeName;
        private final Object targetTypeElement;
        private final String methodName;
        private boolean trustNeeded = false;

        public CallInfo(String targetTypeName, Object targetTypeElement, String methodName) {
            this.targetTypeName = targetTypeName;
            this.targetTypeElement = targetTypeElement;
            this.methodName = methodName;
        }

        public CallInfo(String targetTypeName, Object targetTypeElement, String methodName, boolean trustNeeded) {
            this(targetTypeName, targetTypeElement, methodName);
            this.trustNeeded = trustNeeded;
        }

        boolean isSelf() {
            return targetTypeName.equals("self");
        }

        boolean isTypeName() {
            return targetTypeElement instanceof Perl6TypeName;
        }

        public String getTargetTypeName() {
            return targetTypeName;
        }

        public Object getTargetTypeElement() {
            return targetTypeElement;
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

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6MethodCall call = getElement();
        List<Perl6Symbol> method = getMethodsForType(call, getCallInfo(call), true);
        return method.size() != 0
                ? (method.get(0) == null ? null : method.get(0).getPsi())
                : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6MethodCall call = getElement();
        List<String> methods = getMethodsForType(call, getCallInfo(call), false);
        if (call.getCallOperator().equals("!"))
            methods = ContainerUtil.filter(methods, name -> name.startsWith("!"));
        return methods.toArray();
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

    private static List getMethodsForType(Perl6MethodCall call, CallInfo callInfo, boolean isSingle) {
        if (callInfo == null)
            return new ArrayList();
        if (callInfo.isSelf()) {
            Perl6PackageDecl enclosingPackage = PsiTreeUtil.getParentOfType(call, Perl6PackageDeclImpl.class);
            if (enclosingPackage != null)
                return getMethodsFromPsiType(callInfo, isSingle, enclosingPackage, true);
            return new ArrayList();
        }
        else {
            String name;
            if (callInfo.isTypeName()) {
                Perl6TypeName typeName = (Perl6TypeName)callInfo.getTargetTypeElement();
                name = typeName.getTypeName();
            }
            else {
                name = callInfo.getTargetTypeName();
            }
            return getMethodsByTypeName(call, callInfo, name, isSingle);
        }
    }

    @NotNull
    private static List getMethodsFromPsiType(CallInfo callinfo, boolean isSingle, Perl6PackageDecl enclosingPackage, boolean privatesVisible) {
        MOPSymbolsAllowed symbolsAllowed = new MOPSymbolsAllowed(privatesVisible, true, privatesVisible, false);
        if (isSingle) {
            Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(
                    callinfo.getMethodName(), Perl6SymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return Collections.singletonList(collector.getResult());
        }
        else {
            Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return ContainerUtil.map(collector.getVariants(), Perl6Symbol::getName);
        }
    }

    private static List getMethodsByTypeName(Perl6MethodCall call, CallInfo callInfo, String typeName, boolean isSingle) {
        Perl6Symbol type = call.resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, typeName);
        if (type != null) { // If we know that type, even as external
            PsiElement base = type.getPsi();
            Perl6PackageDecl decl = null;
            if (base instanceof Perl6PackageDecl) {
                decl = (Perl6PackageDecl)base;
            }
            else if (base instanceof Perl6Subset) {
                Perl6Subset subset = (Perl6Subset)base;
                // Get original type of subset
                decl = subset.getSubsetBaseType();
                String newName = subset.getSubsetBaseTypeName();
                typeName = newName != null ? newName : "Any";
            }
            else if (base instanceof Perl6Enum) {
                if (!isSingle) {
                    List<String> enumMethods = tryToCompleteExternalTypeMethods("Int", call, callInfo);
                    enumMethods.addAll(tryToCompleteExternalTypeMethods("Enumeration", call, callInfo));
                    return enumMethods;
                }
            }
            if (decl != null) {
                // Not external type
                return getMethodsFromPsiType(callInfo, isSingle, decl, callInfo.isTrustNeeded());
            }
            else {
                return isSingle ? Collections.EMPTY_LIST :
                       tryToCompleteExternalTypeMethods(typeName, call, callInfo);
            }
        } else {
            // We don't know that type, assume it is derived from Mu/Any. No privates or
            // attributes there, so simple.
            return isSingle ? Collections.EMPTY_LIST : MuAnyMethods(call, null);
        }
    }

    private static List<String> MuAnyMethods(Perl6PsiElement element, Perl6VariantsSymbolCollector collector) {
        if (collector == null)
            collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Any", element).methods())
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
        for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Mu", element).methods())
            collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
        return ContainerUtil.map(collector.getVariants(), Perl6Symbol::getName);
    }

    private static List<String> tryToCompleteExternalTypeMethods(String typeName, Perl6PsiElement element,
                                                                 CallInfo callInfo) {
        Perl6SingleResolutionSymbolCollector collector = new Perl6SingleResolutionSymbolCollector(typeName, Perl6SymbolKind.ExternalPackage);
        int baseOffset = element.getTextOffset();
        element = (Perl6PsiElement)element.getParent();
        outer:
        while (element != null) {
            for (PsiElement child : element.getChildren()) {
                if (child instanceof Perl6Statement) child = child.getFirstChild();
                if ((child instanceof Perl6UseStatement || child instanceof Perl6NeedStatement) &&
                    child.getTextOffset() < baseOffset) {
                    ((Perl6LexicalSymbolContributor)child).contributeLexicalSymbols(collector);
                    if (collector.isSatisfied()) break outer;
                }
            }
            if (element instanceof Perl6File) {
                ((Perl6File)element).contributeScopeSymbols(collector);
                break;
            }
            element = (Perl6PsiElement)element.getParent();
        }

        Perl6ExternalPackage type = (Perl6ExternalPackage)collector.getResult();
        if (type == null) return new ArrayList<>();
        // From external types we get "raw" method names, so should be prepended with `.`
        // While our local packages properly export already prepended named
        List<String> basicMethods = MuAnyMethods(element, null);
        basicMethods.addAll(ContainerUtil.map(type.methods(), s -> '.' + s));
        return basicMethods;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        PsiElement resolved = resolve();
        Perl6MethodCall call = myElement;
        if (resolved instanceof Perl6LongName && !call.getCallName().startsWith("!"))
            throw new IncorrectOperationException("Rename for non-private methods is not yet supported");
        return call.setName(newElementName);
    }
}
