package org.raku.comma.cro;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.RakuIcons;
import org.raku.comma.contribution.Filtering;
import org.raku.comma.extensions.RakuFrameworkCall;
import org.raku.comma.psi.*;
import org.raku.comma.psi.stub.RakuSubCallStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

@InternalIgnoreDependencyViolation
public class CroFrameworkCall extends RakuFrameworkCall {
    private static final Set<String> ROUTE_VERBS;

    static {
        ROUTE_VERBS = new HashSet<>();
        ROUTE_VERBS.addAll(Arrays.asList("get", "put", "post", "delete", "patch"));
    }

    @Override
    public String getFrameworkName() {
        return "Cro Router";
    }

    @Override
    public boolean isApplicable(RakuSubCall call) {
        // We can't resolve the symbol at indexing time to check if it's coming from
        // the Cro HTTP router, so we just go on callee name and it having a single
        // sub argument for now.
        String calleeName = call.getCallName();
        return ROUTE_VERBS.contains(calleeName) && getRouteSignature(call) != null;
    }

    private static RakuParameter[] getRouteSignature(RakuSubCall call) {
        RakuPointyBlock pblock = PsiTreeUtil.getChildOfType(call, RakuPointyBlock.class);
        if (pblock != null)
            return pblock.getParams();
        RakuRoutineDecl routine = PsiTreeUtil.getChildOfType(call, RakuRoutineDecl.class);
        if (routine != null)
            return routine.getParams();
        return null;
    }

    @Override
    public Map<String, String> getFrameworkData(RakuSubCall call) {
        StringBuilder buffer = new StringBuilder();
        RakuParameter[] params = getRouteSignature(call);
        Map<String, String> result = new HashMap<>();
        if (params != null) {
            for (RakuParameter param : params)
                renderParameter(buffer, param);
            result.put("method", call.getCallName());
            result.put("path", buffer.length() == 0 ? "/" : buffer.toString());
        }
        return result;
    }

    private static void renderParameter(StringBuilder buffer, RakuParameter param) {
        // We'll only deal with positional parameters, which are part of the
        // route path.
        if (param.isPositional()) {
            buffer.append("/");
            String varName = param.getVariableName();
            boolean haveVarName = varName != null && !varName.isEmpty();
            if (param.isSlurpy()) {
                buffer.append("{");
                if (haveVarName)
                    buffer.append(varName);
                buffer.append("*}");
            }
            else if (haveVarName) {
                buffer.append("{");
                buffer.append(varName);
                if (param.isOptional())
                    buffer.append("?");
                buffer.append("}");
            }
            else {
                RakuPsiElement value = param.getValueConstraint();
                if (value instanceof RakuStrLiteral) {
                    buffer.append(((RakuStrLiteral)value).getStringText());
                }
                else {
                    buffer.append("<unknown>");
                }
            }
        }
    }

    @Override
    public void indexStub(RakuSubCallStub stub, Map<String, String> frameworkData, IndexSink sink) {
        sink.occurrence(CroIndexKeys.CRO_ROUTES, frameworkData.get("path"));
    }

    @Override
    public void contributeSymbolNames(Project project, List<String> results) {
        results.addAll(CroRouteIndex.getInstance().getAllKeys(project));
    }

    @Override
    public void contributeSymbolItems(Project project, String pattern, List<NavigationItem> results) {
        CroRouteIndex routeIndex = CroRouteIndex.getInstance();
        Filtering.simpleMatch(routeIndex.getAllKeys(project), pattern).forEach(route ->
            results.addAll(StubIndex.getElements(routeIndex.getKey(),
                                                 route,
                                                 project,
                                                 GlobalSearchScope.projectScope(project),
                                                 RakuSubCall.class))
        );
    }

    @Override
    public ItemPresentation getNavigatePresentation(RakuPsiElement call, Map<String, String> frameworkData) {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                return frameworkData.get("method").toUpperCase(Locale.ENGLISH) + " " + frameworkData.get("path");
            }

            @Nullable
            @Override
            public String getLocationString() {
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
                return frameworkData.get("method").toUpperCase(Locale.ENGLISH) + " " + frameworkData.get("path");
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return RakuIcons.CRO;
            }
        };
    }
}
