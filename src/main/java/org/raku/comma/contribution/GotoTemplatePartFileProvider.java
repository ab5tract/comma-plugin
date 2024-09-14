package org.raku.comma.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.cro.template.psi.CroTemplatePart;
import org.raku.comma.cro.template.psi.stub.index.CroTemplateStubIndexKeys;
import org.raku.comma.psi.RakuStrLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GotoTemplatePartFileProvider extends CallArgGotoElementProviderBase {
    @NotNull
    @Override
    protected List<GotoRelatedItem> getFiles(@NotNull RakuStrLiteral psiElement) {
        List<String> pathPieces = calculatePathPieces(psiElement);

        // Get root path for this source root that will be used to
        VirtualFile vf = psiElement.getContainingFile().getVirtualFile();
        @Nullable VirtualFile sourceRoot = ProjectRootManager.getInstance(psiElement.getProject()).getFileIndex().getSourceRootForFile(vf);
        @Nullable VirtualFile searchRoot = sourceRoot == null ? null : sourceRoot.getParent();
        Collection<CroTemplatePart> parts = StubIndex.getElements(CroTemplateStubIndexKeys.TEMPLATE_PART,
                                                                  psiElement.getStringText(), psiElement.getProject(),
                                                                  GlobalSearchScope.projectScope(psiElement.getProject()),
                                                                  CroTemplatePart.class);
        if (sourceRoot == null || searchRoot == null) {
            return parts.isEmpty()
                   ? Collections.emptyList()
                   : ContainerUtil.map(parts, f -> new GotoRelatedItem(f, "Cro::WebApp Template"));
        }
        else {
            List<GotoRelatedItem> items = new ArrayList<>();
            String canonicalSearchRootPath = searchRoot.getCanonicalPath();
            assert canonicalSearchRootPath != null;
            Path basePath = Paths.get(canonicalSearchRootPath);
            for (String pathPiece : pathPieces) {
                Path templatePath = basePath.resolve(Paths.get(pathPiece)).toAbsolutePath().normalize();
                for (CroTemplatePart part : parts) {
                    PsiFile containingFile = part.getContainingFile();
                    String path = containingFile.getVirtualFile().getCanonicalPath();
                    if (path != null && Paths.get(path).startsWith(templatePath)) {
                        items.add(new GotoRelatedItem(part, "Cro::WebApp::Template"));
                    }
                }
            }
            return items;
        }
    }

    @Override
    protected @NotNull String getCallName() {
        return "template-part";
    }
}
