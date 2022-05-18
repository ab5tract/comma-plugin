package edument.perl6idea.cro.template;

import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.psi.CroTemplateCall;
import edument.perl6idea.cro.template.psi.CroTemplateParameter;
import edument.perl6idea.cro.template.psi.CroTemplateSignature;
import edument.perl6idea.cro.template.psi.CroTemplateSub;
import edument.perl6idea.psi.Perl6Signature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.StringJoiner;

@InternalIgnoreDependencyViolation
public class CroTemplateParameterInfoHandler implements ParameterInfoHandler<CroTemplateCall, CroTemplateSub> {
    @Nullable
    @Override
    public CroTemplateCall findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        int offset = context.getOffset();
        PsiElement element = context.getFile().findElementAt(offset == 0 ? 0 : offset - 1);
        return PsiTreeUtil.getParentOfType(element, CroTemplateCall.class, false);
    }

    @Override
    public void showParameterInfo(@NotNull CroTemplateCall element, @NotNull CreateParameterInfoContext context) {
        PsiReference ref = element.getReference();
        assert ref != null;
        CroTemplateSub declaration = (CroTemplateSub)ref.resolve();
        if (declaration == null) {
            return;
        }
        context.setItemsToShow(new CroTemplateSub[]{declaration});
        context.showHint(element, element.getTextOffset() + 1, this);
    }

    @Nullable
    @Override
    public CroTemplateCall findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiElement owner = context.getParameterOwner();
        return owner.getTextRange().contains(context.getOffset() - 1) ? (CroTemplateCall)owner : null;
    }

    @Override
    public void updateParameterInfo(@NotNull CroTemplateCall call, @NotNull UpdateParameterInfoContext context) {}

    @Override
    public void updateUI(CroTemplateSub decl, @NotNull ParameterInfoUIContext context) {
        // Obtain params
        CroTemplateSignature signature = PsiTreeUtil.findChildOfType(decl, CroTemplateSignature.class);
        if (signature == null) return;
        List<CroTemplateParameter> params = PsiTreeUtil.getChildrenOfTypeAsList(signature, CroTemplateParameter.class);
        // Obtain args
        CroTemplateCall call = (CroTemplateCall)context.getParameterOwner();
        PsiElement[] arguments = call.getCallArguments();

        // Compare
        Perl6Signature.SignatureCompareResult compare = signature.acceptsArguments(arguments, false);

        StringJoiner signatureTextBuilder = new StringJoiner(", ");
        int startOffset = 0;
        int endOffset = 0;

        int nextParameeter = compare.getNextParameterIndex();
        for (int i = 0, length = params.size(); i < length; i++) {
            CroTemplateParameter param = params.get(i);
            String paramText = param.getText();
            if (i == nextParameeter && compare.isAccepted()) {
                startOffset = signatureTextBuilder.length();
                if (startOffset != 0) startOffset += 2;
            }
            signatureTextBuilder.add(paramText);
            if (i == nextParameeter && compare.isAccepted()) endOffset = signatureTextBuilder.length();
        }

        context.setUIComponentEnabled(compare.isAccepted());
        context.setupUIComponentPresentation(
            signatureTextBuilder.toString(), startOffset, endOffset, false, false, false,
            context.getDefaultParameterColor()
        );
    }
}
