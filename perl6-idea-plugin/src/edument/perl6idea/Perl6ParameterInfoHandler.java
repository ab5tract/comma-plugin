package edument.perl6idea;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.external.ExternalPerl6PackageDecl;
import edument.perl6idea.psi.external.Perl6ExternalPsiElement;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Perl6ParameterInfoHandler implements ParameterInfoHandler<P6CodeBlockCall, Perl6RoutineDecl> {
    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    @Nullable
    @Override
    public P6CodeBlockCall findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        int offset = context.getOffset();
        PsiElement element = context.getFile().findElementAt(offset == 0 ? 0 : offset - 1);
        return PsiTreeUtil.getParentOfType(element, P6CodeBlockCall.class, false);
    }

    @Override
    public void showParameterInfo(@NotNull P6CodeBlockCall element, @NotNull CreateParameterInfoContext context) {
        PsiReference ref;

        if (element instanceof Perl6MethodCall) {
            ref = element.getReference();
        } else {
            Perl6SubCallName name = PsiTreeUtil.findChildOfType(element, Perl6SubCallName.class);
            if (name == null) return;
            ref = name.getReference();
        }

        if (!(ref instanceof PsiPolyVariantReference)) return;
        Deque<Perl6RoutineDecl> decls = new ArrayDeque<>();
        ResolveResult[] resolvedDecls = ((PsiPolyVariantReference)ref).multiResolve(false);
        for (ResolveResult decl : resolvedDecls) {
            PsiElement declNode = decl.getElement();
            if (declNode instanceof Perl6RoutineDecl) {
                decls.add((Perl6RoutineDecl)declNode);
            }
        }
        // If it is .new constructor, check if there is an already present constructor...
        calculateSyntheticConstructor(element, decls);
        if (decls.size() == 0) return;
        context.setItemsToShow(ArrayUtil.toObjectArray(decls));
        context.showHint(element, element.getTextOffset() + 1, this);
    }

    private static void calculateSyntheticConstructor(@NotNull P6CodeBlockCall element, Deque<Perl6RoutineDecl> decls) {
        if (!(element instanceof Perl6MethodCall) || !element.getCallName().equals(".new"))
            return;
        PsiElement typeToResolve = element.getWholeCallNode().getFirstChild();
        if (!(typeToResolve instanceof Perl6TypeName)|| typeToResolve.getReference() == null)
            return;
        PsiElement resolvedType = typeToResolve.getReference().resolve();
        if (!(resolvedType instanceof Perl6PackageDecl))
            return;
        // Collect variables and methods.
        // Methods - maybe we have a written `.new` there, so no synthetic is needed
        // Public attributes to create a synthetic signature
        Perl6VariantsSymbolCollector collector = new Perl6VariantsSymbolCollector(
            Perl6SymbolKind.Method, Perl6SymbolKind.Variable
        );
        ((Perl6PackageDecl)resolvedType).contributeMOPSymbols(
            collector, new MOPSymbolsAllowed(false, false, false, false));
        List<String> attributes = new ArrayList<>();
        for (Perl6Symbol symbol : collector.getVariants()) {
            if (symbol.getKind() == Perl6SymbolKind.Variable) {
                if (symbol.getPsi() instanceof Perl6VariableDecl) {
                    String type = ((Perl6VariableDecl) symbol.getPsi()).inferType();
                    attributes.add(type + " " + convertAttributeIntoNamed(symbol.getName()));
                }
            }
            else if (symbol.getKind() == Perl6SymbolKind.Method && symbol.getName().equals(".new")) {
                PsiElement method = symbol.getPsi();
                if (method instanceof Perl6ExternalPsiElement &&
                    method.getParent() instanceof ExternalPerl6PackageDecl &&
                    ((ExternalPerl6PackageDecl)method.getParent()).getName().equals("Mu"))
                    continue;
                return;
            }
        }
        Collections.reverse(attributes);
        Perl6RoutineDecl syntheticDeclaration =
            Perl6ElementFactory.createRoutineDeclaration(element.getProject(), "dummy", attributes);
        decls.clear();
        decls.addFirst(syntheticDeclaration);
    }

    private static String convertAttributeIntoNamed(String name) {
        char sigil = Perl6Variable.getSigil(name);
        return String.format(":%s%s", sigil, name.substring(2));
    }

    @Nullable
    @Override
    public P6CodeBlockCall findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiElement owner = context.getParameterOwner();
        return owner.getTextRange().contains(context.getOffset() - 1) ? (P6CodeBlockCall)owner : null;
    }

    @Override
    public void updateParameterInfo(@NotNull final P6CodeBlockCall parameterOwner, @NotNull UpdateParameterInfoContext context) {}

    @Override
    public void updateUI(Perl6RoutineDecl parameterType, @NotNull ParameterInfoUIContext context) {
        PsiElement element = context.getParameterOwner();
        boolean areWeInBlock = PsiTreeUtil.getParentOfType(element, Perl6BlockOrHash.class, Perl6SubCall.class, Perl6MethodCall.class) instanceof Perl6BlockOrHash;
        if (areWeInBlock)
            return;

        // Obtain signature and parameters
        Perl6Signature signatureNode = parameterType.getSignatureNode();
        if (signatureNode == null) return;
        Perl6Parameter[] parameters = signatureNode.getParameters();

        // Obtain call and arguments
        P6CodeBlockCall owner = (P6CodeBlockCall)context.getParameterOwner();
        PsiElement[] arguments = owner.getCallArguments();

        // Compare
        Perl6Signature.SignatureCompareResult compare = signatureNode.acceptsArguments(arguments, false);

        StringJoiner signatureTextBuilder = new StringJoiner(", ");
        int startOffset = 0;
        int endOffset = 0;

        String text = context.getParameterOwner().getText();
        boolean shouldNotJump = !text.endsWith(",");
        int nextParameter = compare.getNextParameterIndex();
        if (shouldNotJump)
            nextParameter = nextParameter == 0 ? 0 : nextParameter - 1;

        for (int i = 0, length = parameters.length; i < length; i++) {
            Perl6Parameter param = parameters[i];
            String paramText = param.getText();
            if (i == nextParameter && compare.isAccepted()) {
                startOffset = signatureTextBuilder.length();
                if (startOffset != 0) startOffset += 2;
            }
            signatureTextBuilder.add(paramText);
            if (i == nextParameter && compare.isAccepted()) endOffset = signatureTextBuilder.length();
        }

        context.setUIComponentEnabled(compare.isAccepted());
        context.setupUIComponentPresentation(signatureTextBuilder.toString().trim(), startOffset, endOffset, !compare.isAccepted(), false, false,
                                             context.getDefaultParameterColor());
    }
}
