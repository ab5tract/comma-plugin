package org.raku.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import org.raku.psi.RakuStrLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GotoTemplateFileProvider extends CallArgGotoElementProviderBase {
    @NotNull
    @Override
    protected List<GotoRelatedItem> getFiles(@NotNull RakuStrLiteral psiElement) {
        List<String> pathPieces = calculatePathPieces(psiElement);

        // Get root path for this source root that will be used to
        VirtualFile vf = psiElement.getContainingFile().getVirtualFile();
        @Nullable VirtualFile sourceRoot = ProjectRootManager.getInstance(psiElement.getProject()).getFileIndex().getSourceRootForFile(vf);
        @Nullable VirtualFile searchRoot = sourceRoot == null ? null : sourceRoot.getParent();
        if (sourceRoot == null || searchRoot == null) {
            PsiFile[] files = FilenameIndex
                .getFilesByName(psiElement.getProject(), psiElement.getStringText(), GlobalSearchScope.projectScope(psiElement.getProject()));
            return files.length == 0
                   ? Collections.emptyList()
                   : ContainerUtil.map(files, f -> new GotoRelatedItem(f, "Cro::WebApp Template"));
        }
        else {
            List<GotoRelatedItem> items = new ArrayList<>();
            assert searchRoot.getCanonicalPath() != null;
            Path basePath = Paths.get(searchRoot.getCanonicalPath());
            for (String pathPiece : pathPieces) {
                Path templatePath = basePath.resolve(Paths.get(pathPiece, psiElement.getStringText()));
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

    @Override
    protected @NotNull String getCallName() {
        return "template";
    }
}
