package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.SlowOperations;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6UseStatement;
import edument.perl6idea.psi.external.ExternalPerl6File;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.stub.Perl6UseStatementStubElementType;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.Perl6SingleResolutionSymbolCollector;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

            SlowOperations.allowSlowOperations(() -> {
                Collection<Perl6File> found =
                    ProjectModulesStubIndex.getInstance().get(name, project, GlobalSearchScope.projectScope(project));
                if (found.size() > 0) {
                    Perl6File file = found.iterator().next();
                    file.contributeGlobals(collector, new HashSet<>());
                    contributeEXPORT(collector, file);
                    Set<String> seen = new HashSet<>();
                    seen.add(name);
                    file.contributeGlobals(collector, seen);
                }
                else {
                    Collection<Perl6File> elements =
                        StubIndex.getElements(Perl6StubIndexKeys.PROJECT_MODULES, name, project, GlobalSearchScope.allScope(project),
                                              Perl6File.class);
                    if (!elements.isEmpty()) {
                        elements.iterator().next().contributeGlobals(collector, new HashSet<>());
                    }

                    if (collector.isSatisfied())
                        return;

                    Perl6File file = Perl6SdkType.getInstance().getPsiFileForModule(project, name, getText());
                    if (file != null) {
                        file.contributeGlobals(collector, new HashSet<>());
                    }
                }
            });
        }
    }

    private void contributeEXPORT(Perl6SymbolCollector collector, Perl6File file) {
        if (file.getFirstChild() instanceof Perl6PsiElement) {
            Perl6Symbol symbol = ((Perl6PsiElement)file.getFirstChild()).resolveLexicalSymbol(Perl6SymbolKind.Routine, "EXPORT");
            // If we have a custom EXPORT, try to evaluate the file
            if (symbol != null) {
                LightVirtualFile dummy = new LightVirtualFile(file.getName());
                ExternalPerl6File perl6File = new ExternalPerl6File(getProject(), dummy);
                String invocation = "use " + file.getEnclosingPerl6ModuleName();
                List<Perl6Symbol> symbols = Perl6SdkType.loadModuleSymbols(getProject(), perl6File, file.getName(),
                                                                           invocation,
                                                                           new HashMap<>(), true);
                for (Perl6Symbol perl6Symbol : symbols) {
                    collector.offerSymbol(perl6Symbol);
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
