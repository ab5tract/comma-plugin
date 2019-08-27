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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
        List<Perl6RoutineDecl> decls = new ArrayList<>();
        ResolveResult[] resolvedDecls = ((PsiPolyVariantReference)ref).multiResolve(false);
        for (ResolveResult decl : resolvedDecls) {
            PsiElement declNode = decl.getElement();
            if (declNode instanceof Perl6RoutineDecl) {
                decls.add((Perl6RoutineDecl)declNode);
            }
        }
        if (decls.size() == 0) return;
        context.setItemsToShow(ArrayUtil.toObjectArray(decls));
        context.showHint(element, element.getTextOffset() + 1, this);
    }

    @Nullable
    @Override
    public P6CodeBlockCall findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiElement owner = context.getParameterOwner();
        return owner.getTextRange().contains(context.getOffset() - 1) ? (P6CodeBlockCall)owner : null;
    }

    @Override
    public void updateParameterInfo(@NotNull final P6CodeBlockCall parameterOwner, @NotNull UpdateParameterInfoContext context) {
    }

    @Override
    public void updateUI(Perl6RoutineDecl parameterType, @NotNull ParameterInfoUIContext context) {
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
