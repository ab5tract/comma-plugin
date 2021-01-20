package edument.perl6idea.psi;

import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.impl.Perl6MethodCallImpl;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.psi.type.Perl6SelfType;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import edument.perl6idea.psi.type.Perl6Untyped;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edument.perl6idea.parsing.Perl6TokenTypes.SELF;

public class Perl6MethodReference extends PsiReferenceBase.Poly<Perl6MethodCall> {
    public Perl6MethodReference(Perl6MethodCallImpl call) {
        super(call, new TextRange(0, call.getCallName().length()), false);
    }

    static class CallInfo {
        @NotNull
        private final Perl6Type targetType;
        private final String methodName;
        private boolean trustNeeded = false;

        CallInfo(@NotNull Perl6Type targetType, String methodName) {
            this.targetType = targetType.dispatchType();
            this.methodName = methodName;
        }

        CallInfo(Perl6Type targetType, String methodName, boolean trustNeeded) {
            this(targetType, methodName);
            this.trustNeeded = trustNeeded;
        }

        boolean isSelf() {
            return targetType instanceof Perl6SelfType || (methodName.startsWith("!") && !trustNeeded);
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
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Perl6MethodCall call = getElement();
        Collection<LookupElement> method = getMethodsForType(call, getCallInfo(call), true);
        return method.stream()
                .map(s ->s.getPsiElement())
                .filter(p -> p != null)
                .map(p -> new PsiElementResolveResult(p))
                .toArray(ResolveResult[]::new);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6MethodCall call = getElement();
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
    private static CallInfo getCallInfo(Perl6MethodCall call) {
        // Check if the targetTypeName contains an explicitly qualified part.
        String name = call.getCallName();
        if (name.length() >= 5) { // Minimum case is !A::m
            boolean isPrivate = name.charAt(0) == '!';
            int lastSep = name.lastIndexOf("::");
            if (lastSep >= 0) {
                String typeName = name.substring(1, lastSep);
                String shortName = name.substring(0, 1) + name.substring(lastSep + 2);
                return new CallInfo(new Perl6UnresolvedType(typeName), shortName, isPrivate);
            }
        }

        PsiElement firstElement = call.findElementAt(0);
        if (firstElement != null && firstElement.getNode().getElementType() == SELF) {
            return new CallInfo(((Perl6PsiElement )firstElement).inferType(), name);
        }

        // Based on previous targetTypeElement decide what type methods we want
        PsiElement prev = call.getPrevSibling();

        if (prev == null) { // .foo
            Perl6Symbol symbol = call.resolveLexicalSymbol(Perl6SymbolKind.Variable, "$_");
            if (symbol != null) {
                PsiElement psi = symbol.getPsi();
                if (psi instanceof P6Topicalizer) {
                    return new CallInfo(((P6Topicalizer)psi).calculateTopicType(call), name);
                }
                else if (psi instanceof Perl6PsiElement) {
                    return new CallInfo(((Perl6PsiElement)psi).inferType(), name);
                }
            }
            return new CallInfo(Perl6Untyped.INSTANCE, name);
        }

        if (prev instanceof Perl6PsiElement) {
            Perl6PsiElement element = (Perl6PsiElement) prev;
            Perl6Type type = element.inferType();
            return new CallInfo(type, name);
        }
        else
            return null;
    }

    private static Collection<LookupElement> getMethodsForType(Perl6MethodCall call, CallInfo callInfo, boolean isSingle) {
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
    private static Collection<LookupElement> getMethodsFromPsiType(CallInfo callinfo,
                                                                   boolean isSingle,
                                                                   Perl6PackageDecl enclosingPackage,
                                                                   boolean privatesVisible) {
        MOPSymbolsAllowed symbolsAllowed = new MOPSymbolsAllowed(privatesVisible, privatesVisible, true, false);
        if (isSingle) {
            Perl6SingleResolutionSymbolCollector collector =
                new Perl6SingleResolutionSymbolCollector(callinfo.getMethodName(), Perl6SymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return ContainerUtil.map(collector.getResults(), s -> strikeoutDeprecated(LookupElementBuilder.create(s.getPsi()), s.getPsi()));
        }
        else {
            Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
            enclosingPackage.contributeMOPSymbols(collector, symbolsAllowed);
            return prioritizeResults(collector.getVariants());
        }
    }

    private static Collection<LookupElement> getMethodsByTypeName(Perl6MethodCall call, CallInfo callInfo, boolean isSingle) {
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

    public static Collection<LookupElement> MuAnyMethods(Perl6PsiElement element, @Nullable Perl6VariantsSymbolCollector collector) {
        if (collector == null)
            collector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);

        Perl6File coreSetting = Perl6SdkType.getInstance().getCoreSettingFile(element.getProject());
        MOPSymbolsAllowed symbolsAllowed = new MOPSymbolsAllowed(true, true, true, true);
        Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Any", symbolsAllowed);
        Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Mu", symbolsAllowed);
        return prioritizeResults(collector.getVariants());
    }

    private static Collection<LookupElement> tryToCompleteExternalTypeMethods(String typeName, Perl6PsiElement element) {
        Perl6SingleResolutionSymbolCollector externalPackageCollector =
            new Perl6SingleResolutionSymbolCollector(typeName, Perl6SymbolKind.TypeOrConstant);
        element.applyExternalSymbolCollector(externalPackageCollector);
        Perl6Symbol type = externalPackageCollector.getResult();
        if (type == null || !(type.getPsi() instanceof Perl6PackageDecl))
            return MuAnyMethods(element, null);

        Perl6VariantsSymbolCollector methodsCollector = new Perl6VariantsSymbolCollector(Perl6SymbolKind.Method);
        Perl6PackageDecl packageDecl = (Perl6PackageDecl)type.getPsi();
        packageDecl.contributeMOPSymbols(methodsCollector, new MOPSymbolsAllowed(true, true, true, true));
        return prioritizeResults(methodsCollector.getVariants());
    }

    @NotNull
    private static List<LookupElement> prioritizeResults(Collection<Perl6Symbol> symbols) {
        return ContainerUtil.map(symbols, s -> {
            LookupElementBuilder item = LookupElementBuilder.create(s.getPsi(), s.getName());
            if (s.getPsi() instanceof Perl6RoutineDecl) {
                item = item.withTypeText(((Perl6RoutineDecl)s.getPsi()).summarySignature());
                item = strikeoutDeprecated(item, s.getPsi());
            } else if (s.getPsi() instanceof Perl6VariableDecl) {
                Perl6VariableDecl variableDecl = (Perl6VariableDecl)s.getPsi();
                Perl6Type type = variableDecl.inferType();
                item = item.withTypeText("(attribute) " + type.getName());
            }
            return PrioritizedLookupElement.withPriority(item, s.getPriority());
        });
    }

    private static LookupElementBuilder strikeoutDeprecated(LookupElementBuilder item, PsiElement psi) {
        return psi instanceof Perl6Deprecatable && ((Perl6Deprecatable)psi).isDeprecated()
                ? item.strikeout()
               : item;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }
}
