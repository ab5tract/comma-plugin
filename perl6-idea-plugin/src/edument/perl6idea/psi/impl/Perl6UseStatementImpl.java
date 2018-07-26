package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6UseStatementStub;
import edument.perl6idea.psi.stub.Perl6UseStatementStubElementType;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;

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
    public void contributeSymbols(Perl6SymbolCollector collector) {
        String name = getModuleName();
        if (name != null) {
            Project project = getProject();
            Collection<Perl6File> found = ProjectModulesStubIndex.getInstance()
                    .get(name, project, GlobalSearchScope.projectScope(project));
            if (found.size() > 0) {
                Perl6File file = found.iterator().next();
                for (Perl6PsiDeclaration export : file.getExports()) {
                    export.contributeSymbols(collector);
                    if (collector.isSatisfied())
                        return;
                }
                Set<String> seen = new HashSet<>();
                seen.add(name);
                file.contributeGlobals(collector, seen);
            }
            else {
                for (Perl6Symbol sym : Perl6SdkType.getInstance().getNamesForUse(project, name)) {
                    collector.offerSymbol(sym);
                    if (collector.isSatisfied())
                        return;
                }
            }
        }
    }

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