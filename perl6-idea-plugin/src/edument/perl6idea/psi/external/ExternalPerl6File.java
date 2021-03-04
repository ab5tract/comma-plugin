package edument.perl6idea.psi.external;

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
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class ExternalPerl6File implements Perl6File {
    private Project myProject;
    private FileViewProvider myViewProvider;
    private VirtualFile myFile;
    private List<Perl6Symbol> mySymbols = new ArrayList<>();

    public ExternalPerl6File(Project project,
                             VirtualFile file) {
        myProject = project;
        myViewProvider = PsiManager.getInstance(project).findViewProvider(file);
        myFile = file;
    }

    @Override
    public boolean isReal() {
        return false;
    }

    public void setSymbols(List<Perl6Symbol> symbols) {
        mySymbols = symbols;
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        contributeGlobals(collector, new HashSet<>());
    }

    @Override
    public void contributeGlobals(Perl6SymbolCollector collector, Set<String> seen) {
        for (Perl6Symbol symbol : mySymbols) {
            if (symbol.getKind() == Perl6SymbolKind.Routine) {
                Perl6RoutineDecl psi = (Perl6RoutineDecl)symbol.getPsi();
                if (psi.getMultiness().equals("only")) {
                    collector.offerSymbol(symbol);
                } else {
                    collector.offerMultiSymbol(symbol, false);
                }
            } else {
                collector.offerSymbol(symbol);
            }
            if (collector.isSatisfied())
                return;
        }
    }

    @Override
    public Map<Integer, List<Integer>> getStatementLineMap() {
        return new HashMap<>();
    }

    @Override
    public VirtualFile getVirtualFile() {
        return myFile;
    }

    @Override
    public boolean processChildren(PsiElementProcessor<? super PsiFileSystemItem> processor) {
        return false;
    }

    @Override
    public PsiDirectory getContainingDirectory() {
        return null;
    }

    @NotNull
    @Override
    public Project getProject() throws PsiInvalidElementAccessException {
        return myProject;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return Perl6Language.INSTANCE;
    }

    @Override
    public PsiManager getManager() {
        return PsiManager.getInstance(myProject);
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public PsiDirectory getParent() {
        return null;
    }

    @Override
    public PsiElement getFirstChild() {
        return null;
    }

    @Override
    public PsiElement getLastChild() {
        return null;
    }

    @Override
    public PsiElement getNextSibling() {
        return null;
    }

    @Override
    public PsiElement getPrevSibling() {
        return null;
    }

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return this;
    }

    @Override
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public int getStartOffsetInParent() {
        return 0;
    }

    @Override
    public int getTextLength() {
        return 0;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int offset) {
        return null;
    }

    @Override
    public int getTextOffset() {
        return 0;
    }

    @Override
    public String getText() {
        return null;
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return new char[0];
    }

    @Override
    public PsiElement getNavigationElement() {
        return null;
    }

    @Override
    public PsiElement getOriginalElement() {
        return null;
    }

    @Override
    public boolean textMatches(@NotNull CharSequence text) {
        return false;
    }

    @Override
    public boolean textMatches(@NotNull PsiElement element) {
        return false;
    }

    @Override
    public boolean textContains(char c) {
        return false;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {

    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor visitor) {

    }

    @Override
    public PsiElement copy() {
        return this;
    }

    @Override
    public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement addRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor)
        throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkDelete() throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Nullable
    @Override
    public PsiReference getReference() {
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return PsiReference.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(Key<T> key) {
        return null;
    }

    @Override
    public <T> void putCopyableUserData(Key<T> key, @Nullable T value) {

    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       @Nullable PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return false;
    }

    @Nullable
    @Override
    public PsiElement getContext() {
        return null;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @NotNull
    @Override
    public GlobalSearchScope getResolveScope() {
        return GlobalSearchScope.projectScope(myProject);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return GlobalSearchScope.projectScope(myProject);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @NotNull
    @Override
    public PsiFile getOriginalFile() {
        return this;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Perl6ModuleFileType.INSTANCE;
    }

    @NotNull
    @Override
    public PsiFile[] getPsiRoots() {
        return PsiFile.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public FileViewProvider getViewProvider() {
        return myViewProvider;
    }

    @Override
    public FileASTNode getNode() {
        return null;
    }

    @Override
    public String toString() {
        return myFile.getName();
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        return false;
    }

    @Override
    public void subtreeChanged() {

    }

    @Override
    public void clearCaches() {
    }

    @Override
    public void checkSetName(String name) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return myFile.getName();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return null;
    }

    @Override
    public void navigate(boolean requestFocus) {

    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        return null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Icon getIcon(int flags) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }
}
