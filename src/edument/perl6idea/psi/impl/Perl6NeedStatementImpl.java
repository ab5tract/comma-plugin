package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.psi.Perl6NeedStatement;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Perl6NeedStatementImpl extends ASTWrapperPsiElement implements Perl6NeedStatement {
    public Perl6NeedStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        Perl6ModuleName moduleName = findChildByClass(Perl6ModuleName.class);
        if (moduleName != null) {
            String name = moduleName.getText();
            Project project = getProject();
            Collection<Perl6File> found = ProjectModulesStubIndex.getInstance()
                    .get(name, project, GlobalSearchScope.projectScope(project));
            if (found.size() > 0) {
                Perl6File file = found.iterator().next();
                file.contributeGlobals(collector);
            }
            else {
                for (Perl6Symbol sym : Perl6SdkType.getInstance().getNamesForNeed(project, name)) {
                    collector.offerSymbol(sym);
                    if (collector.isSatisfied())
                        return;
                }
            }
        }
    }
}
