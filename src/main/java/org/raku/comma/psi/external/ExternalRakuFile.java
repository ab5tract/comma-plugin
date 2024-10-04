package org.raku.comma.psi.external;

import com.intellij.lang.FileASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.RakuLanguage;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.symbols.RakuSymbol;
import org.raku.comma.psi.symbols.RakuSymbolCollector;
import org.raku.comma.psi.symbols.RakuSymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class ExternalRakuFile implements RakuFile {
    private final Project myProject;
    private final FileViewProvider myViewProvider;
    private final VirtualFile myFile;
    private List<RakuSymbol> mySymbols = new ArrayList<>();
    private String moduleName;
    private String originalPath;

    public ExternalRakuFile(Project project, VirtualFile file) {
        myProject = project;
        myViewProvider = PsiManager.getInstance(project).findViewProvider(file);
        myFile = file;
    }

    @Override
    public boolean isReal() {
        return false;
    }

    @Override
    public String renderPod() {
        // No Pod from external files
        return "";
    }

    @Override
    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    @Override
    public String getOriginalPath() {
        return originalPath;
    }

        @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSymbols(List<RakuSymbol> symbols) {
        mySymbols = symbols;
    }

    @Override
    public void contributeScopeSymbols(RakuSymbolCollector collector) {
        contributeGlobals(collector, new HashSet<>());
    }

    @Override
    public void contributeGlobals(RakuSymbolCollector collector, Set<String> seen) {
        for (RakuSymbol symbol : mySymbols) {
            if (symbol.getKind() == RakuSymbolKind.Routine) {
                RakuRoutineDecl psi = (RakuRoutineDecl)symbol.getPsi();
                if (psi.getMultiness().equals("only")) {
                    collector.offerSymbol(symbol);
                } else {
                    collector.offerMultiSymbol(symbol, false);
                }
            } else {
                collector.offerSymbol(symbol);
            }
            if (collector.isSatisfied()) {
                return;
            }
        }
    }


    @NotNull
    @Override
    public Language getLanguage() {
        return RakuLanguage.INSTANCE;
    }

    // WHY HAVE INHERITANCE ONLY TO FORCE SUBCLASSES TO OVERRIDE A THOUSAND SUBS WITH THE SAME DEFINITION PRESENT IN THE SUPER CLASS????
    @Override public Map<Integer, List<Integer>> getStatementLineMap() { return new HashMap<>(); }
    @Override public VirtualFile getVirtualFile() { return myFile; }
    @Override public boolean processChildren(@NotNull PsiElementProcessor<? super PsiFileSystemItem> processor) { return false; }
    @Override public PsiDirectory getContainingDirectory() { return null; }
    @NotNull @Override public Project getProject() throws PsiInvalidElementAccessException { return myProject; }
    @Override public PsiManager getManager() { return PsiManager.getInstance(myProject); }
    @Override public PsiElement @NotNull [] getChildren() { return PsiElement.EMPTY_ARRAY; }
    @Override public PsiDirectory getParent() { return null; }
    @Override public PsiElement getFirstChild() { return null; }
    @Override public PsiElement getLastChild() { return null; }
    @Override public PsiElement getNextSibling() { return null; }
    @Override public PsiElement getPrevSibling() { return null; }
    @Override public PsiFile getContainingFile() throws PsiInvalidElementAccessException { return this; }
    @Override public TextRange getTextRange() { return null; }
    @Override public int getStartOffsetInParent() { return 0; }
    @Override public int getTextLength() { return 0; }
    @Nullable @Override public PsiElement findElementAt(int offset) { return null; }
    @Nullable @Override public PsiReference findReferenceAt(int offset) { return null; }
    @Override public int getTextOffset() { return 0; }
    @Override public String getText() { return null; }
    @Override public char @NotNull [] textToCharArray() { return new char[0]; }
    @Override public PsiElement getNavigationElement() { return null; }
    @Override public PsiElement getOriginalElement() { return null; }
    @Override public boolean textMatches(@NotNull CharSequence text) { return false; }
    @Override public boolean textMatches(@NotNull PsiElement element) { return false; }
    @Override public boolean textContains(char c) { return false; }
    @Override public void accept(@NotNull PsiElementVisitor visitor) { }
    @Override public void acceptChildren(@NotNull PsiElementVisitor visitor) { }
    @Override public PsiElement copy() { return this; }
    @Override public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public PsiElement addBefore(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public PsiElement addAfter(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public PsiElement addRange(PsiElement first, PsiElement last)
            throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor)
            throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor)
            throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public void delete() throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public void checkDelete() throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public void deleteChildRange(PsiElement first, PsiElement last)
            throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public PsiElement replace(@NotNull PsiElement newElement)
            throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public boolean isValid() { return true; }
    @Override public boolean isWritable() { return false; }
    @Nullable @Override public PsiReference getReference() { return null; }
    @Override public PsiReference @NotNull [] getReferences() { return PsiReference.EMPTY_ARRAY; }
    @Nullable @Override public <T> T getCopyableUserData(@NotNull Key<T> key) { return null; }
    @Override public <T> void putCopyableUserData(@NotNull Key<T> key, @Nullable T value) {}

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       @Nullable PsiElement lastParent,
                                       @NotNull PsiElement place) { return false; }

    @Nullable @Override public PsiElement getContext() { return null; }
    @Override public boolean isPhysical() { return false; }
    @NotNull @Override public GlobalSearchScope getResolveScope() { return GlobalSearchScope.projectScope(myProject); }
    @NotNull @Override public SearchScope getUseScope() { return GlobalSearchScope.projectScope(myProject); }
    @Override public boolean isDirectory() { return false; }
    @Override public long getModificationStamp() { return 0; }
    @NotNull @Override public PsiFile getOriginalFile() { return this; }
    @NotNull @Override public FileType getFileType() { return RakuModuleFileType.INSTANCE; }
    @Override public PsiFile @NotNull [] getPsiRoots() { return PsiFile.EMPTY_ARRAY; }
    @NotNull @Override public FileViewProvider getViewProvider() { return myViewProvider; }
    @Override public FileASTNode getNode() { return null; }
    @Override public String toString() { return myFile.getName(); }
    @Override public boolean isEquivalentTo(PsiElement another) { return false; }
    @Override public void subtreeChanged() { }
    @Override public void checkSetName(String name) throws IncorrectOperationException { throw new IncorrectOperationException(); }
    @Nullable @Override public PsiElement getNameIdentifier() { return null; }
    @NotNull @Override public String getName() { return myFile.getName(); }
    @Nullable @Override public ItemPresentation getPresentation() { return null; }
    @Override public void navigate(boolean requestFocus) { }
    @Nullable @Override public PsiMetaData getMetaData() { return null; }
    @Override public PsiElement setName(@NotNull String name) throws IncorrectOperationException { throw new UnsupportedOperationException(); }
    @Override public Icon getIcon(int flags) { return null; }
    @Nullable @Override public <T> T getUserData(@NotNull Key<T> key) { return null; }
    @Override public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) { }
}
