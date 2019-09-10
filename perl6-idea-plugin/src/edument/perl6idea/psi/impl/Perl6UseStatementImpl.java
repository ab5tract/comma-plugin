package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.psi.Perl6PsiDeclaration;
import edument.perl6idea.psi.Perl6UseStatement;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.stub.Perl6UseStatementStubElementType;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Perl6UseStatementImpl extends StubBasedPsiElementBase<Perl6UseStatementStub> implements Perl6UseStatement {
    public Perl6UseStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6UseStatementImpl(Perl6UseStatementStub stub, Perl6UseStatementStubElementType type) {
        super(stub, type);
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        String name = getModuleName();
        if (name != null) {
            Project project = getProject();

            // We cannot contribute based on stubs when indexing is in progress
            if (DumbService.isDumb(getProject())) return;

            Collection<Perl6File> found = ProjectModulesStubIndex.getInstance()
                    .get(name, project, GlobalSearchScope.projectScope(project));
            if (found.size() > 0) {
                Perl6File file = found.iterator().next();
                for (Perl6PsiDeclaration export : file.getExports()) {
                    if (export instanceof Perl6LexicalSymbolContributor) {
                        ((Perl6LexicalSymbolContributor)export).contributeLexicalSymbols(collector);
                        if (collector.isSatisfied())
                            return;
                    }
                }
                Set<String> seen = new HashSet<>();
                seen.add(name);
                file.contributeGlobals(collector, seen);
                if (collector.isSatisfied())
                    return;
            }
            else {
                Perl6File file = Perl6SdkType.getInstance().getPsiFileForModule(project, name, getText());
                if (file != null) {
                    file.contributeGlobals(collector, new HashSet<>());
                    if (collector.isSatisfied())
                        return;
                }
            }

            // Workaround for https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000437020-Usage-of-StringStubIndexExtension-during-unit-testing
            // Quite a slow manual method which does not use stubs, that we hardly fallback to "in real life"
            Collection <VirtualFile> slowFound = FileTypeIndex.getFiles(
                Perl6ModuleFileType.INSTANCE,
                GlobalSearchScope.projectScope(getProject()));

            String newName = String.format(
                "lib/%s.%s",
                name.replaceAll("::", "/"), Perl6ModuleFileType.INSTANCE.getDefaultExtension()
            );
            for (VirtualFile file : slowFound) {
                String path = file.getCanonicalPath();
                if (path == null) continue;
                if (!path.endsWith(newName)) continue;
                PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(file);
                if (psiFile instanceof Perl6File) {
                    for (Perl6PsiDeclaration export : ((Perl6File)psiFile).getExports()) {
                        if (export instanceof Perl6LexicalSymbolContributor) {
                            ((Perl6LexicalSymbolContributor)export).contributeLexicalSymbols(collector);
                            if (collector.isSatisfied())
                                return;
                        }
                    }
                    Set<String> seen = new HashSet<>();
                    seen.add(name);
                    ((Perl6File)psiFile).contributeGlobals(collector, seen);
                }
            }
        }
    }

    @Nullable
    @Override
    public String getModuleName() {
        Perl6UseStatementStub stub = getStub();
        if (stub != null)
            return stub.getModuleName();

        Perl6ModuleName moduleName = findChildByClass(Perl6ModuleName.class);
        return moduleName != null ? moduleName.getText() : null;
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:USE_STATEMENT)";
    }
}
