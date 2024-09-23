package org.raku.comma.psi;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.pod.PodDomBuildingContext;
import org.raku.comma.psi.effects.Effect;
import org.raku.comma.psi.effects.EffectCollection;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static org.raku.comma.parsing.RakuTokenTypes.UNV_WHITE_SPACE;

public interface RakuPsiElement extends NavigatablePsiElement {
    /* Name-manages the enclosing file name into a module name, if possible.
     * Returns null if that's not possible or this doesn't seem to be a module. */
    default String getEnclosingRakuModuleName() {
        // Make sure it's Raku module file, and trim the extension.
        VirtualFile file = getContainingFile().getVirtualFile();
        if (!(FileTypeManager.getInstance().getFileTypeByFile(file) instanceof RakuModuleFileType))
            return null;
        String path = file.getPath();
        String extension = file.getExtension();
        path = path.substring(0, path.length() - (extension == null ? 0 : extension.length() + 1));

        // Make sure it's within the project and trim the project path
        // off the start.
        String projectPath = getProject().getBasePath();
        if (projectPath == null)
            return null;
        if (!path.startsWith(projectPath))
            return null;
        path = path.substring(projectPath.length() + 1);

        // Mangle it, removing a leading lib:: since lib/ is the standard place
        // for libraries.
        String libraryName = path.replaceAll("[/\\\\]", "::");
        return StringUtil.trimStart(libraryName, "lib::");
    }

    default RakuSymbol resolveLexicalSymbol(RakuSymbolKind kind, String name) {
        RakuSingleResolutionSymbolCollector collector = new RakuSingleResolutionSymbolCollector(name, kind);
        applyLexicalSymbolCollector(collector);
        return collector.getResult();
    }

    @NotNull
    default List<RakuSymbol> resolveLexicalSymbolAllowingMulti(RakuSymbolKind kind, String name) {
        RakuSingleResolutionSymbolCollector collector = new RakuSingleResolutionSymbolCollector(name, kind);
        applyLexicalSymbolCollector(collector);
        return collector.getResults();
    }

    default Collection<RakuSymbol> getLexicalSymbolVariants(RakuSymbolKind... kinds) {
        RakuVariantsSymbolCollector collector = new RakuVariantsSymbolCollector(kinds);
        applyLexicalSymbolCollector(collector);
        return collector.getVariants();
    }

    default void applyExternalSymbolCollector(RakuSymbolCollector collector) {
        RakuPsiScope scope = PsiTreeUtil.getParentOfType(this, RakuPsiScope.class);
        while (scope != null) {
            // If we are at top level already, we need to contribute CORE external symbols too
            if (scope instanceof RakuFile) {
                scope.contributeScopeSymbols(collector);
            }

            RakuStatementList list = PsiTreeUtil.findChildOfType(scope, RakuStatementList.class);
            if (list == null) return;
            RakuStatement[] stats = PsiTreeUtil.getChildrenOfType(list, RakuStatement.class);
            // Just go one level up, skipping the for loop below
            if (stats == null) stats = new RakuStatement[0];
            for (RakuStatement statement : stats) {
                // Do not iterate further If we already passed current element
                if (statement.getTextOffset() > getTextOffset()) break;
                for (PsiElement maybeImport : statement.getChildren()) {
                    if (!(maybeImport instanceof RakuUseStatement || maybeImport instanceof RakuNeedStatement)) continue;
                    RakuLexicalSymbolContributor cont = (RakuLexicalSymbolContributor)maybeImport;
                    cont.contributeLexicalSymbols(collector);
                    if (collector.isSatisfied()) return;
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, RakuPsiScope.class);
        }
    }

    default void applyLexicalSymbolCollector(RakuSymbolCollector collector) {
        RakuPsiScope scope = PsiTreeUtil.getParentOfType(this, RakuPsiScope.class);
        while (scope != null) {
            for (RakuLexicalSymbolContributor cont : scope.getSymbolContributors()) {
                cont.contributeLexicalSymbols(collector);
                if (collector.isSatisfied()) return;
            }
            scope.contributeScopeSymbols(collector);
            if (collector.isSatisfied()) return;
            scope = PsiTreeUtil.getParentOfType(scope, RakuPsiScope.class);
        }
    }

    @NotNull
    default RakuType inferType() {
        return RakuUntyped.INSTANCE;
    }

    default RakuPackageDecl getSelfType() {
        // There's only a self type if we're inside of a method or in the declaration of
        // an attribute.
        RakuPsiElement current = this;
        boolean foundSelfProvider = false;
        while (current != null) {
            current = PsiTreeUtil.getParentOfType(current, RakuRoutineDecl.class, RakuRegexDecl.class, RakuPackageDecl.class, RakuVariableDecl.class);
            if (current instanceof RakuPackageDecl)
                return foundSelfProvider ? (RakuPackageDecl)current : null;
            if (foundSelfProvider)
                return null;
            if (current instanceof RakuRoutineDecl) {
                String scope = ((RakuRoutineDecl)current).getScope();
                if (scope.equals("has"))
                    foundSelfProvider = true;
            }
            else if (current instanceof RakuRegexDecl) {
                String scope = ((RakuRegexDecl)current).getScope();
                if (scope.equals("has"))
                    foundSelfProvider = true;
            }
            else if (current instanceof RakuVariableDecl) {
                String scope = ((RakuVariableDecl)current).getScope();
                if (scope.equals("has"))
                    foundSelfProvider = true;
            }
        }
        return null;
    }

    @Nullable
    default PsiElement skipWhitespacesBackward() {
        PsiElement temp = getPrevSibling();
        while (temp != null
            && (temp instanceof PsiWhiteSpace || temp.getNode().getElementType() == UNV_WHITE_SPACE))
        {
            temp = temp.getPrevSibling();
        }
        return temp;
    }

    @Nullable
    default PsiElement skipWhitespacesForward() {
        PsiElement temp = getNextSibling();
        while (temp != null
            && (temp instanceof PsiWhiteSpace || temp.getNode().getElementType() == UNV_WHITE_SPACE))
        {
            temp = temp.getNextSibling();
        }
        return temp;
    }

    default void collectPodAndDocumentables(PodDomBuildingContext context) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof RakuPsiElement) {
                ((RakuPsiElement) child).collectPodAndDocumentables(context);
            }
            child = child.getNextSibling();
        }
    }

    @NotNull
    default EffectCollection inferEffects() {
        return EffectCollection.of(Effect.IMPURE);
    }
}
