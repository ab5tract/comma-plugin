package edument.perl6idea.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6StrLiteral;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.external.ExternalPerl6File;
import edument.perl6idea.psi.impl.Perl6SubCallImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GotoTemplateFileProvider extends GotoRelatedProvider {
    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof Perl6StrLiteral))
            return Collections.emptyList();
        psiElement = psiElement.getParent();

        Perl6SubCall call = PsiTreeUtil.getParentOfType(psiElement, Perl6SubCall.class);
        if (call == null || !Objects.equals("template", call.getCallName()))
            return Collections.emptyList();

        @Nullable Perl6SubCallName name = ((Perl6SubCallImpl)call).getSubCallNameNode();
        @Nullable PsiReference ref = name == null ? null : name.getReference();
        if (name == null || !(ref instanceof PsiReferenceBase.Poly))
            return Collections.emptyList();

        ResolveResult @NotNull [] realRoutine = ((PsiReferenceBase.Poly<?>)ref).multiResolve(true);
        if (realRoutine.length != 0) {
            for (ResolveResult result : realRoutine) {
                if (result.getElement() != null && result.getElement().getParent() instanceof ExternalPerl6File &&
                    ((ExternalPerl6File)result.getElement().getParent()).getName().startsWith("Cro::WebApp::Template")) {
                    return getFiles((Perl6StrLiteral)psiElement);
                }
            }
        }
        return Collections.emptyList();
    }

    @NotNull
    private static List<GotoRelatedItem> getFiles(@NotNull Perl6StrLiteral psiElement) {
        // Take last part of the path
        String pathToTemplate = psiElement.getStringText();
        // We need to gather all available `template-location` calls in current `route` call if any
        PsiElement routeCall = psiElement;
        while (routeCall != null) {
            routeCall = PsiTreeUtil.getParentOfType(routeCall, Perl6SubCall.class);
            if (routeCall != null && ((Perl6SubCall)routeCall).getCallName().equals("route")) {
                break;
            }
        }
        List<String> pathPieces = new ArrayList<>();
        pathPieces.add(".");
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

        // Get root path for this source root that will be used to
        VirtualFile vf = psiElement.getContainingFile().getVirtualFile();
        @Nullable VirtualFile sourceRoot = ProjectRootManager.getInstance(psiElement.getProject()).getFileIndex().getSourceRootForFile(vf);
        @Nullable VirtualFile searchRoot = sourceRoot == null ? null : sourceRoot.getParent();
        if (sourceRoot == null || searchRoot == null) {
            PsiFile[] files = FilenameIndex
                .getFilesByName(psiElement.getProject(), pathToTemplate, GlobalSearchScope.projectScope(psiElement.getProject()));
            return files.length == 0
                   ? Collections.emptyList()
                   : ContainerUtil.map(files, f -> new GotoRelatedItem(f, "Cro::WebApp Template"));
        }
        else {
            List<GotoRelatedItem> items = new ArrayList<>();
            assert searchRoot.getCanonicalPath() != null;
            Path basePath = Paths.get(searchRoot.getCanonicalPath());
            for (String pathPiece : pathPieces) {
                Path templatePath = basePath.resolve(Paths.get(pathPiece, pathToTemplate));
                @Nullable VirtualFile templateVirtualFile = LocalFileSystem.getInstance().findFileByNioFile(templatePath);
                if (templateVirtualFile != null) {
                    PsiElement templatePsi = psiElement.getManager().findFile(templateVirtualFile);
                    if (templatePsi != null)
                        items.add(new GotoRelatedItem(templatePsi, "Cro::WebApp Template"));
                }
            }
            return items;
        }
    }
}
