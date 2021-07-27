package edument.perl6idea.cro;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IndexSink;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.contribution.Filtering;
import edument.perl6idea.extensions.Perl6FrameworkCall;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6StrLiteral;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.stub.Perl6SubCallStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CroTemplateCall extends Perl6FrameworkCall {
    @Override
    public String getFrameworkName() {
        return "Cro Templates";
    }

    @Override
    public boolean isApplicable(Perl6SubCall call) {
        return Objects.equals(call.getCallName(), "template-part") &&
               call.getCallArguments().length > 0 &&
               call.getCallArguments()[0] instanceof Perl6StrLiteral;
    }

    @Override
    public Map<String, String> getFrameworkData(Perl6SubCall call) {
        Map<String, String> result = new HashMap<>();
        result.put("name", ((Perl6StrLiteral)call.getCallArguments()[0]).getStringText());
        return result;
    }

    @Override
    public void indexStub(Perl6SubCallStub stub, Map<String, String> frameworkData, IndexSink sink) {
        sink.occurrence(CroIndexKeys.CRO_TEMPLATE, frameworkData.get("name"));
    }

    @Override
    public void contributeSymbolNames(Project project, List<String> results) {
        results.addAll(CroTemplateIndex.getInstance().getAllKeys(project));
    }

    @Override
    public void contributeSymbolItems(Project project, String pattern, List<NavigationItem> results) {
        CroTemplateIndex routeIndex = CroTemplateIndex.getInstance();
        for (String route : Filtering.simpleMatch(routeIndex.getAllKeys(project), pattern))
            results.addAll(routeIndex.get(route, project, GlobalSearchScope.projectScope(project)));
    }

    @Override
    public ItemPresentation getNavigatePresentation(Perl6PsiElement call, Map<String, String> frameworkData) {
        return new ItemPresentation() {
            @Override
            public @NlsSafe @NotNull String getPresentableText() {
                return frameworkData.get("name");
            }

            @Override
            public @NlsSafe @Nullable String getLocationString() {
                return call.getEnclosingPerl6ModuleName();
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return Perl6Icons.CRO;
            }
        };
    }

    @Override
    public ItemPresentation getStructureViewPresentation(Perl6PsiElement call, Map<String, String> frameworkData) {
        return new ItemPresentation() {
            @Override
            public @NlsSafe @NotNull String getPresentableText() {
                return frameworkData.get("name");
            }

            @Override
            public @NlsSafe @Nullable String getLocationString() {
                return null;
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return Perl6Icons.CRO;
            }
        };
    }
}
