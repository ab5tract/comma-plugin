package org.raku.comma.psi;

import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.impl.RakuMethodCallImpl;
import org.raku.comma.psi.impl.RakuPackageDeclImpl;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.type.*;
import org.raku.comma.sdk.RakuSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.raku.comma.parsing.RakuTokenTypes.SELF;

public class RakuMethodReference extends PsiReferenceBase.Poly<RakuMethodCall> {
    public RakuMethodReference(RakuMethodCallImpl call) {
        super(call, new TextRange(0, call.getCallName().length()), false);
    }

    static class CallInfo {
        @NotNull
        private final RakuType targetType;
        private final String methodName;
        private boolean trustNeeded = false;

        CallInfo(@NotNull RakuType targetType, String methodName) {
            this.targetType = targetType.dispatchType();
            this.methodName = methodName;
        }

        CallInfo(RakuType targetType, String methodName, boolean trustNeeded) {
            this(targetType, methodName);
            this.trustNeeded = trustNeeded;
        }

        boolean isSelf() {
            return targetType instanceof RakuSelfType || (methodName.startsWith("!") && !trustNeeded);
        }

        public String getTargetTypeName() {
            return targetType.getName();
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

        public @Nullable RakuPackageDecl getResolvedPackage() {
            if (targetType instanceof RakuResolvedType) {
                RakuPsiElement resolution = ((RakuResolvedType)targetType).getResolution();
                if (resolution instanceof RakuPackageDecl)
                    return (RakuPackageDecl)resolution;
            }
            return null;
        }
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RakuMethodCall call = getElement();
        Collection<LookupElement> method = getMethodsForType(call, getCallInfo(call), true);
        return method.stream()
                .map(s ->s.getPsiElement())
                .filter(p -> p != null)
                .map(p -> new PsiElementResolveResult(p))
                .toArray(ResolveResult[]::new);
    }

    @Override
    public Object @NotNull [] getVariants() {
        RakuMethodCall call = getElement();
        Collection<LookupElement> methods = getMethodsForType(call, getCallInfo(call), false);
        if (call.getCallOperator().equals("!")) {
            return methods.stream().filter(le -> le.getLookupString().startsWith("!")).toArray();
        } else {
            return methods.toArray();
        }
    }

    /**
     * Calculates the type that we are calling the method on.
     */
    private static CallInfo getCallInfo(RakuMethodCall call) {
        // Check if the targetTypeName contains an explicitly qualified part.
        String name = call.getCallName();
        if (name.length() >= 5) { // Minimum case is !A::m
            boolean isPrivate = name.charAt(0) == '!';
            int lastSep = name.lastIndexOf("::");
            if (lastSep >= 0) {
                String typeName = name.substring(1, lastSep);
                String shortName = name.charAt(0) + name.substring(lastSep + 2);
                return new CallInfo(new RakuUnresolvedType(typeName), shortName, isPrivate);
            }
        }

        PsiElement firstElement = call.findElementAt(0);
        if (firstElement != null && firstElement.getNode().getElementType() == SELF) {
            return new CallInfo(((RakuPsiElement)firstElement).inferType(), name);
        }

        // Based on previous targetTypeElement decide what type methods we want
        PsiElement prev = call.getPrevSibling();

        if (prev == null) { // .foo
            RakuSymbol symbol = call.resolveLexicalSymbol(RakuSymbolKind.Variable, "$_");
            if (symbol != null) {
                PsiElement psi = symbol.getPsi();
                if (psi instanceof RakuTopicalizer) {
                    return new CallInfo(((RakuTopicalizer)psi).calculateTopicType(call), name);
                }
                else if (psi instanceof RakuPsiElement) {
                    return new CallInfo(((RakuPsiElement)psi).inferType(), name);
                }
            }
            return new CallInfo(RakuUntyped.INSTANCE, name);
        }

        if (prev instanceof RakuPsiElement element) {
            RakuType type = element.inferType();
            return new CallInfo(type, name);
        }
        else
            return null;
    }

    private static Collection<LookupElement> getMethodsForType(RakuMethodCall call, CallInfo callInfo, boolean isSingle) {
        if (callInfo == null)
            return Collections.emptyList();
        if (callInfo.isSelf()) {
            RakuPackageDecl enclosingPackage = PsiTreeUtil.getParentOfType(call, RakuPackageDeclImpl.class);
            if (enclosingPackage != null)
                return getMethodsFromPsiType(callInfo, isSingle, enclosingPackage, true);
            return Collections.emptyList();
        }
        else {
            RakuPackageDecl packageDecl = callInfo.getResolvedPackage();
            return packageDecl != null
                ? getMethodsFromPsiType(callInfo, isSingle, packageDecl, false)
                : getMethodsByTypeName(call, callInfo, isSingle);
        }
    }

