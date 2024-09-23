package org.raku.comma;

import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.external.ExternalRakuPackageDecl;
import org.raku.comma.psi.external.RakuExternalPsiElement;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@InternalIgnoreDependencyViolation
public class RakuParameterInfoHandler implements ParameterInfoHandler<RakuCodeBlockCall, RakuRoutineDecl> {
    @Nullable
    @Override
    public RakuCodeBlockCall findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        int offset = context.getOffset();
        PsiElement element = context.getFile()
                                    .findElementAt(offset == 0
                                                   ? 0
                                                   : offset - 1);
        return PsiTreeUtil.getParentOfType(element, RakuCodeBlockCall.class, false);
    }

    @Override
    public void showParameterInfo(@NotNull RakuCodeBlockCall element, @NotNull CreateParameterInfoContext context) {
        PsiReference ref;

        if (element instanceof RakuMethodCall) {
            ref = element.getReference();
        } else {
            RakuSubCallName name = PsiTreeUtil.findChildOfType(element, RakuSubCallName.class);
            if (name == null) return;
            ref = name.getReference();
        }

        if (! (ref instanceof PsiPolyVariantReference)) return;
        Deque<RakuRoutineDecl> decls = new ArrayDeque<>();
        ResolveResult[] resolvedDecls = ((PsiPolyVariantReference) ref).multiResolve(false);
        for (ResolveResult decl : resolvedDecls) {
            PsiElement declNode = decl.getElement();
            if (declNode instanceof RakuRoutineDecl) {
                decls.add((RakuRoutineDecl) declNode);
            }
        }
        // If it is .new constructor, check if there is an already present constructor...
        calculateSyntheticConstructor(element, decls);
        if (decls.isEmpty()) return;
        context.setItemsToShow(ArrayUtil.toObjectArray(decls));
        context.showHint(element, element.getTextOffset() + 1, this);
    }

    private static void calculateSyntheticConstructor(@NotNull RakuCodeBlockCall element, Deque<RakuRoutineDecl> decls) {
        if (!(element instanceof RakuMethodCall) || !element.getCallName().equals(".new")) {
            return;
        }
        PsiElement typeToResolve = element.getWholeCallNode().getFirstChild();
        if (!(typeToResolve instanceof RakuTypeName) || typeToResolve.getReference() == null) {
            return;
        }
        PsiElement resolvedType = typeToResolve.getReference().resolve();
        if (!(resolvedType instanceof RakuPackageDecl)) {
            return;
        }
        // Collect variables and methods.
        // Methods - maybe we have a written `.new` there, so no synthetic is needed
        // Public attributes to create a synthetic signature
        RakuVariantsSymbolCollector collector = new RakuVariantsSymbolCollector(
            RakuSymbolKind.Method, RakuSymbolKind.Variable
        );
        ((RakuPackageDecl) resolvedType).contributeMOPSymbols(collector,
                                                              new MOPSymbolsAllowed(false,
                                                                                    false,
                                                                                    false,
                                                                                    false));
        List<String> attributes = new ArrayList<>();
        for (RakuSymbol symbol : collector.getVariants()) {
            if (symbol.getKind() == RakuSymbolKind.Variable) {
                if (symbol.getPsi() instanceof RakuVariableDecl) {
                    RakuType type = ((RakuVariableDecl) symbol.getPsi()).inferType();
                    attributes.add(type.getName() + " " + convertAttributeIntoNamed(symbol.getName()));
                }
            } else if (symbol.getKind() == RakuSymbolKind.Method && symbol.getName().equals(".new")) {
                PsiElement method = symbol.getPsi();
                if (method instanceof RakuExternalPsiElement &&
                        method.getParent() instanceof ExternalRakuPackageDecl &&
                        ((ExternalRakuPackageDecl) method.getParent()).getName().equals("Mu"))
                {
                    continue;
                }
                return;
            }
        }
        Collections.reverse(attributes);
        RakuRoutineDecl syntheticDeclaration =
                RakuElementFactory.createRoutineDeclaration(element.getProject(), "dummy", attributes);
        decls.clear();
        decls.addFirst(syntheticDeclaration);
    }

    private static String convertAttributeIntoNamed(String name) {
        char sigil = RakuVariable.getSigil(name);
        return String.format(":%s%s", sigil, name.substring(2));
    }

    @Nullable
    @Override
    public RakuCodeBlockCall findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiElement owner = context.getParameterOwner();
        return owner.getTextRange().contains(context.getOffset() - 1)
               ? (RakuCodeBlockCall) owner
               : null;
    }

    @Override
    public void updateParameterInfo(@NotNull final RakuCodeBlockCall parameterOwner, @NotNull UpdateParameterInfoContext context) {
    }

    @Override
    public void updateUI(RakuRoutineDecl parameterType, @NotNull ParameterInfoUIContext context) {
        PsiElement element = context.getParameterOwner();
        boolean areWeInBlock = PsiTreeUtil.getParentOfType(element,
                                                           RakuBlockOrHash.class,
                                                           RakuSubCall.class,
                                                           RakuMethodCall.class) instanceof RakuBlockOrHash;
        if (areWeInBlock) {
            return;
        }

        // Obtain signature and parameters
        RakuSignature signatureNode = parameterType.getSignatureNode();
        if (signatureNode == null) return;
        RakuParameter[] parameters = signatureNode.getParameters();

        // Obtain call and arguments
        RakuCodeBlockCall owner = (RakuCodeBlockCall) context.getParameterOwner();
        PsiElement[] arguments = owner.getCallArguments();

        // Compare
        RakuSignature.SignatureCompareResult compare = signatureNode.acceptsArguments(arguments,
                                                                                      false,
                                                                                      owner instanceof RakuMethodCall);

        StringJoiner signatureTextBuilder = new StringJoiner(", ");
        int startOffset = 0;
        int endOffset = 0;

        String text = context.getParameterOwner().getText();
        boolean shouldNotJump = !text.endsWith(",");
        int nextParameter = compare.getNextParameterIndex();
        if (shouldNotJump) {
            nextParameter = nextParameter == 0
                                ? 0
                                : nextParameter - 1;
        }

        for (int i = 0, length = parameters.length; i < length; i++) {
            RakuParameter param = parameters[i];
            String paramText = param.getText();
            if (i == nextParameter && compare.isAccepted()) {
                startOffset = signatureTextBuilder.length();
                if (startOffset != 0) {
                    startOffset += 2;
                }
            }
            signatureTextBuilder.add(paramText);
            if (i == nextParameter && compare.isAccepted()) {
                endOffset = signatureTextBuilder.length();
            }
        }

        context.setUIComponentEnabled(compare.isAccepted());
        context.setupUIComponentPresentation(signatureTextBuilder.toString().trim(),
                                             startOffset,
                                             endOffset,
                                             !compare.isAccepted(),
                                             false,
                                             false,
                                             context.getDefaultParameterColor());
    }
}
