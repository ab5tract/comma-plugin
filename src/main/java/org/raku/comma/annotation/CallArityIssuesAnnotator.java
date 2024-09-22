package org.raku.comma.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import org.raku.comma.psi.*;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.utils.CommaProjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallArityIssuesAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuCodeBlockCall)) return;
        if (CommaProjectUtil.isRakudoCoreProject(element)) return;

        PsiElement refElement = element instanceof RakuSubCall
                                ? element.getFirstChild()
                                : element;
        if (CommaProjectUtil.isRakudoCoreProject(refElement)) return;

        PsiElement[] args = ((RakuCodeBlockCall) element).getCallArguments();

        if (element instanceof RakuMethodCall) {
            PsiElement wholeNode = ((RakuMethodCall) element).getWholeCallNode();
            if (wholeNode instanceof RakuPostfixApplication) {
                PsiElement operand = ((RakuPostfixApplication) wholeNode).getOperand();
                if (operand instanceof RakuPsiElement) {
                    @NotNull RakuType type = ((RakuPsiElement) operand).inferType();
                    if (type instanceof RakuUntyped) {
                        return;
                    }
                }
            }
        }

        // If there is a `|` in a call, we are not smart enough to show anything worthy for this case
        for (PsiElement arg : args) {
            if (arg instanceof RakuPrefixApplication) {
                PsiElement prefix = ((RakuPrefixApplication) arg).getPrefix();
                if (prefix != null && prefix.getText().startsWith("|")) {
                    return;
                }
            }
        }

        PsiReference ref = refElement.getReference();
        if (!(ref instanceof PsiPolyVariantReference)) return;

        ResolveResult[] defs = ((PsiPolyVariantReference) ref).multiResolve(false);
        if (defs.length == 0) return;

        List<AnnotationBuilderWrap> annotations = new ArrayList<>();

        MULTI_LOOP:
        for (ResolveResult def : defs) {
            if (!(def.getElement() instanceof RakuRoutineDecl)) return;

            RakuSignature signature = ((RakuRoutineDecl) def.getElement()).getSignatureNode();
            if (signature == null) {
                signature = RakuElementFactory.createRoutineSignature(element.getProject(), new ArrayList<>());
            }
            RakuSignature.SignatureCompareResult result = signature.acceptsArguments(args, true, element instanceof RakuMethodCall);
            if (result.isAccepted()) {
                for (int i = 0; i < annotations.size(); i++) {
                    annotations.set(0, null);
                }
                return;
            } else {
                for (int i = 0; i <= args.length; i++) {
                    if (CommaProjectUtil.isRakudoCoreProject(args[i])) return;

                    RakuSignature.MatchFailureReason reason = result.getArgumentFailureReason(i);
                    if (reason == null) continue;
                    TextRange argToHighlight = i == 0 && args.length == 0
                                                   ? refElement.getTextRange()
                                                   : i < args.length
                                                         ? args[i].getTextRange()
                                                         : new TextRange(args[0].getTextRange().getStartOffset(),
                                                                         args[args.length - 1].getTextRange().getEndOffset());
                    switch (reason) {
                        case TOO_MANY_ARGS: {
                            annotations.add(new AnnotationBuilderWrap(signature, argToHighlight, "Too many positional arguments"));
                            continue MULTI_LOOP;
                        }
                        case SURPLUS_NAMED: {
                            annotations.add(new AnnotationBuilderWrap(signature, argToHighlight, "No such named parameter in signature"));
                            continue MULTI_LOOP;
                        }
                        case NOT_ENOUGH_ARGS: {
                            annotations.add(new AnnotationBuilderWrap(signature, argToHighlight, "Not enough positional arguments"));
                            continue MULTI_LOOP;
                        }
                        case MISSING_REQUIRED_NAMED: {
                            annotations.add(new AnnotationBuilderWrap(signature, argToHighlight, "This call misses a required named argument: " + reason.name));
                            continue MULTI_LOOP;
                        }
                        default: {
                            return;
                        }
                    }
                }
            }
        }

        if (defs.length == 1) {
            for (AnnotationBuilderWrap wrapper : annotations) {
                holder.newAnnotation(HighlightSeverity.ERROR, wrapper.text).range(wrapper.range).create();
            }
        } else {
            // Multi...
            String message = String.format("No multi candidates match (%s)",
                                           annotations.stream()
                                                      .map(an -> String.format("%s: %s", an.signature.summary(RakuUntyped.INSTANCE), an.text))
                                                      .collect(Collectors.joining(", ")));
            holder.newAnnotation(HighlightSeverity.ERROR, message)
                  .range(refElement)
                  .create();
        }
    }

    // Because AnnotationBuilder once created _MUST_
    // be used with a `create()` call, and we iterate
    // over candidates to see if one of them fits to these arguments,
    // if we found a fitting candidate we bail out, where
    // an exception about AnnotationBuilder can bite us.
    // Use a wrapper to avoid this...
    private record AnnotationBuilderWrap(RakuSignature signature, TextRange range, String text) {
    }
}
