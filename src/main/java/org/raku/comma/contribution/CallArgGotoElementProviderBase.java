package org.raku.comma.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.psi.RakuStrLiteral;
import org.raku.comma.psi.RakuSubCall;
import org.raku.comma.psi.RakuSubCallName;
import org.raku.comma.psi.external.ExternalRakuFile;
import org.raku.comma.psi.impl.RakuSubCallImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract public class CallArgGotoElementProviderBase extends GotoRelatedProvider {
    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof RakuStrLiteral))
            return Collections.emptyList();
        psiElement = psiElement.getParent();

        RakuSubCall call = PsiTreeUtil.getParentOfType(psiElement, RakuSubCall.class);
        if (call == null || !Objects.equals(getCallName(), call.getCallName()))
            return Collections.emptyList();

        @Nullable RakuSubCallName name = ((RakuSubCallImpl)call).getSubCallNameNode();
        @Nullable PsiReference ref = name == null ? null : name.getReference();
        if (name == null || !(ref instanceof PsiReferenceBase.Poly))
            return Collections.emptyList();

        ResolveResult @NotNull [] realRoutine = ((PsiReferenceBase.Poly<?>)ref).multiResolve(true);
        for (ResolveResult result : realRoutine) {
            if (result.getElement() != null && result.getElement().getParent() instanceof ExternalRakuFile &&
                ((ExternalRakuFile)result.getElement().getParent()).getName().startsWith("Cro::WebApp::Template")) {
                return getFiles((RakuStrLiteral)psiElement);
            }
        }
        return Collections.emptyList();
    }

    @NotNull
    protected List<String> calculatePathPieces(@NotNull RakuStrLiteral psiElement) {
        // We need to gather all available `template-location` calls in current `route` call if any
        PsiElement routeCall = psiElement;
        while (routeCall != null) {
            routeCall = PsiTreeUtil.getParentOfType(routeCall, RakuSubCall.class);
            if (routeCall != null && ((RakuSubCall)routeCall).getCallName().equals("route")) {
                break;
            }
        }
        List<String> pathPieces = new ArrayList<>();

        if (routeCall != null) {
            List<RakuSubCall> locationCalls = ContainerUtil.filter(
              PsiTreeUtil.findChildrenOfType(routeCall, RakuSubCall.class), (call) -> call.getCallName().equals("template-location"));
            for (RakuSubCall locationCall : locationCalls) {
                PsiElement[] args = locationCall.getCallArguments();
                if (args.length != 1 || !(args[0] instanceof RakuStrLiteral))
                    continue;
                pathPieces.add(((RakuStrLiteral)args[0]).getStringText());
            }
        }
        pathPieces.add(".");
        return pathPieces;
    }

    protected abstract List<? extends GotoRelatedItem> getFiles(RakuStrLiteral element);

    @NotNull
    abstract protected String getCallName();
}
