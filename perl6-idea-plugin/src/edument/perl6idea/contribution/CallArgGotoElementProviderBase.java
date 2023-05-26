package edument.perl6idea.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6StrLiteral;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.external.ExternalPerl6File;
import edument.perl6idea.psi.impl.Perl6SubCallImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract public class CallArgGotoElementProviderBase extends GotoRelatedProvider {
    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof Perl6StrLiteral))
            return Collections.emptyList();
        psiElement = psiElement.getParent();

        Perl6SubCall call = PsiTreeUtil.getParentOfType(psiElement, Perl6SubCall.class);
        if (call == null || !Objects.equals(getCallName(), call.getCallName()))
            return Collections.emptyList();

        @Nullable Perl6SubCallName name = ((Perl6SubCallImpl)call).getSubCallNameNode();
        @Nullable PsiReference ref = name == null ? null : name.getReference();
        if (name == null || !(ref instanceof PsiReferenceBase.Poly))
            return Collections.emptyList();

        ResolveResult @NotNull [] realRoutine = ((PsiReferenceBase.Poly<?>)ref).multiResolve(true);
        for (ResolveResult result : realRoutine) {
            if (result.getElement() != null && result.getElement().getParent() instanceof ExternalPerl6File &&
                ((ExternalPerl6File)result.getElement().getParent()).getName().startsWith("Cro::WebApp::Template")) {
                return getFiles((Perl6StrLiteral)psiElement);
            }
        }
        return Collections.emptyList();
    }

    @NotNull
    protected List<String> calculatePathPieces(@NotNull Perl6StrLiteral psiElement) {
        // We need to gather all available `template-location` calls in current `route` call if any
        PsiElement routeCall = psiElement;
        while (routeCall != null) {
            routeCall = PsiTreeUtil.getParentOfType(routeCall, Perl6SubCall.class);
            if (routeCall != null && ((Perl6SubCall)routeCall).getCallName().equals("route")) {
                break;
            }
        }
        List<String> pathPieces = new ArrayList<>();

        if (routeCall != null) {
            List<Perl6SubCall> locationCalls = ContainerUtil.filter(
                PsiTreeUtil.findChildrenOfType(routeCall, Perl6SubCall.class), (call) -> call.getCallName().equals("template-location"));
            for (Perl6SubCall locationCall : locationCalls) {
                PsiElement[] args = locationCall.getCallArguments();
                if (args.length != 1 || !(args[0] instanceof Perl6StrLiteral))
                    continue;
                pathPieces.add(((Perl6StrLiteral)args[0]).getStringText());
            }
        }
        pathPieces.add(".");
        return pathPieces;
    }

    protected abstract List<? extends GotoRelatedItem> getFiles(Perl6StrLiteral element);

    @NotNull
    abstract protected String getCallName();
}
