package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.component.Perl6NameCache;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.Perl6ExternalSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Perl6UseStatementImpl extends ASTWrapperPsiElement implements Perl6UseStatement {
    public Perl6UseStatementImpl(@NotNull ASTNode node) {
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
                for (Perl6PsiDeclaration export : file.getExports()) {
                    export.contributeSymbols(collector);
                    if (collector.isSatisfied())
                        return;
                }
            }
            else {
                for (String sym : project.getComponent(Perl6NameCache.class).getNames(project, name)) {
                    collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Variable, sym));
                    if (sym.startsWith("&"))
                        collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Routine, sym.substring(1)));
                }
            }
        }
    }
}