    @NotNull
    private static Collection<LookupElement> getMethodsFromPsiType(CallInfo callinfo,
                                                                   boolean isSingle,
                                                                   RakuPackageDecl enclosingPackage,
                                                                   boolean privatesVisible) {
        MOPSymbolsAllowed symbolsAllowed = new MOPSymbolsAllowed(privatesVisible, privatesVisible, true, false);
        if (isSingle) {
            RakuSingleResolutionSymbolCollector collector =
                new RakuSingleResolutionSymbolCollector(callinfo.getMethodName(), RakuSymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return ContainerUtil.map(collector.getResults(), s -> strikeoutDeprecated(LookupElementBuilder.create(s.getPsi()), s.getPsi()));
        }
        else {
            RakuVariantsSymbolCollector collector = new RakuVariantsSymbolCollector(RakuSymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return prioritizeResults(collector.getVariants());
        }
    }

    private static Collection<LookupElement> getMethodsByTypeName(RakuMethodCall call, CallInfo callInfo, boolean isSingle) {
        String typeName = callInfo.getTargetTypeName();
        RakuSymbol type = call.resolveLexicalSymbol(RakuSymbolKind.TypeOrConstant, typeName);
        if (type != null) { // If we know that type, even as an external one
            PsiElement psiDeclaration = type.getPsi();
            RakuPackageDecl decl;
            if (psiDeclaration instanceof RakuPackageDecl) {
                return getMethodsFromPsiType(callInfo, isSingle, (RakuPackageDecl)psiDeclaration, callInfo.isTrustNeeded());
            } else if (psiDeclaration instanceof RakuSubset subset) {
                // Get original type of subset
                decl = subset.getSubsetBaseType();
                if (decl == null) {
                    return tryToCompleteExternalTypeMethods("Any", call);
                } else {
                    return getMethodsFromPsiType(callInfo, isSingle, decl, callInfo.isTrustNeeded());
                }
            } else if (psiDeclaration instanceof RakuEnum) {
                if (!isSingle) {
                    Collection<LookupElement> enumerationMethods = tryToCompleteExternalTypeMethods("Enumeration", call);
                    Collection<LookupElement> intMethods = tryToCompleteExternalTypeMethods("Int", call);
                    return Stream.concat(intMethods.stream(), enumerationMethods.stream()).collect(Collectors.toList());
                }
            }
            // We never reach this
            return new ArrayList<>();
        } else {
            // We don't know that type, assume it is derived from Mu/Any. No privates or
            // attributes there, so simple
            return isSingle ? Collections.emptyList() : MuAnyMethods(call, null);
        }
    }

    public static Collection<LookupElement> MuAnyMethods(RakuPsiElement element, @Nullable RakuVariantsSymbolCollector collector) {
        if (collector == null)
            collector = new RakuVariantsSymbolCollector(RakuSymbolKind.Method);

        RakuFile coreSetting = RakuSdkType.getInstance().getCoreSettingFile(element.getProject());
        MOPSymbolsAllowed symbolsAllowed = new MOPSymbolsAllowed(true, true, true, true);
        RakuSdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Any", symbolsAllowed);
        RakuSdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Mu", symbolsAllowed);
        return prioritizeResults(collector.getVariants());
    }

    private static Collection<LookupElement> tryToCompleteExternalTypeMethods(String typeName, RakuPsiElement element) {
        RakuSingleResolutionSymbolCollector externalPackageCollector =
            new RakuSingleResolutionSymbolCollector(typeName, RakuSymbolKind.TypeOrConstant);
        element.applyExternalSymbolCollector(externalPackageCollector);
        RakuSymbol type = externalPackageCollector.getResult();
        if (type == null || !(type.getPsi() instanceof RakuPackageDecl packageDecl))
            return MuAnyMethods(element, null);

        RakuVariantsSymbolCollector methodsCollector = new RakuVariantsSymbolCollector(RakuSymbolKind.Method);
        packageDecl.contributeMOPSymbols(methodsCollector, new MOPSymbolsAllowed(true, true, true, true));
        return prioritizeResults(methodsCollector.getVariants());
    }

    @NotNull
    private static List<LookupElement> prioritizeResults(Collection<RakuSymbol> symbols) {
        return ContainerUtil.map(symbols, s -> {
            LookupElementBuilder item = LookupElementBuilder.create(s.getPsi(), s.getName());
            if (s.getPsi() instanceof RakuRoutineDecl) {
                item = item.withTypeText(((RakuRoutineDecl)s.getPsi()).summarySignature());
                item = strikeoutDeprecated(item, s.getPsi());
            } else if (s.getPsi() instanceof RakuVariableDecl variableDecl) {
                RakuType type = variableDecl.inferType();
                item = item.withTypeText("(attribute) " + type.getName());
            }
            return PrioritizedLookupElement.withPriority(item, s.getPriority());
        });
    }

    private static LookupElementBuilder strikeoutDeprecated(LookupElementBuilder item, PsiElement psi) {
        return psi instanceof RakuDeprecatable && ((RakuDeprecatable)psi).isDeprecated()
                ? item.strikeout()
               : item;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }
}
