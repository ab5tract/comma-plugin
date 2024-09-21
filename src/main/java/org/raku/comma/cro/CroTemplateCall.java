package org.raku.comma.cro;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubIndex;
import org.raku.comma.RakuIcons;
import org.raku.comma.contribution.Filtering;
import org.raku.comma.extensions.RakuFrameworkCall;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.RakuStrLiteral;
import org.raku.comma.psi.RakuSubCall;
import org.raku.comma.psi.stub.RakuSubCallStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@InternalIgnoreDependencyViolation
public class CroTemplateCall extends RakuFrameworkCall {
    @Override
    public String getFrameworkName() {
        return "Cro Templates";
    }

    @Override
    public boolean isApplicable(RakuSubCall call) {
        return Objects.equals(call.getCallName(), "template-part") &&
               call.getCallArguments().length > 0 &&
               call.getCallArguments()[0] instanceof RakuStrLiteral;
    }

    @Override
    public Map<String, String> getFrameworkData(RakuSubCall call) {
        Map<String, String> result = new HashMap<>();
        result.put("name", ((RakuStrLiteral)call.getCallArguments()[0]).getStringText());
        return result;
    }

    @Override
    public void indexStub(RakuSubCallStub stub, Map<String, String> frameworkData, IndexSink sink) {
        sink.occurrence(CroIndexKeys.CRO_TEMPLATE, frameworkData.get("name"));
    }

    @Override
    public void contributeSymbolNames(Project project, List<String> results) {
        results.addAll(CroTemplateIndex.getInstance().getAllKeys(project));
    }

    @Override
    public void contributeSymbolItems(Project project, String pattern, List<NavigationItem> results) {
        CroTemplateIndex routeIndex = CroTemplateIndex.getInstance();
        Filtering.simpleMatch(routeIndex.getAllKeys(project), pattern).forEach(route ->
            results.addAll(StubIndex.getElements(routeIndex.getKey(),
                                                 route,
                                                 project,
                                                 GlobalSearchScope.projectScope(project),
                                                 RakuSubCall.class)));
    }

    @Override
    public ItemPresentation getNavigatePresentation(RakuPsiElement call, Map<String, String> frameworkData) {
        return new ItemPresentation() {
            @Override
            public @NlsSafe @NotNull String getPresentableText() {
                return frameworkData.get("name");
            }

            @Override
            public @NlsSafe @Nullable String getLocationString() {
                return call.getEnclosingRakuModuleName();
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return RakuIcons.CRO;
            }
        };
    }

    @Override
    public ItemPresentation getStructureViewPresentation(RakuPsiElement call, Map<String, String> frameworkData) {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                return frameworkData.get("name");
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return RakuIcons.CRO;
            }
        };
    }
}
