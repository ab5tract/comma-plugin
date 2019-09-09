package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.psi.Perl6NeedStatement;
import edument.perl6idea.psi.stub.Perl6NeedStatementStub;
import edument.perl6idea.psi.stub.Perl6NeedStatementStubElementType;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Perl6NeedStatementImpl extends StubBasedPsiElementBase<Perl6NeedStatementStub> implements Perl6NeedStatement {
    public Perl6NeedStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6NeedStatementImpl(Perl6NeedStatementStub stub, Perl6NeedStatementStubElementType type) {
        super(stub, type);
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        // We cannot contribute based on stubs when indexing is in progress
        if (DumbService.isDumb(getProject())) return;
        for (String name : getModuleNames()) {
            Project project = getProject();
            Collection<Perl6File> found = ProjectModulesStubIndex.getInstance()
                    .get(name, project, GlobalSearchScope.projectScope(project));
            if (found.size() > 0) {
                Perl6File file = found.iterator().next();
                Set<String> seen = new HashSet<>();
                seen.add(name);
                file.contributeGlobals(collector, seen);
            }
            else {
                Perl6File file = Perl6SdkType.getInstance().getPsiFileForModule(project, "need", name);
                if (file != null) {
                    file.contributeGlobals(collector, new HashSet<>());
                }
            }
        }
    }

    @Override
    public List<String> getModuleNames() {
        Perl6NeedStatementStub stub = getStub();
        if (stub != null)
            return stub.getModuleNames();

        List<String> result = new ArrayList<>();
        for (Perl6ModuleName moduleName : findChildrenByClass(Perl6ModuleName.class))
            result.add(moduleName.getText());
        return result;
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:NEED_STATEMENT)";
    }
}
